package riflab3.logic;

import java.util.List;

import riflab3.entities.Product;
import riflab3.interfaces.Doable;

public class ReqChecking implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		product.getSrs().setConsistent(false);

		return product;
	}
}
