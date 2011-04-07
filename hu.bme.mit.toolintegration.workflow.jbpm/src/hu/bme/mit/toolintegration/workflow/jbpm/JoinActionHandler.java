package hu.bme.mit.toolintegration.workflow.jbpm;

import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.SensoriaEventActionHandler;

import java.util.List;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

public class JoinActionHandler implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4505168991281557479L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		
		
		Node currentNode = executionContext.getNode();
		
		Token rootToken = executionContext.getProcessInstance().getRootToken();
		@SuppressWarnings("unchecked")
		List<Token> tokensAtNode = rootToken.getChildrenAtNode(currentNode);
		
		Token token = executionContext.getToken();
		//all tokens arrived in
		if (tokensAtNode.size() == currentNode.getArrivingTransitions().size()){
			//remove ended tokens from node
//			for (Token t : tokensAtNode){
//				if (!t.equals(token)){
//					rootToken.getChildren().remove(t.getName());
//				}
//			}
			//and propagate last arrived
			currentNode.leave(executionContext);
		}
		else {
			token.end();
			currentNode.fireAndPropagateEvent(
					SensoriaEventActionHandler.EVENTTYPE_TOKEN_REMOVED,
					executionContext);
		}
	}

}
