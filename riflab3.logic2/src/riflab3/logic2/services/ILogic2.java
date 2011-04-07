package riflab3.logic2.services;

import riflab3.entities.Product;
import eu.sensoria_ist.casetool.core.ext.ISensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunction;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunctionParameter;

@SensoriaTool(
		categories = "RIFLAB",
		description = "Logic2",
		name="Logic2 Tool")
public interface ILogic2 extends ISensoriaTool {

	@SensoriaToolFunction(description = "Design")
	public Product doDesign(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;

	@SensoriaToolFunction(description = "Develop")
	public Product doDevelop(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;

	@SensoriaToolFunction(description = "Doc")
	public Product doDoc(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;

	@SensoriaToolFunction(description = "Integration")
	public Product doIntegration(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product productDevelopment, Product productTesting, Product productDoc) throws Exception;

	@SensoriaToolFunction(description = "ReqRefine")
	public Product doReqRefine(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;

	@SensoriaToolFunction(description = "Test")
	public Product doTest(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;
	
}
