package hu.bme.mit.toolintegration.workflow.jbpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class MoveVariableActionHandler implements ActionHandler {

	String sourceVar;
	String targetVar;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2428231170679003819L;
												  

	@Override
	public void execute(ExecutionContext ec) throws Exception {
		Object varToMove = ec.getContextInstance().getTransientVariable(sourceVar);
		ec.getContextInstance().deleteTransientVariable(sourceVar);
		ec.getContextInstance().setTransientVariable(targetVar, varToMove);
		ec.getNode().leave(ec);
	}

}
