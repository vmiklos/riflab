package riflab3.logic2;

import java.util.List;
import riflab3.entities.Product;
import riflab3.interfaces.Doable;

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
