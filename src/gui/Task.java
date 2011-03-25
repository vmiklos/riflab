package gui;

import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import logic.Doable;

import entities.Product;

public class Task extends SwingWorker<Void, Void> {

	protected String dtoStatus = "";
	protected Doable doable;
	protected GuiContext guiContext;
	protected List<Queue> inQueues;
	protected List<Queue> outQueues;
	JLabel status;

	public Task(Doable doable, List<Queue> inQueues, List<Queue> outQueues, JLabel status, GuiContext guiContext) {
		this.doable = doable;
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		this.status = status;
		this.guiContext = guiContext;
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
		for(Queue q : inQueues) {
			try {
				Product p = poll(q, 60);
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
		for (Queue q : outQueues) {
			put(q, res);
		}
		dtoStatus = dtoStatus + "<br>" + Gui.WAIT_FOR_NEXT;
		publish();
	}

	protected Product poll(Queue q, int timeout) {
		Product product = null;

		try {
			QueueReceiver queueReceiver = guiContext.getQueueSession().createReceiver(q);
			ObjectMessage retrievedMessage = (ObjectMessage) queueReceiver.receive(timeout * 1000);
			product = (Product) retrievedMessage.getObject();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return product;
	}

	protected boolean put(Queue q, Product p) {
		try {
			QueueSender queueSender = guiContext.getQueueSession().createSender(q);
			ObjectMessage m = guiContext.getQueueSession().createObjectMessage(p);
			queueSender.send(m);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	protected void process(List<Void> chunks) {
		status.setText("<html>" + dtoStatus + "</html>");
	}

}
