package logic;

import java.util.List;
import entities.Product;

public class Verification implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		product.setVerified(true);
		return product;
	}
}
