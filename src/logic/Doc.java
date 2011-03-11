package logic;

import java.util.LinkedList;
import java.util.List;

import entities.File;
import entities.Product;

public class Doc implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		List<File> documentation = new LinkedList<File>();
		File file = new File();
		file.setContent("doc content");
		file.setName("doc.txt");
		file.setQuality(100);
		documentation.add(file);
		product.setDocumentation(documentation);
		return product;
	}
}
