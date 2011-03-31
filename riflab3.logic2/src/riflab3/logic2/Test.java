package riflab3.logic2;

import java.util.LinkedList;
import java.util.List;

import riflab3.entities.File;
import riflab3.entities.Product;
import riflab3.interfaces.Doable;

public class Test implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		List<File> testSuite = new LinkedList<File>();
		File file = new File();
		file.setContent("public static void test()");
		file.setName("test.java");
		file.setQuality(100);
		testSuite.add(file);
		product.setTestSuite(testSuite);
		return product;
	}
}
