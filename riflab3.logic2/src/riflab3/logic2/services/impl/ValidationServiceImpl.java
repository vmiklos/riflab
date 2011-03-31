package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Validation;
import riflab3.logic2.services.ValidationService;

public class ValidationServiceImpl implements ValidationService {

	@Override
	public Product doValidation(List<Product> product) {
		Validation design = new Validation();
		return design.doIt(product);
	}
}
