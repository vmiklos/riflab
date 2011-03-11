package logic;

import java.util.LinkedList;
import java.util.List;

import entities.Product;

public class ReqAnalysis implements Doable {

	public Product doIt(Product product) {
		List<String> requirements = new LinkedList<String>();
		requirements.add("elso kovetelmeny");
		requirements.add("masodik kovetelmeny");
		product = new Product();
		product.getSrs().setRequirements(requirements);
		return product;
	}
	
}
