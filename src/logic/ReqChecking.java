package logic;

import java.util.List;

import entities.Product;

public class ReqChecking implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		product.getSrs().setConsistent(false);
		return product;
	}
}
