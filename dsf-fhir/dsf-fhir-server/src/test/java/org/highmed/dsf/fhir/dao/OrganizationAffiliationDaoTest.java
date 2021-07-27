package org.highmed.dsf.fhir.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.highmed.dsf.fhir.dao.jdbc.OrganizationAffiliationDaoJdbc;
import org.highmed.dsf.fhir.dao.jdbc.OrganizationDaoJdbc;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.OrganizationAffiliation;
import org.junit.Test;

public class OrganizationAffiliationDaoTest
		extends AbstractResourceDaoTest<OrganizationAffiliation, OrganizationAffiliationDao>
		implements ReadAccessDaoTest<OrganizationAffiliation>
{
	private static final String identifierSystem = "http://highmed.org/sid/organization-identifier";
	private static final String identifierValue = "identifier.test";
	private static final boolean active = true;

	private final OrganizationDao organizationDao = new OrganizationDaoJdbc(defaultDataSource, fhirContext);

	public OrganizationAffiliationDaoTest()
	{
		super(OrganizationAffiliation.class, OrganizationAffiliationDaoJdbc::new);
	}

	@Override
	public OrganizationAffiliation createResource()
	{
		OrganizationAffiliation organizationAffiliation = new OrganizationAffiliation();
		organizationAffiliation.addIdentifier().setSystem(identifierSystem).setValue(identifierValue);
		return organizationAffiliation;
	}

	@Override
	protected void checkCreated(OrganizationAffiliation resource)
	{
		assertTrue(resource.hasIdentifier());
		assertEquals(identifierSystem, resource.getIdentifierFirstRep().getSystem());
		assertEquals(identifierValue, resource.getIdentifierFirstRep().getValue());
	}

	@Override
	protected OrganizationAffiliation updateResource(OrganizationAffiliation resource)
	{
		resource.setActive(active);
		return resource;
	}

	@Override
	protected void checkUpdates(OrganizationAffiliation resource)
	{
		assertEquals(active, resource.getActive());
	}

	@Test
	public void testReadActiveNotDeletedByMemberOrganizationIdentifier() throws Exception
	{
		final String parentIdentifier = "parent.org";

		try (Connection connection = getDao().newReadWriteTransaction())
		{
			Organization memberOrg = new Organization();
			memberOrg.setActive(true);
			memberOrg.addIdentifier().setSystem(identifierSystem).setValue(identifierValue);

			Organization parentOrg = new Organization();
			parentOrg.setActive(true);
			parentOrg.addIdentifier().setSystem(identifierSystem).setValue(parentIdentifier);

			Organization createdMemberOrg = organizationDao.createWithTransactionAndId(connection, memberOrg,
					UUID.randomUUID());

			Organization createdParentOrg = organizationDao.createWithTransactionAndId(connection, parentOrg,
					UUID.randomUUID());

			OrganizationAffiliation affiliation = new OrganizationAffiliation();
			affiliation.setActive(true);
			affiliation.getParticipatingOrganization()
					.setReference("Organization/" + createdMemberOrg.getIdElement().getIdPart());
			affiliation.getOrganization().setReference("Organization/" + createdParentOrg.getIdElement().getIdPart());

			OrganizationAffiliation createdAffiliation = getDao().createWithTransactionAndId(connection, affiliation,
					UUID.randomUUID());

			List<OrganizationAffiliation> affiliations = getDao()
					.readActiveNotDeletedByMemberOrganizationIdentifierIncludingOrganizationIdentifiersWithTransaction(
							connection, identifierValue);
			assertNotNull(affiliations);
			assertEquals(1, affiliations.size());
			assertEquals(createdAffiliation.getIdElement().getIdPart(), affiliations.get(0).getIdElement().getIdPart());
			assertTrue(affiliations.get(0).hasParticipatingOrganization());
			assertTrue(affiliations.get(0).getParticipatingOrganization().hasReference());
			assertEquals("Organization/" + createdMemberOrg.getIdElement().getIdPart(),
					affiliations.get(0).getParticipatingOrganization().getReference());
			assertTrue(affiliations.get(0).getParticipatingOrganization().hasIdentifier());
			assertTrue(affiliations.get(0).getParticipatingOrganization().getIdentifier().hasSystem());
			assertEquals(identifierSystem,
					affiliations.get(0).getParticipatingOrganization().getIdentifier().getSystem());
			assertTrue(affiliations.get(0).getParticipatingOrganization().getIdentifier().hasValue());
			assertEquals(identifierValue,
					affiliations.get(0).getParticipatingOrganization().getIdentifier().getValue());
			assertTrue(affiliations.get(0).hasOrganization());
			assertTrue(affiliations.get(0).getOrganization().hasReference());
			assertEquals("Organization/" + createdParentOrg.getIdElement().getIdPart(),
					affiliations.get(0).getOrganization().getReference());
			assertTrue(affiliations.get(0).getOrganization().hasIdentifier());
			assertTrue(affiliations.get(0).getOrganization().getIdentifier().hasSystem());
			assertEquals(identifierSystem, affiliations.get(0).getOrganization().getIdentifier().getSystem());
			assertTrue(affiliations.get(0).getOrganization().getIdentifier().hasValue());
			assertEquals(parentIdentifier, affiliations.get(0).getOrganization().getIdentifier().getValue());
		}
	}

	@Override
	@Test
	public void testReadAccessTriggerAll() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerAll();
	}

	@Override
	@Test
	public void testReadAccessTriggerLocal() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerLocal();
	}

	@Override
	@Test
	public void testReadAccessTriggerOrganization() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerOrganization();
	}

	@Override
	@Test
	public void testReadAccessTriggerOrganizationResourceFirst() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerOrganizationResourceFirst();
	}

	@Override
	@Test
	public void testReadAccessTriggerOrganization2Organizations1Matching() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerOrganization2Organizations1Matching();
	}

	@Override
	@Test
	public void testReadAccessTriggerOrganization2Organizations2Matching() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerOrganization2Organizations2Matching();
	}

	@Override
	@Test
	public void testReadAccessTriggerRole() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRole();
	}

	@Override
	@Test
	public void testReadAccessTriggerRoleResourceFirst() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRoleResourceFirst();
	}

	@Override
	@Test
	public void testReadAccessTriggerRole2Organizations1Matching() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRole2Organizations1Matching();
	}

	@Override
	@Test
	public void testReadAccessTriggerRole2Organizations2Matching() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRole2Organizations2Matching();
	}

	@Override
	@Test
	public void testReadAccessTriggerAllUpdate() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerAllUpdate();
	}

	@Override
	@Test
	public void testReadAccessTriggerLocalUpdate() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerLocalUpdate();
	}

	@Override
	@Test
	public void testReadAccessTriggerOrganizationUpdate() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerOrganizationUpdate();
	}

	@Override
	@Test
	public void testReadAccessTriggerRoleUpdate() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRoleUpdate();
	}

	@Override
	@Test
	public void testReadAccessTriggerRoleUpdateMemberOrganizationNonActive() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRoleUpdateMemberOrganizationNonActive();
	}

	@Override
	@Test
	public void testReadAccessTriggerRoleUpdateParentOrganizationNonActive() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRoleUpdateParentOrganizationNonActive();
	}

	@Override
	@Test
	public void testReadAccessTriggerRoleUpdateMemberAndParentOrganizationNonActive() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerRoleUpdateMemberAndParentOrganizationNonActive();
	}

	@Override
	@Test
	public void testReadAccessTriggerAllDelete() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerAllDelete();
	}

	@Override
	@Test
	public void testReadAccessTriggerLocalDelete() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerLocalDelete();
	}

	@Override
	@Test
	public void testReadAccessTriggerOrganizationDelete() throws Exception
	{
		ReadAccessDaoTest.super.testReadAccessTriggerOrganizationDelete();
	}

	@Override
	@Test
	public void testSearchWithUserFilterAfterReadAccessTriggerAll() throws Exception
	{
		ReadAccessDaoTest.super.testSearchWithUserFilterAfterReadAccessTriggerAll();
	}

	@Override
	@Test
	public void testSearchWithUserFilterAfterReadAccessTriggerLocal() throws Exception
	{
		ReadAccessDaoTest.super.testSearchWithUserFilterAfterReadAccessTriggerLocal();
	}
}
