package logic;

import java.util.List;

import entities.Product;

public class ReqRefine implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		System.out.println("ReqRefine::doIt()");
		System.out.println(product);
		product.getSrs().setConsistent(true);
		System.out.println(product);
		
		return product;
	}
}
