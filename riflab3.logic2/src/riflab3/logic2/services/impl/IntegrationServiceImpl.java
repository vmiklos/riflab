package riflab3.logic2.services.impl;

import java.util.List;

import riflab3.entities.Product;
import riflab3.logic2.Integration;
import riflab3.logic2.services.IntegrationService;

public class IntegrationServiceImpl implements IntegrationService {

	@Override
	public Product doIntegration(List<Product> product) {
		Integration integration = new Integration();
		return integration.doIt(product);
	}
}
