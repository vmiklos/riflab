package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Doc;
import riflab3.logic2.services.DocService;

public class DocServiceImpl implements DocService {

	@Override
	public Product doDocCreation(List<Product> product) {
		Doc design = new Doc();
		return design.doIt(product);
	}
}
