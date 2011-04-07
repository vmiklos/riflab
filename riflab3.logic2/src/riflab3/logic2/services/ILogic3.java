package riflab3.logic2.services;

import riflab3.entities.Product;
import eu.sensoria_ist.casetool.core.ext.ISensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaTool;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunction;
import eu.sensoria_ist.casetool.core.ext.SensoriaToolFunctionParameter;

@SensoriaTool(
		categories = "RIFLAB",
		description = "Logic3",
		name="Logic3 Tool")
public interface ILogic3 extends ISensoriaTool {

	@SensoriaToolFunction(description = "Validation")
	public Product doValidation(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;

	@SensoriaToolFunction(description = "Verification")
	public Product doVerification(
			@SensoriaToolFunctionParameter(description = "Input Product List") 
			Product product) throws Exception;

}
