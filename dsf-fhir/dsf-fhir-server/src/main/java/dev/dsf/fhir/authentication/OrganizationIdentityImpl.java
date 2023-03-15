package dev.dsf.fhir.authentication;

import java.security.cert.X509Certificate;
import java.util.Set;

import org.hl7.fhir.r4.model.Organization;

import dev.dsf.common.auth.conf.OrganizationIdentity;
import dev.dsf.common.auth.conf.Role;

// TODO implement equals, hashCode, toString methods based on the DSF organization identifier to fully comply with the java.security.Principal specification
public class OrganizationIdentityImpl extends AbstractIdentity implements OrganizationIdentity
{
	/**
	 * @param localIdentity
	 *            <code>true</code> if this is a local identity
	 * @param organization
	 *            not <code>null</code>
	 * @param roles
	 *            may be <code>null</code>
	 * @param certificate
	 *            may be <code>null</code>
	 */
	public OrganizationIdentityImpl(boolean localIdentity, Organization organization, Set<? extends Role> roles,
			X509Certificate certificate)
	{
		super(localIdentity, organization, roles, certificate);
	}

	@Override
	public String getName()
	{
		return getOrganizationIdentifierValue();
	}

	@Override
	public String getDisplayName()
	{
		return getOrganization() != null && getOrganization().hasName() ? getOrganization().getName() : "";
	}
}
