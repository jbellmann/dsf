package org.highmed.dsf.bpe.service;

import static org.highmed.dsf.bpe.Constants.MIN_PARTICIPATING_MEDICS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.highmed.dsf.bpe.Constants;
import org.highmed.dsf.bpe.delegate.AbstractServiceDelegate;
import org.highmed.dsf.bpe.variables.SimpleCohortSizeResult;
import org.highmed.dsf.fhir.task.TaskHelper;
import org.highmed.dsf.bpe.variables.MultiInstanceResult;
import org.highmed.dsf.fhir.variables.OutputWrapper;
import org.highmed.fhir.client.FhirWebserviceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class CheckSingleMedicFeasibilityResults extends AbstractServiceDelegate
{
	private static final Logger logger = LoggerFactory.getLogger(CheckSingleMedicFeasibilityResults.class);

	public CheckSingleMedicFeasibilityResults(FhirWebserviceClient webserviceClient, TaskHelper taskHelper)
	{
		super(webserviceClient, taskHelper);
	}

	@Override
	public void doExecute(DelegateExecution execution) throws Exception
	{
		List<OutputWrapper> outputs = (List<OutputWrapper>) execution.getVariable(Constants.VARIABLE_PROCESS_OUTPUTS);
		MultiInstanceResult results = (MultiInstanceResult) execution
				.getVariable(Constants.VARIABLE_MULTI_INSTANCE_RESULT);

		Map<String, String> finalResults = results.getQueryResults();
		Map<String, String> erroneousResults = checkQueryResults(finalResults);
		finalResults.keySet().removeAll(erroneousResults.keySet());

		// TODO: more checks

		OutputWrapper errorOutput = getOutputWrapperErroneous(erroneousResults);
		outputs.add(errorOutput);
		OutputWrapper successOutput = getOutputWrapperSuccessful(finalResults);
		outputs.add(successOutput);

		execution.setVariable(Constants.VARIABLE_PROCESS_OUTPUTS, outputs);
	}

	private Map<String, String> checkQueryResults(Map<String, String> queryResults)
	{
		Map<String, String> toRemove = new HashMap<>();

		queryResults.forEach((groupId, result) -> {
			// TODO implement check
		});

		return toRemove;
	}

	private OutputWrapper getOutputWrapperErroneous(Map<String, String> erroneousResults)
	{
		OutputWrapper outputWrapper = new OutputWrapper(Constants.CODESYSTEM_HIGHMED_BPMN);

		erroneousResults.forEach((groupId, result) -> {
			logger.error(
					"Final single medic feasibility query result check failed for group with id '{}', reason unknown",
					groupId);

			outputWrapper.addKeyValue(Constants.CODESYSTEM_HIGHMED_BPMN_VALUE_ERROR_MESSAGE,
					"Final single medic feasibility query result check failed for group with id '" + groupId
							+ "', reason unknown");
		});

		return outputWrapper;
	}

	private OutputWrapper getOutputWrapperSuccessful(Map<String, String> successfulResults)
	{
		OutputWrapper outputWrapper = new OutputWrapper(Constants.NAMINGSYSTEM_HIGHMED_FEASIBILITY);

		successfulResults.forEach((groupId, result) -> {
			outputWrapper.addKeyValue(Constants.NAMINGSYSTEM_HIGHMED_FEASIBILITY_VALUE_PREFIX_SINGLE_RESULT
							+ groupId, result);
		});

		return outputWrapper;
	}
}
