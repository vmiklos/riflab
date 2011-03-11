package logic;

import java.util.LinkedList;
import java.util.List;

import entities.Product;

public class Design implements Doable {

	public Product doIt(Product product) {
		List<String> architecture = new LinkedList<String>();
		architecture.add("model");
		architecture.add("view");
		architecture.add("controller");
		product.setArchitecture(architecture);
		return product;
	}
}
