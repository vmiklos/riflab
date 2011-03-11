package logic;

import java.util.LinkedList;
import java.util.List;

import entities.File;
import entities.Product;

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
