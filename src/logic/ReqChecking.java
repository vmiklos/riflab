package logic;

import entities.Product;

public class ReqChecking implements Doable {

	public Product doIt(Product product) {
		product.getSrs().setConsistent(false);
		return product;
	}
}
