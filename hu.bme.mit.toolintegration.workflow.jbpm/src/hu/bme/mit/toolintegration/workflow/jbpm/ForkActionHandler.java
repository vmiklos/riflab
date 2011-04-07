package hu.bme.mit.toolintegration.workflow.jbpm;

import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.SensoriaEventActionHandler;

import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

public class ForkActionHandler implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1751915765968973464L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		Node currentNode = executionContext.getNode();
		String currentNodeName = currentNode.getName();
		
		
		
		
		//if there is a transient variable named currentNodeName in context, this fork is an ObjectFlowFork
		//else this is a ContrloFlowFork
		//Map trVar =  executionContext.getContextInstance().getTransientVariables();
		boolean isObjectFork = executionContext.getContextInstance().hasTransientVariable(currentNodeName);
		Object forkData = null;
		if (isObjectFork){
			forkData = executionContext.getContextInstance().getTransientVariable(currentNodeName);
		}
		Token rootToken = executionContext.getProcessInstance().getRootToken();
		
		@SuppressWarnings("unchecked")
		List<Transition> leavingTransitions = currentNode.getLeavingTransitions();
		
		
		int lastIndex = leavingTransitions.size()-1;
		
		
		//In this case the fork dosn't have any leaving transition
		if (lastIndex<0){
			//TODO throw Exception or simple end this flow?
			//the return will end the current flow
			return;
		}
		for (int i = 0; i<lastIndex;i++){
			if (isObjectFork){
				executionContext.getContextInstance().setTransientVariable(currentNodeName,forkData);
			}
			Token newToken = new Token(rootToken,"token"+rootToken.getChildren().size());
			

			ExecutionContext childExecutionContext = new ExecutionContext(newToken);
			newToken.getNode().fireAndPropagateEvent(
					SensoriaEventActionHandler.EVENTTYPE_TOKEN_CREATED,
					childExecutionContext);
			childExecutionContext.leaveNode(leavingTransitions.get(i));		      
		}
		
		//don't create a new Token for last transition
		if (isObjectFork){
			executionContext.getContextInstance().setTransientVariable(currentNodeName,forkData);
		}
		//except if the token is the root token
		if (rootToken.equals(executionContext.getToken())){
			Token newToken = new Token(rootToken,"token"+rootToken.getChildren().size());
			ExecutionContext childExecutionContext = new ExecutionContext(newToken);
			newToken.getNode().fireAndPropagateEvent(
					SensoriaEventActionHandler.EVENTTYPE_TOKEN_CREATED,
					childExecutionContext);
			childExecutionContext.leaveNode(leavingTransitions.get(lastIndex));		  
		}
		else {
			executionContext.leaveNode(leavingTransitions.get(lastIndex));
		}
	}

}
