package gui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import logic.*;
import entities.Product;

public class Controller {
	
	private BlockingQueue<Product> reqAnalysisCheckingQueue;
	private BlockingQueue<Product> isConsistentSwitchQueue;
	private BlockingQueue<Product> reqDesignQueue;
	private BlockingQueue<Product> reqCheckingRefineQueue;
	private BlockingQueue<Product> designDevelopQueue;
	private BlockingQueue<Product> designTestQueue;
	private BlockingQueue<Product> designDocQueue;
	private BlockingQueue<Product> developIntegrateQueue;
	private BlockingQueue<Product> testIntegrateQueue;
	private BlockingQueue<Product> docIntegrateQueue;
	private BlockingQueue<Product> integrateVerificationQueue;
	private BlockingQueue<Product> verificationValidationQueue;

	public List<BlockingQueue<Product>> asList(BlockingQueue<Product> q1) {
		List<BlockingQueue<Product>> l = new LinkedList<BlockingQueue<Product>>();
		l.add(q1);
		return l;
	}
	
	public List<BlockingQueue<Product>> asList(BlockingQueue<Product> q1, BlockingQueue<Product> q2) {
		List<BlockingQueue<Product>> l = new LinkedList<BlockingQueue<Product>>();
		l.add(q1);
		l.add(q2);
		return l;
	}

	public List<BlockingQueue<Product>> asList(BlockingQueue<Product> q1, BlockingQueue<Product> q2, BlockingQueue<Product> q3) {
		List<BlockingQueue<Product>> l = new LinkedList<BlockingQueue<Product>>();
		l.add(q1);
		l.add(q2);
		l.add(q3);
		return l;
	}

	public Controller() {
		
		reqAnalysisCheckingQueue = new LinkedBlockingQueue<Product>();
		isConsistentSwitchQueue = new LinkedBlockingQueue<Product>();
		reqDesignQueue = new LinkedBlockingQueue<Product>();
		reqCheckingRefineQueue = new LinkedBlockingQueue<Product>();
		designDevelopQueue = new LinkedBlockingQueue<Product>();
		designTestQueue = new LinkedBlockingQueue<Product>();
		designDocQueue = new LinkedBlockingQueue<Product>();
		developIntegrateQueue = new LinkedBlockingQueue<Product>();
		testIntegrateQueue = new LinkedBlockingQueue<Product>();
		docIntegrateQueue = new LinkedBlockingQueue<Product>();
		integrateVerificationQueue = new LinkedBlockingQueue<Product>();
		verificationValidationQueue = new LinkedBlockingQueue<Product>();
		
		new Gui("ReqAnalysis", new ReqAnalysis(), new LinkedList<BlockingQueue<Product>>(), asList(reqAnalysisCheckingQueue));
		
		new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(isConsistentSwitchQueue));
		
		new Gui("isConsistentSwitch", null, asList(isConsistentSwitchQueue), asList(reqDesignQueue, reqCheckingRefineQueue),1);
		
		new Gui("ReqRefine", new ReqRefine(), asList(reqCheckingRefineQueue), asList(reqDesignQueue));
		new Gui("Design", new Design(), asList(reqDesignQueue), asList(designDevelopQueue, designTestQueue, designDocQueue));
		
		new Gui("Develop", new Develop(), asList(designDevelopQueue), asList(developIntegrateQueue));
		new Gui("Test", new Test(), asList(designTestQueue), asList(testIntegrateQueue));
		new Gui("Doc", new Doc(), asList(designDocQueue), asList(docIntegrateQueue));
		new Gui("Integration", new Integration(), asList(developIntegrateQueue, testIntegrateQueue, docIntegrateQueue), asList(integrateVerificationQueue));
		new Gui("Verification", new Verification(), asList(integrateVerificationQueue), asList(verificationValidationQueue));
		new Gui("Validation", new Validation(), asList(verificationValidationQueue), new LinkedList<BlockingQueue<Product>>());
	}

	public static void main(String[] args) {
		new Controller();
	}
}