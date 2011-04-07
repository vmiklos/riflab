package riflab3.logic2.services;

import java.util.LinkedList;
import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Design;
import riflab3.logic2.Develop;
import riflab3.logic2.Doc;
import riflab3.logic2.Integration;
import riflab3.logic2.ReqRefine;
import riflab3.logic2.Test;

public class Logic2 implements ILogic2 {

	@Override
	public Product doDesign(Product product) throws Exception {
		Design design = new Design();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

	@Override
	public Product doDevelop(Product product) throws Exception {
		Develop design = new Develop();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

	@Override
	public Product doDoc(Product product) throws Exception {
		Doc design = new Doc();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

	@Override
	public Product doIntegration(Product productDevelopment, Product productTesting, Product productDoc) throws Exception {
		Integration integration = new Integration();
		List<Product> products = new  LinkedList<Product>();
		products.add(productDevelopment);
		products.add(productTesting);
		products.add(productDoc);
		return integration.doIt(products);
	}

	@Override
	public Product doReqRefine(Product product) throws Exception {
		ReqRefine design = new ReqRefine();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

	@Override
	public Product doTest(Product product) throws Exception {
		Test design = new Test();
		List<Product> products = new  LinkedList<Product>();
		products.add(product);
		return design.doIt(products);
	}

}
