package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Develop;
import riflab3.logic2.services.DevelopService;

public class DevelopServiceImpl implements DevelopService {

	@Override
	public Product doDevelopment(List<Product> product) {
		Develop design = new Develop();
		return design.doIt(product);
	}
}
