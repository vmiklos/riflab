package gui;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import logic.Doable;

import entities.Product;

public class Task_isConsistent extends Task {

	boolean isConsistent;
	
	public Task_isConsistent(Doable doable,
			List<BlockingQueue<Product>> inQueues,
			List<BlockingQueue<Product>> outQueues, JLabel status) {
		super(doable, inQueues, outQueues, status);
	}
	
	@Override
	protected Product processData(List<Product> ins) throws Exception {
		isConsistent = ask();
		return ins.get(0);
	}

	@Override
	protected void copyToOutput(Product res) {
		if (isConsistent) {
			outQueues.get(0).add(res);
		}
		else {
			outQueues.get(1).add(res);
		}
		dtoStatus = dtoStatus + "<br>" + Gui.WAIT_FOR_NEXT;
		publish();
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

}
