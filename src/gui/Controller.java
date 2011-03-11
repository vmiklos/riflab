package gui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logic.*;
import entities.Product;

public class Controller {
	
	private BlockingQueue<Product> reqAnalysisCheckingQueue;
	private BlockingQueue<Product> reqCheckingDesignQueue;
	private BlockingQueue<Product> reqCheckingRefineQueue;
	private BlockingQueue<Product> reqRefineDesignQueue;
	private BlockingQueue<Product> designDevelopQueue;
	private BlockingQueue<Product> designTestQueue;
	private BlockingQueue<Product> designDocQueue;
	private BlockingQueue<Product> developIntegrateQueue;
	private BlockingQueue<Product> testIntegrateQueue;
	private BlockingQueue<Product> docIntegrateQueue;
	private BlockingQueue<Product> integrateVerificationQueue;
	private BlockingQueue<Product> verificationValidationQueue;

	public List<BlockingQueue<Product>> asList(BlockingQueue<Product> q) {
		List<BlockingQueue<Product>> l = new LinkedList<BlockingQueue<Product>>();
		l.add(q);
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
		reqCheckingDesignQueue = new LinkedBlockingQueue<Product>();
		reqCheckingRefineQueue = new LinkedBlockingQueue<Product>();
		reqRefineDesignQueue = new LinkedBlockingQueue<Product>();
		designDevelopQueue = new LinkedBlockingQueue<Product>();
		designTestQueue = new LinkedBlockingQueue<Product>();
		designDocQueue = new LinkedBlockingQueue<Product>();
		developIntegrateQueue = new LinkedBlockingQueue<Product>();
		testIntegrateQueue = new LinkedBlockingQueue<Product>();
		docIntegrateQueue = new LinkedBlockingQueue<Product>();
		integrateVerificationQueue = new LinkedBlockingQueue<Product>();
		verificationValidationQueue = new LinkedBlockingQueue<Product>();

		boolean yesno = ask();
		
		new Gui("ReqAnalysis", new ReqAnalysis(), new LinkedList<BlockingQueue<Product>>(), asList(reqAnalysisCheckingQueue));
		if (yesno)
			new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(reqCheckingDesignQueue));
		else
			new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(reqCheckingRefineQueue));
		new Gui("ReqRefine", new ReqRefine(), asList(reqCheckingRefineQueue), asList(reqRefineDesignQueue));
		if (yesno)
			new Gui("Design", new Design(), asList(reqCheckingDesignQueue), asList(designDevelopQueue, designTestQueue, designDocQueue));
		else
			new Gui("Design", new Design(), asList(reqRefineDesignQueue), asList(designDevelopQueue, designTestQueue, designDocQueue));
		new Gui("Develop", new Develop(), asList(designDevelopQueue), asList(developIntegrateQueue));
		new Gui("Test", new Test(), asList(designTestQueue), asList(testIntegrateQueue));
		new Gui("Doc", new Doc(), asList(designDocQueue), asList(docIntegrateQueue));
		new Gui("Integration", new Integration(), asList(developIntegrateQueue, testIntegrateQueue, docIntegrateQueue), asList(integrateVerificationQueue));
		new Gui("Verification", new Verification(), asList(integrateVerificationQueue), asList(verificationValidationQueue));
		new Gui("Validation", new Validation(), asList(verificationValidationQueue), new LinkedList<BlockingQueue<Product>>());
	}

	private boolean ask() {
		JOptionPane pane = new JOptionPane("Will the requirements be consistent?");
		Object[] options = new String[] { "Yes", "No" };
		pane.setOptions(options);
		JDialog dialog = pane.createDialog(new JFrame(), "Initialization parameter");
		dialog.setVisible(true);
		Object obj = pane.getValue();
		int result = -1;
		for (int k = 0; k < options.length; k++)
			if (options[k].equals(obj))
				result = k;
		return result == 0;
	}

	public static void main(String[] args) {
		new Controller();
	}
}