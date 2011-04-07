package hu.bme.mit.toolintegration.workflow.jbpm;

import hu.bme.mit.toolintegration.workflow.jbpm.dataflow.DataNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import eu.sensoria_ist.casetool.core.ISensoriaCore;
import eu.sensoria_ist.casetool.core.SensoriaCoreRegistry;
import eu.sensoria_ist.casetool.core.tools.Parameter;
import eu.sensoria_ist.casetool.core.tools.api.IFunction;
import eu.sensoria_ist.casetool.core.tools.api.ITool;

/**
 * ActionHandler implementation for invoking Sensoria Tools. The class should be
 * instantiated using the field method, providing at least the tool Id and the
 * functionId strings. The coreId defaults to the LOCAL_CORE_ID (which
 * identifies the default local core). Parameter and return types and names
 * default to an empty list (a void function with no arguments).
 * 
 * @author adam
 * 
 */
public class SensoriaActionHandler implements ActionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4267912002631319521L;

	/**
	 * required for jBPM...
	 */
	String message;

	String coreId;

	String toolId;

	String functionId;

	String returnType;

	String resultName;

	/**
	 * Key-value pair for parameter names and values. The key is the name of the
	 * variable as declared in the function you wish to call. The String list is
	 * the set of variables in the execution context that should be given to the
	 * function.
	 */
	Map<String,String> params;
	
	List<String> inputDataNodes;
	List<String> outputDataNodes;
	
	public SensoriaActionHandler() {
		
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
		executeInputDataNodes(executionContext);
		initializeOutputDatanodes(executionContext);
		
		for (Object o: params.keySet()){
			String s= (String) o;
			String s2= (String) params.get(s);
			System.out.println(s+"  " +s2);
		}
	
		if (checkParameters()) {
			SensoriaCoreRegistry cReg = SensoriaCoreRegistry.getInstance();
			ISensoriaCore core = cReg.getCore(coreId);
			if (core != null) {
				ITool tool = core.getTools().findToolById(toolId);
				if (tool != null) {
					List<IFunction> functions = tool.getFunctions();
					// find functions, that are suitable for calling with the
					// given parameter types
					IFunction goodFunction = null;
					for (IFunction function : functions) {
						if (function.getSimpleName().equals(functionId)) {
							if (checkFunctionParameters(function,
									executionContext)) {
								goodFunction = function;
								break;
							}

						}
					}
					if (goodFunction != null) {
						List<Object> arguments = new ArrayList<Object>();
						for (Parameter param : goodFunction.getParameters()) 
						{
							String valueStr = (String)params.get(param.getName());
							if (valueStr.startsWith("#"))
							{
								// treat it as a variable reference
								Object arg = executionContext.getContextInstance().getVariable(valueStr.substring(1));
								if (arg == null) arg = executionContext.getContextInstance().getTransientVariable((valueStr.substring(1)));
								
								arguments.add(arg);
							}
							else
							{
								// try to pass it as a primitive type
								// TODO: add try-casts to primitive types such as Integer, boolean, ...
								try {
									if ((param.getTypeCanonicalName().equals("int")) || (param.getTypeCanonicalName().equalsIgnoreCase("java.lang.Integer")))
										arguments.add(Integer.valueOf(valueStr));
									else
										arguments.add(valueStr);
								} catch (Exception e) {
									SensoriaCoreRegistry
									.logInfo("Parameter '" + valueStr + "' in function '" + functionId
											+ "' cannot be casted to type " + param.getTypeCanonicalName());								}
							}
						}
						Object result = goodFunction
								.invoke(arguments.toArray());
						
						if (result != null) {
							//executionContext.setVariable(resultName, result);
							ProcessInstance processInstance = executionContext.getProcessInstance();
							ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
							contextInstance.setTransientVariable(resultName, result);
						}
						executeOutputDatanodes(executionContext);
//						System.out.println("Executed node: " + executionContext.getNode().getName());
						

					} else
						SensoriaCoreRegistry
								.logInfo("Function with id '"
										+ functionId
										+ "' was not found with the parameter names and types supplied.");
				} else
					SensoriaCoreRegistry.logInfo("Tool with id '" + toolId
							+ "' was not found");
			} else
				SensoriaCoreRegistry.logInfo("Core with id '" + coreId
						+ "' was not found");
		} else
			SensoriaCoreRegistry
					.logWarning("Tool id or function id was not present when trying to invoke Sensoria function.");
		
		// propagate execution
		executionContext.getNode().leave(executionContext);
	}

	private void executeOutputDatanodes(ExecutionContext ec) {
		if (outputDataNodes == null) return;
		@SuppressWarnings("unchecked")
		Map<String,Object> nameMap = ec.getProcessDefinition().getNodesMap();
		for (String name: outputDataNodes){
			Object o = nameMap.get(name);
			if (o instanceof DataNode){
				DataNode d = (DataNode) o;
				d.executeAsOutput(ec);
				//System.out.println(d.getName() + " "+d.getVariableName());
			}
		}
		
	}

	private void initializeOutputDatanodes(ExecutionContext ec) {
		if (outputDataNodes == null) return;
		@SuppressWarnings("unchecked")
		Map<String,Object> nameMap = ec.getProcessDefinition().getNodesMap();
		
		for (String name : outputDataNodes){
			Object o = nameMap.get(name);
			if (o instanceof DataNode){
				DataNode d = (DataNode) nameMap.get(name);
				d.initializeAsOutput(ec);
				//System.out.println(d.getName() + " "+d.getVariableName());
			} 
		}
		
	}

	private void executeInputDataNodes(ExecutionContext ec) {
		if (inputDataNodes == null) return;
		@SuppressWarnings("unchecked")
		Map<String,Object>  nameMap = ec.getProcessDefinition().getNodesMap();
		for (String name: inputDataNodes){
			Object o = nameMap.get(name);
			if (o instanceof DataNode){
				DataNode d = (DataNode) nameMap.get(name);
				d.executeAsInput(ec);
				//System.out.println(d.getName() + " "+d.getVariableName());
			} 
		}
		
	}

	protected boolean checkParameters() {
		if (coreId == null || coreId.isEmpty()) {
			// default to local core
			coreId = SensoriaCoreRegistry.LOCAL_CORE_ID;
		}
		if (toolId == null || toolId.isEmpty())
			return false;
		if (functionId == null || functionId.isEmpty())
			return false;
		if (params == null)
			params = new HashMap<String, String>();
		return true;
	}

	protected boolean checkFunctionParameters(IFunction function,
			ExecutionContext executionContext) {
		List<Parameter> specifiedParams = function.getParameters();

		// check number of parameters
		if (specifiedParams.size() != params.keySet().size())
			return false;

		for (Parameter parameter : specifiedParams) {
			// check if the parameter was given
			if (!params.containsKey(parameter.getName())) {
				return false;
			} else {
				// get variable names that should be assigned to this calling
				// parameter
				String valueStr = (String)params.get(parameter.getName());
				if (valueStr.startsWith("#"))
				{
					String varName = valueStr.substring(1);
					// check if the variable exists in the root context
					if (executionContext.getContextInstance().hasVariable(varName)
							|| executionContext.getContextInstance().hasTransientVariable(varName)) {
						// check type compatibility
						Object variable = executionContext.getContextInstance()
								.getVariable(varName);
						
						if (variable == null) variable = executionContext.getContextInstance().getTransientVariable(varName);
						if (variable == null)
							return true; // variable containing null value can be assigned to everything
						else if (parameter.getParameterType().isAssignableFrom(
								variable.getClass())) 
						{
							if (!checkReturnType(function))
								return false;
						} else
							return false;
					} else
						return false;
				}
				else
				{
					// we were given a static primitive
					if (parameter.getParameterType().equals(String.class))
					{
						return checkReturnType(function);
					}
					// TODO add casting support for more primitive types
				}
				
			}
		}
		return true;
	}

	private boolean checkReturnType(IFunction function)
	{
		// check return type
		if (returnType == null || returnType.isEmpty()) {
			if (function.getReturnTypeSimpleName().equals("void"))
				return true;
			else
				return false;
		} else {
			if (function.getReturnTypeSimpleName()
					.equals(returnType))
				return true;
			else
				return false;
		}
	}
	
}
