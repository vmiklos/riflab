package hu.bme.mit.toolintegration.workflow.jbpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.sensoria_ist.casetool.core.ext.ISensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunction;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunctionParameter;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunctionReturns;

@SensoriaTool(categories = "Workflow management", 
		description = "A tool for running jBPM processes, that can access the Core Registry", 
		name = "jBPM Executor Tool")
public interface IJbpmExecutorTool extends ISensoriaTool {

//********************************************************

	@SensoriaToolFunction(description = "Executes a process archive.")
	@SensoriaToolFunctionParameter(description = "The absolute path that points to the prcess archive")
	
	public void executeProcessArchive(String path);

	@SensoriaToolFunction(description= "Executes an instance of the process defined as an XML String supplied in the parameter")
	@SensoriaToolFunctionParameter(description="XML document as a String")
	public void executeProcessDefinition(String xmlProcessDefinition);
	
	@SensoriaToolFunction(description="Clear the database - drop and recreate the schema")
	public void clearDatabase();
	
	@SensoriaToolFunction(description="Creates the database schema according to the default configuration file.")
	public void createSchema();
	//********************************************************
	
	@SensoriaToolFunction(description = "Deploys a process archive.")
	@SensoriaToolFunctionParameter(description = "The absolute path that points to the prcess archive")
	public int deployProcessArchive(String path);
	
	@SensoriaToolFunction(description = "Executes a process instance.")
	
	public void executeProcessInstance(
			@SensoriaToolFunctionParameter(description = "The processInstanceID") String processID,
			@SensoriaToolFunctionParameter(description = "VariableMap(key: varName, value: varValue as a string)") HashMap<String,String> variableMap
	) throws Exception;
	
	@SensoriaToolFunction(description = "Creates a new process instance.")
	@SensoriaToolFunctionParameter(description = "The processDefinitionName")
	public int createNewProcessInstance(String processDefinitionName,String processDefinitionId, String processInstanceName);
	
	@SensoriaToolFunction(description = "Creates or extends a parameter map for process instances with a new variable name - variable value pair.")
	@SensoriaToolFunctionReturns(description="Parameter map for process instances containing actual values for parameters.")
	public HashMap<String,String> addParameterToMap(
			@SensoriaToolFunctionParameter(description = "The map where the new variable name - variable value pair shall be added. If it is null, a new map will be created.") 
			HashMap<String,String> map, 
			@SensoriaToolFunctionParameter(description = "The name of a variable as defined in a process definition as the variableName attribute of a DataNode.")
			String variableName, 
			@SensoriaToolFunctionParameter(description = "The actual value for the variable with name variableName.")
			String variableValue);
	//********************************************************
	@SensoriaToolFunction(description = "Creates a new process instance.")
	public HashMap<String,String> createProcessInstance(String processDefinitionId, String processInstanceName,
			@SensoriaToolFunctionParameter(description = "VariableMap(key: varName, value: varValue as a string)") HashMap<String,String> variableMap);
	
	@SensoriaToolFunction(description = "Deletes process instance.")
	public boolean deleteProcessInstance(String processInstanceId);
	
	@SensoriaToolFunction(description = "Deletes process definition and all instances of the definition.")
	public boolean deleteProcessDefinition(String processDefinitionId);
	
	@SensoriaToolFunction(description = "hh")
	public HashMap<String,String> deployProcessDefinition(String jPDLSourceString) throws Exception;
	
	@SensoriaToolFunction(description = "hh")
	public HashMap<String,String> deployProcessDefinitionWithGPDId(String jPDLSourceString,String GPDId) throws Exception;
	
	@SensoriaToolFunction(description = "hh")
	public ArrayList<HashMap<String,String>> getAllProcessDefinitions();
	@SensoriaToolFunction(description = "hh")
	public ArrayList<HashMap<String,String>> getAllProcessInstances();
}
