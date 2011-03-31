package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.ReqRefine;
import riflab3.logic2.services.ReqRefineService;

public class ReqRefineServiceImpl implements ReqRefineService {

	@Override
	public Product doReqRefine(List<Product> product) {
		ReqRefine design = new ReqRefine();
		return design.doIt(product);
	}
}
