package gui;

import java.util.List;

import javax.jms.Queue;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import logic.Doable;

import entities.Product;

public class Task_isConsistent extends Task {

	boolean isConsistent;
	
	public Task_isConsistent(Doable doable,
			List<Queue> inQueues,
			List<Queue> outQueues, JLabel status, GuiContext guiContext) {
		super(doable, inQueues, outQueues, status, guiContext);
	}
	
	@Override
	protected Product processData(List<Product> ins) throws Exception {
		isConsistent = ask();
		Product product;
		product = ins.get(0);
		// product.setVerified(true);
		return product;
	}

	@Override
	protected void copyToOutput(Product res) {
		if (isConsistent) {
			put(outQueues.get(0), res);
		}
		else {
			put(outQueues.get(1), res);
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
