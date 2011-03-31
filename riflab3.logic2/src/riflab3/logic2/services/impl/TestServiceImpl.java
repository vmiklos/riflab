package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Test;
import riflab3.logic2.services.TestService;

public class TestServiceImpl implements TestService {

	@Override
	public Product doTesting(List<Product> product) {
		Test design = new Test();
		return design.doIt(product);
	}
}
