package gui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import logic.ReqAnalysis;
import logic.ReqChecking;

import entities.Product;

public class Controller {
	
	private BlockingQueue<Product> reqAnalysisCheckingQueue;
	private BlockingQueue<Product> reqCheckingDesignQueue;
	private BlockingQueue<Product> reqCheckingRefineQueue;
	private BlockingQueue<Product> reqRefineDesignQueue;

	public List<BlockingQueue<Product>> asList(BlockingQueue<Product> q) {
		List<BlockingQueue<Product>> l = new LinkedList<BlockingQueue<Product>>();
		l.add(q);
		return l;
	}

	public Controller() {
		
		reqAnalysisCheckingQueue = new LinkedBlockingQueue<Product>();
		reqCheckingDesignQueue = new LinkedBlockingQueue<Product>();
		reqCheckingRefineQueue = new LinkedBlockingQueue<Product>();
		reqRefineDesignQueue = new LinkedBlockingQueue<Product>();

		boolean yesno = ask();
		
		new Gui("ReqAnalysis", new ReqAnalysis(), new LinkedList<BlockingQueue<Product>>(), asList(reqAnalysisCheckingQueue));
		if (yesno)
			new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(reqCheckingDesignQueue));
		else
			new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(reqCheckingRefineQueue));
		new Gui("ReqRefine", new ReqChecking(), asList(reqCheckingRefineQueue), asList(reqRefineDesignQueue));
		if (yesno)
			new Gui("Design", new ReqChecking(), asList(reqCheckingDesignQueue), new LinkedList<BlockingQueue<Product>>());
		else
			new Gui("Design", new ReqChecking(), asList(reqRefineDesignQueue), new LinkedList<BlockingQueue<Product>>());
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