package logic;

import java.util.LinkedList;
import java.util.List;

import entities.Product;

public class ReqAnalysis implements Doable {
	static Integer id = 0;

	public Product doIt(List<Product> products) {
		List<String> requirements = new LinkedList<String>();
		requirements.add("elso kovetelmeny");
		requirements.add("masodik kovetelmeny");
		Product product = products.get(0);
		product.getSrs().setRequirements(requirements);
		
		return product;
	}
	
}
