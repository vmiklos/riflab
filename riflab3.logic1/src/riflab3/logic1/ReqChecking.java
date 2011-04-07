package riflab3.logic1;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import riflab3.entities.Product;
import riflab3.interfaces.Doable;

public class ReqChecking implements Doable {

	public Product doIt(List<Product> products) {
		Product product = products.get(0);
		product.getSrs().setConsistent(ask());
		return product;
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
