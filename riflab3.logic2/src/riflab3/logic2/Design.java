package riflab3.logic2;

import java.util.LinkedList;
import java.util.List;

import riflab3.entities.Product;
import riflab3.interfaces.Doable;

public class Design implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		List<String> architecture = new LinkedList<String>();
		architecture.add("model");
		architecture.add("view");
		architecture.add("controller");
		product.setArchitecture(architecture);

		return product;
	}
}
