package riflab3.logic2.services;

import java.util.LinkedList;
import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Validation;
import riflab3.logic2.Verification;

public class Logic3 implements ILogic3 {

	@Override
	public Product doValidation(Product product) throws Exception {
		Validation design = new Validation();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

	@Override
	public Product doVerification(Product product) throws Exception {
		Verification design = new Verification();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

}
