package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Design;
import riflab3.logic2.services.DesignService;

public class DesignServiceImpl implements DesignService {

	@Override
	public Product doDesign(List<Product> product) {
		Design design = new Design();
		return design.doIt(product);
	}
}
