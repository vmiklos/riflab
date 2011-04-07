package riflab3.logic1.services;

import java.util.LinkedList;
import java.util.List;

import riflab3.entities.Product;
import riflab3.logic1.ReqAnalysis;
import riflab3.logic1.ReqChecking;

public class Logic1 implements ILogic1 {

	@Override
	public Product doReqAnalysis() throws Exception {
		ReqAnalysis analysis = new ReqAnalysis();
		System.out.println("doReqAnalysis");
		return analysis.doIt(new LinkedList<Product>());
	}

	@Override
	public Product doReqChecking(Product product) throws Exception {
		ReqChecking reqChecking = new ReqChecking();
		System.out.println("doReqChecking");
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return reqChecking.doIt(products);
	}

}
