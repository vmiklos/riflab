package riflab3.logic1.services;

import riflab3.entities.Product;
import eu.sensoria_ist.casetool.core.ext.ISensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunction;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunctionParameter;

@SensoriaTool(
		categories = "RIFLAB",
		description = "Logic1",
		name="Logic1 Tool")
public interface ILogic1 extends ISensoriaTool {
	
	@SensoriaToolFunction(description = "ReqAnalysis")
	public Product doReqAnalysis() throws Exception;
	
	@SensoriaToolFunction(description = "ReqChecking")
	public Product doReqChecking(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;
}
