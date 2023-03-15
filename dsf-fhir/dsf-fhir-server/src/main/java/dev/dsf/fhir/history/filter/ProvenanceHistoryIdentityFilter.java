package dev.dsf.fhir.history.filter;

import org.hl7.fhir.r4.model.Provenance;

import ca.uhn.fhir.model.api.annotation.ResourceDef;
import dev.dsf.common.auth.conf.Identity;
import dev.dsf.fhir.search.filter.ProvenanceIdentityFilter;

public class ProvenanceHistoryIdentityFilter extends ProvenanceIdentityFilter implements HistoryIdentityFilter
{
	private static final String RESOURCE_TYPE = Provenance.class.getAnnotation(ResourceDef.class).name();

	public ProvenanceHistoryIdentityFilter(Identity identity)
	{
		super(identity, HistoryIdentityFilter.RESOURCE_TABLE, HistoryIdentityFilter.RESOURCE_ID_COLUMN);
	}

	@Override
	public String getFilterQuery()
	{
		return HistoryIdentityFilter.getFilterQuery(RESOURCE_TYPE, super.getFilterQuery());
	}
}
