package logic;

import hog.CalculatePi;

import java.util.List;

import entities.Product;

public class ReqChecking implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		product.getSrs().setConsistent(false);

		CalculatePi.computePI(1000000000);
		
		return product;
	}
}
