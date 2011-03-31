package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Verification;
import riflab3.logic2.services.VerificationService;

public class VerificationServiceImpl implements VerificationService {

	@Override
	public Product doVerification(List<Product> product) {
		Verification design = new Verification();
		return design.doIt(product);
	}
}
