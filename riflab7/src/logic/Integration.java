package logic;

import java.util.List;
import entities.Product;

public class Integration implements Doable {

	public Product doIt(List<Product> products) {
		Product develop = products.get(0);
		Product test = products.get(1);
		Product doc = products.get(2);
		develop.setTestSuite(test.getTestSuite());
		develop.setDocumentation(doc.getDocumentation());
		return develop;
	}
}
