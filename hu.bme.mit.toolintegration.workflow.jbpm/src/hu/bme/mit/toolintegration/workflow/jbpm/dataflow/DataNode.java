package hu.bme.mit.toolintegration.workflow.jbpm.dataflow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.xml.JpdlXmlReader;

import eu.sensoria_ist.casetool.core.ISensoriaCore;
import eu.sensoria_ist.casetool.core.SensoriaCoreRegistry;
import eu.sensoria_ist.casetool.core.tools.api.IFunction;
import eu.sensoria_ist.casetool.core.tools.api.ITool;


public class DataNode extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = 602099535449710833L;
	//attributes
	/**
	 * Name of temporary folder, where repository files are downloaded
	 */
	protected static final String sdeTempFolderName = "SDETemp";
	/**
	 * Type of the data represented by this node
	 * (primitive, file)
	 */
	protected String dataType;
	/** Id to the identification */
	protected int dataNodeId;
	//child elements
	/**
	 * The name of the variable in jBPM context
	 */
	protected String variableName;
	/**
	 * The type/class of the variable
	 */
	protected String variableType;

	/**
	 * The value of the variable; 
	 * 	(file : URL)
	 * 	(primitive : the String representation of the variable)
	 */
	protected String variableValue;
	/**
	 * True if the variable is transient  
	 */
	protected boolean isTransient;
	
	protected boolean inputDataNode;
	protected String toolId;
	
	public DataNode (){};
	
	/*public void execute(ExecutionContext ec) { 
		if (dataType.compareToIgnoreCase("blackboard") == 0) executeBlackboard(ec);
		else executePrimitive(ec);
	}*/
	
	@Deprecated
	public void execute(ExecutionContext ec) {
		//nincs felülírás
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		//ha már elõttünk beállította vki
		if (contextInstance.getVariable(variableName) != null || contextInstance.getTransientVariable(variableName) != null ) return;
		
		
		if (inputDataNode ){
			if ((dataType.compareToIgnoreCase("file")==0)){
				executeInputFile(ec);
			}
			else {
				executeInputPrimitive(ec);
			}
		}
		else {
			resolveOutputVariable(ec);
			if ((dataType.compareToIgnoreCase("file")==0)){
				executeOutputFile(ec);
			}
		}
	}
	
	public void executeAsInput(ExecutionContext ec) {
		//nincs felülírás
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		//ha már elõttünk beállította vki
		if (contextInstance.getVariable(variableName) != null || contextInstance.getTransientVariable(variableName) != null ) return;
			
		if ((dataType.compareToIgnoreCase("file")==0)){
			executeInputFile(ec);
		}
		else {
			executeInputPrimitive(ec);
		}
	}
	
	public void executeAsOutput(ExecutionContext ec) {
		//nincs felülírás
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		//ha már elõttünk beállította vki
		if (contextInstance.getVariable(variableName) == null && contextInstance.getTransientVariable(variableName) == null ) 
			resolveOutputVariable(ec);
		
		if ((dataType.compareToIgnoreCase("file")==0)){
			executeOutputFile(ec);
		}
	}
	
	public void initializeAsOutput(ExecutionContext ec) {
		//nincs felülírás
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		//ha már elõttünk beállította vki
		if (contextInstance.getVariable(variableName) != null || contextInstance.getTransientVariable(variableName) != null ) return;
		
		if ((dataType.compareToIgnoreCase("file")==0)){
			initializeOutputFile(ec);
		}
	}
	
	
	
	protected void initializeVariableValue(ExecutionContext ec) {
		//
		String initialParameter = getParameterVariableIfExists(ec);
		if (initialParameter != null) variableValue = initialParameter;
		
		//
		if (variableValue.startsWith("#")){
			variableValue = (String) ec.getVariable(variableValue.substring(1));
		}
	
	}

	private void resolveOutputVariable(ExecutionContext ec) {
		//továbbadó szerep
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		
		//fontos hogy hivatkozhassunk korábban definiált változóra is
		Object variableToSet;
		if (variableValue.startsWith("#")){
			variableToSet = contextInstance.getVariable(variableValue.substring(1));
			if (variableToSet == null) variableToSet = contextInstance.getTransientVariable(variableValue.substring(1));
			//beállítjuk a nevet
			// TODO: megnézni, hogy létezik-e már
			if (isTransient){
				contextInstance.setTransientVariable(variableName, variableToSet);
			}
			else {
				contextInstance.setVariable(variableName, variableToSet);
			}
		}
	}

	
	/**
	 * Create empty file with name determined by the variableValue
	 * in process instance specific folder in the workspace
	 * or get a reference to it if it exists
	 * 
	 * @return IFile reference to the file
	 */
	protected IFile getTempIFile(ExecutionContext ec) {
		// determine the file name
		String[] pathFragments = variableValue.split("/");
		String filename = pathFragments[pathFragments.length-1];

		// get a ref to the file in process instance specific folder in the workspace
		ProcessInstance pi = ec.getProcessInstance();
		IProject sdeTempProject = ResourcesPlugin.getWorkspace().getRoot().getProject(sdeTempFolderName);
		String tmpProcessFolderName = pi.getProcessDefinition().getName() 
			+ "_" + pi.getProcessDefinition().getVersion() + "_" + pi.getId();
		IFolder tmpProcessFolder = sdeTempProject.getFolder(new Path(tmpProcessFolderName));
		if (! tmpProcessFolder.exists()){
			try {
				tmpProcessFolder.create(true, false, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		IFile ifile = tmpProcessFolder.getFile(filename);
		return ifile;
	}
	
	/**
	 * If output node is of type File or IFile a resource reference is created
	 * 
	 * @param ec
	 */
	protected void initializeOutputFile(ExecutionContext ec) {
		initializeVariableValue(ec);

		ProcessInstance processInstance = ec.getProcessInstance();
		Object file=null;
		if ((variableType.compareToIgnoreCase("File") == 0)
			|| (variableType.compareToIgnoreCase("IFile") == 0)) 
		{
			IFile ifile = getTempIFile(ec);
			
			// determine the result
			if (variableType.compareToIgnoreCase("File") == 0)
				file = ifile.getLocation().toFile();
			else
				file = ifile;
		}
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		contextInstance.setTransientVariable(variableName, file);
	}
	
	/**
	 * Content of data node (File, IFile or String) is uploaded to the repository
	 * 
	 * @param ec
	 */
	private void executeOutputFile(ExecutionContext ec) {
		// process variable is set
		//executeOutputPrimitive(ec);

		// content is uploaded to repository
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		String content = null;
		Object o = contextInstance.getTransientVariable(variableName);
		if ((variableType.compareToIgnoreCase("IFile") == 0)
				&& (o instanceof IFile))
		{
			content = callStringLoaderTool((IFile)o);
		}
		else if ((variableType.compareToIgnoreCase("File") == 0)
				&& (o instanceof File))
		{
			// TODO get content from File
			//content = ((File)o).;
		}
		else if ((variableType.compareToIgnoreCase("String") == 0)
				&& (o instanceof String))
		{
			content = (String)o;
		}
		
		if (content != null) {
			callRepoUploadTool(content);
		}
	}
	private String getParameterVariableIfExists(ExecutionContext ec){
		Object o = ec.getVariable("DataNode"+dataNodeId);
		if (o == null) return null;
		else if (o instanceof String) return (String) o;
		return null;
	}
	private void executeInputFile(ExecutionContext ec) {
		initializeVariableValue(ec);
		
		String fileString = callRepoDowloadTool();
		if (fileString == null){
			System.out.println("FileString is null: "+name);
			return;
		}
		ProcessInstance processInstance = ec.getProcessInstance();
		Object file=null;
		if ((variableType.compareToIgnoreCase("File") == 0)
			|| (variableType.compareToIgnoreCase("IFile") == 0)) 
		{
			try {
				IFile ifile = getTempIFile(ec);
				ifile.create(new ByteArrayInputStream(fileString.getBytes()), true, null);
				
				// determine the result
				if (variableType.compareToIgnoreCase("File") == 0)
					file = ifile.getLocation().toFile();
				else
					file = ifile;
				//File tmp =  File.createTempFile("tmp", pathFragments[pathFragments.length-1]) ;
				//tmp.deleteOnExit();
				//OutputStream o = new FileOutputStream(tmp);
				//o.write(fileString.getBytes());
				//o.close();
				//file = tmp;
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (variableType.compareToIgnoreCase("InputStream") == 0){
			file= new ByteArrayInputStream(fileString.getBytes());
		}
		else {
			file = fileString;
		}
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		contextInstance.setTransientVariable(variableName, file);
	}
	
	protected IFunction getSDEToolFunction(String toolID, String functionName) {
		IFunction goodFunction = null;
		ISensoriaCore core = SensoriaCoreRegistry.getLocalCore();
		
		if (core != null) {
			ITool tool = core.getTools().findToolById(toolID);
			if (tool != null) {
				List<IFunction> functions = tool.getFunctions();
				for (IFunction function : functions) {
					if (function.getSimpleName().equals(functionName)) {
						goodFunction=function;
					}
				}
			}
		}
		return goodFunction;
	}

	protected String callRepoDowloadTool() {
		String result=null;
		IFunction goodFunction = getSDEToolFunction("hu.bme.mit.toolintegration.repository.general.IGeneralRepository", "getDataFileContents");
		if (goodFunction != null) {
			List<Object> arguments = new ArrayList<Object>();
			arguments.add(variableValue);
			try {
				result = (String)goodFunction.invoke(arguments.toArray());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	protected String callRepoUploadTool(String content) {
		String result=null;
		IFunction goodFunction = getSDEToolFunction("hu.bme.mit.toolintegration.repository.general.IGeneralRepository", "setDataFileContents");
		if (goodFunction != null) {
			List<Object> arguments = new ArrayList<Object>();
			arguments.add(variableValue);
			arguments.add(content);
			try {
				result = (String)goodFunction.invoke(arguments.toArray());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	protected String callStringLoaderTool(IFile ifile) {
		String result=null;
		IFunction goodFunction = getSDEToolFunction("SensoriaUITool", "loadStringFromFile");
		if (goodFunction != null) {
			List<Object> arguments = new ArrayList<Object>();
			arguments.add(ifile);
			try {
				result = (String)goodFunction.invoke(arguments.toArray());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	protected void executeInputPrimitive(ExecutionContext ec){
		//
		String initialParameter = getParameterVariableIfExists(ec);
		if (initialParameter != null) variableValue = initialParameter;
		
		ProcessInstance processInstance = ec.getProcessInstance();
		ContextInstance contextInstance = (ContextInstance) processInstance.getInstance(ContextInstance.class);
		
		//fontos hogy hivatkozhassunk korábban definiált változóra is
		Object variableToSet;
		if (variableValue.startsWith("#")){
			variableToSet = contextInstance.getVariable(variableValue.substring(1));
		}
		else variableToSet = setVariableValue(variableValue);
		//beállítjuk a nevet
		if (isTransient){
			contextInstance.setTransientVariable(variableName, variableToSet);
		}
		else {
			contextInstance.setVariable(variableName, variableToSet);
		}
		
	}
	

	public void read(Element dataElement, JpdlXmlReader jpdlReader) {
		dataNodeId = Integer.parseInt(dataElement.attributeValue("id"));
		dataType= dataElement.attributeValue("type");
	   
	    variableName = dataElement.element("variableName").getText();
	    variableType = dataElement.element("variableType").getText();
	    
	    Element e = dataElement.element("variableValue");
	    variableValue = (	e == null ? null :e.getText());
	    
	    isTransient =true;
	     e =dataElement.element("transient");
	    if (e != null){
	    	isTransient = Boolean.parseBoolean(e.getText());
	    }
	    e=dataElement.element("creatorToolId");
	    if (e != null){
	    	toolId= e.getText();
	    }
	    
	 /*   if (expression!=null) {
	      decisionExpression = expression;

	    } else if (decisionHandlerElement!=null) {
	      decisionDelegation = new Delegation();
	      decisionDelegation.read(decisionHandlerElement, jpdlReader);
	    }*/
	  }
	 
	public Object setVariableValue(String valueString){
		Object retVariable;
		if (variableType.equalsIgnoreCase("Integer"))
			retVariable = Integer.parseInt(valueString);
		else if (variableType.equalsIgnoreCase("Float"))
			retVariable = Float.parseFloat(valueString);
		else if (variableType.equalsIgnoreCase("Double"))
			retVariable = Double.parseDouble(valueString);
		else if (variableType.equalsIgnoreCase("Character"))
			retVariable = valueString.charAt(0);
		else if (variableType.equalsIgnoreCase("Boolean"))
			retVariable =  Boolean.parseBoolean(valueString);
		else if (variableType.equalsIgnoreCase("Short"))
			retVariable = Short.parseShort(valueString);
		else if (variableType.equalsIgnoreCase("Long"))
			retVariable = Long.parseLong(valueString);
		else 
			retVariable = valueString;
		return retVariable;
	}

	public String getVariableValue() {
		return variableValue;
	}



	public String getToolId() {
		return toolId;
	}

	public String getDataType() {
		return dataType;
	}

	public int getDataNodeId() {
		return dataNodeId;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getVariableType() {
		return variableType;
	}

	
	public boolean isTransient() {
		return isTransient;
	}

	public boolean isInputDataNode() {
		return inputDataNode;
	}

	public void setInputDataNode(boolean inputDataNode) {
		this.inputDataNode = inputDataNode;
	}
	
	
}
