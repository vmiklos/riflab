package hu.bme.mit.toolintegration.workflow.jbpm;

import hu.bme.mit.toolintegration.workflow.decisionhandler.WorkflowDecisionHandler;

import java.util.Map;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

public class GeneralDecisionHandler implements DecisionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 932204366731951315L;
	Map<String,String> outputTansitionMapping;
	String decisionInputVarName;
	String decisionBody;
	String language;
	
	
	@Override
	public String decide(ExecutionContext executionContext) throws Exception {
		WorkflowDecisionHandler hnd = new WorkflowDecisionHandler();
		Object decisionInput = executionContext.getContextInstance().getTransientVariable(decisionInputVarName);
		
		//decisionInput may be null;
		String result = hnd.evaluateDecision(language, decisionBody, decisionInput);
		
		String mapToTransitionName = outputTansitionMapping.get(result);
		
		if (mapToTransitionName == null){
			throw new Exception("Cannot find the chosen branch: '"+result+"'. Decision node: "+decisionInputVarName);
		}
		return mapToTransitionName;
	}

}
