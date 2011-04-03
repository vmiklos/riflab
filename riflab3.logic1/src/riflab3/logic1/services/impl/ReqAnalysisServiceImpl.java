package riflab3.logic1.services.impl;

import java.util.List;


import riflab3.entities.Product;
import riflab3.logic1.ReqAnalysis;
import riflab3.logic1.services.ReqAnalysisService;

public class ReqAnalysisServiceImpl implements ReqAnalysisService {

	@Override
	public Product doReqAnalysis(List<Product> product) {
		ReqAnalysis analysis = new ReqAnalysis();
		return analysis.doIt(product);
	}
}
