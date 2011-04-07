package hu.bme.mit.toolintegration.workflow.jbpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import eu.sensoria_ist.casetool.core.ISensoriaCore;
import eu.sensoria_ist.casetool.core.SensoriaCoreRegistry;
import eu.sensoria_ist.casetool.core.tools.Parameter;
import eu.sensoria_ist.casetool.core.tools.api.IFunction;
import eu.sensoria_ist.casetool.core.tools.api.ITool;


public class CallToolActionHandler implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4267912002631319521L;

	/**
	 * required for jBPM...
	 */

	String coreId;

	String toolId;

	String functionId;

	

	/**
	 * Key-value pair for parameter names and values. The key is the name of the
	 * variable as declared in the function you wish to call. The String list is
	 * the set of variables in the execution context that should be given to the
	 * function.
	 */
	Map<String,String> inputParams;
	Map<String,String> outputParams;

	private ExecutionContext executionContext;
	
	
	
	
	public CallToolActionHandler() {
		
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jbpm.graph.def.ActionHandler#execute(org.jbpm.graph.exe.ExecutionContext
	 * )
	 */
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		try {
			this.executionContext = executionContext;
			ISensoriaCore core = findCore ();
			ITool tool = findTool(core);
			IFunction function = findFunction(tool);
			Object [] arguments= calculateArguments(function);
			
			Object result = function.invoke(arguments);
			processResult (result);
			
			clearInputVariables();
//			// review this solution
//			//if a node doesn't have any leaving transition the current flow ends (returns)
//			@SuppressWarnings("rawtypes")
//			List leavingTr = executionContext.getNode().getLeavingTransitions();
//			if (leavingTr == null ? true :( leavingTr.size() == 0)){
//				return;
//			}
			executionContext.getNode().leave(executionContext);
		}catch (Exception e){
			executionContext.getProcessInstance().end();
			Logger.logError(e.getMessage(), e);
			throw new Exception ("Error during invoking service "+functionId+" in tool "+toolId + ". Sensoria core was: "+(coreId == null ? "LOCAL" : coreId),e);
		}
	}

	


	
	private void clearInputVariables() {
		for (String paramName : inputParams.keySet()){
			executionContext.getContextInstance().deleteTransientVariable(inputParams.get(paramName));
		}
	}

	private ISensoriaCore findCore() throws Exception {
		SensoriaCoreRegistry cReg = SensoriaCoreRegistry.getInstance();
		ISensoriaCore core;
		if (coreId == null || coreId.isEmpty()){
			if (toolId.split("#").length == 2 ){
				core = cReg.getCore(toolId.split("#")[0]);
			}else {
				core = SensoriaCoreRegistry.getLocalCore();
			}
		}
		else{
			core = cReg.getCore(coreId);
		}
		
		if (core == null) 
			throw new Exception ("Cannot find coreId: "+coreId);
		return core;
	}

	private ITool findTool(ISensoriaCore core) throws Exception {
		if (toolId == null || toolId.isEmpty()){
			throw new Exception ("ToolId was null or empty");
		}
		ITool tool = null;
		if (toolId.split("#").length == 2 ){
			tool =  core.getTools().findToolById(toolId.split("#")[1]);
		} else {
			tool =  core.getTools().findToolById(toolId);
		}

		if (tool == null) 
			throw new Exception ("Cannot find tool: "+toolId);
		return tool;
	}


	private IFunction findFunction(ITool tool) throws Exception{
		if (functionId == null || functionId.isEmpty()){
			throw new Exception ("FunctionId was null or empty");
		}
		IFunction function = null;
		
		for (IFunction f : tool.getFunctions()){
			if (functionId.equals(f.getSimpleName()) ){
				function = f;
			}
		}
		
		if (function == null) 
			throw new Exception ("Cannot find function with name "+functionId+ ", toolId ("+toolId+")");
		return function;
		
	}

	private Object[] calculateArguments(IFunction function) throws Exception{
		List<Object> args= new ArrayList<Object>();
		
		for (Parameter p : function.getParameters()){
			Object parValue = getParameterValueFromContext( p.getName());
			args.add(parValue);
		}
		
		return args.toArray();
	}

	private Object getParameterValueFromContext(String parName) throws Exception {
		String variableName = inputParams.get(parName);
		if (variableName == null || variableName.isEmpty()){
			throw new Exception ("Cannot find JPDL variable represents the "+parName+ "parameter in function "+functionId+ ", toolId ("+toolId+")");
		}
		//Map m =executionContext.getContextInstance().getTransientVariables();
		boolean existsVariable = executionContext.getContextInstance().hasTransientVariable(variableName);
		if (!existsVariable){
			throw new Exception ("JPDL variable ("+variableName+") represents the "+parName+ "parameter in function "+functionId+ ", toolId ("+toolId+") does not exist.");
		}
		//the value may be null
		Object value = executionContext.getContextInstance().getTransientVariable(variableName);
		
		return value;
	}

	

	private void processResult(Object result) {
		if (result == null) return;
		if (outputParams.size() == 1){
			String paramName = outputParams.keySet().iterator().next();
			String variableName = outputParams.get(paramName);
			executionContext.getContextInstance().setTransientVariable(variableName, result);
			
		}
	}


}
