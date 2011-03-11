package logic;

import java.util.List;

import entities.Product;

public class ReqRefine implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		product.getSrs().setConsistent(true);
		return product;
	}
}
