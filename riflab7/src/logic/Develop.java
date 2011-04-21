package logic;

import java.util.LinkedList;
import java.util.List;

import entities.File;
import entities.Product;

public class Develop implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		List<File> sourceFiles = new LinkedList<File>();
		File file = new File();
		file.setContent("public static void main()");
		file.setName("main.java");
		file.setQuality(100);
		sourceFiles.add(file);
		product.setSourceFiles(sourceFiles);
		return product;
	}
}
