package riflab3.logic1.services.impl;


import java.util.List;


import riflab3.entities.Product;
import riflab3.logic1.ReqChecking;
import riflab3.logic1.services.ReqCheckingService;

public class ReqCheckingServiceImpl implements ReqCheckingService {

	@Override
	public Product doReqChecking(List<Product> product) {
		ReqChecking reqChecking = new ReqChecking();
		return reqChecking.doIt(product);
	}
}
