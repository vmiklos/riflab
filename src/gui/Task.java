package gui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import logic.Doable;

import entities.Product;

public class Task extends SwingWorker<Void, Void> {

	protected String dtoStatus = "";
	protected Doable doable;
	protected List<BlockingQueue<Product>> inQueues;
	protected List<BlockingQueue<Product>> outQueues;
	JLabel status;

	public Task(Doable doable, List<BlockingQueue<Product>> inQueues, List<BlockingQueue<Product>> outQueues, JLabel status) {
		this.doable = doable;
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		this.status = status;
	}
	
	@Override
	protected Void doInBackground() {
		List<Product> ins = new LinkedList<Product>();
		Product res;

		try {
			getFromIntput(ins);
			res = processData(ins);
			copyToOutput(res);
		} catch (Exception e1) {
			return null;
		}

		return null;
	}

	protected void getFromIntput(List<Product> ins) throws Exception {
		for(BlockingQueue<Product> q : inQueues) {
			try {
				Product p = q.poll(20L, TimeUnit.SECONDS);
				if (p == null) {
					dtoStatus = "Read timeout!<br>" + Gui.WAIT_FOR_NEXT;
					publish();
					throw new Exception("Read timeout");
				}
				ins.add(p);
			} catch (InterruptedException e) {
				dtoStatus = "Read interrupted!<br>" + Gui.WAIT_FOR_NEXT;
				publish();
				throw new Exception("Read interrupted");
			}
		}
		dtoStatus = "running input";
		if (ins.size() > 0) {
			for (Product p : ins) {
				dtoStatus += "<br>'"+p+"'";
			}
		}
		publish();
	}

	protected Product processData(List<Product> ins) throws Exception {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			dtoStatus = "Running interrupted!<br>" + Gui.WAIT_FOR_NEXT;
			publish();
			throw new Exception("Running interrupted");
		}
		return doable.doIt(ins);
	}

	protected void copyToOutput(Product res) {
		for (BlockingQueue<Product> q : outQueues) {
			q.add(res);
		}
		dtoStatus = dtoStatus + "<br>" + Gui.WAIT_FOR_NEXT;
		publish();
	}

	@Override
	protected void process(List<Void> chunks) {
		status.setText("<html>" + dtoStatus + "</html>");
	}

}
