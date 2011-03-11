package logic;

import entities.Product;

public class ReqRefine implements Doable {

	public Product doIt(Product product) {
		product.getSrs().setConsistent(true);
		return product;
	}
}
