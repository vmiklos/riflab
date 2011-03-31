package riflab3.logic2;

import java.util.LinkedList;
import java.util.List;

import riflab3.entities.File;
import riflab3.entities.Product;
import riflab3.interfaces.Doable;

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