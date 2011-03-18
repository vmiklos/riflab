package gui;

import com.ibm.mq.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import logic.Doable;

import entities.Product;

public class Task extends SwingWorker<Void, Void> {

	protected String dtoStatus = "";
	protected Doable doable;
	protected List<MQQueue> inQueues;
	protected List<MQQueue> outQueues;
	JLabel status;

	public Task(Doable doable, List<MQQueue> inQueues, List<MQQueue> outQueues, JLabel status) {
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
		for(MQQueue q : inQueues) {
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
		for (MQQueue q : outQueues) {
			put(q, res);
		}
		dtoStatus = dtoStatus + "<br>" + Gui.WAIT_FOR_NEXT;
		publish();
	}

	@SuppressWarnings("deprecation")
	protected Product poll(MQQueue q, int timeout) {
		MQMessage retrievedMessage = new MQMessage();
		
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		gmo.options |= MQC.MQGMO_WAIT;
		gmo.waitInterval = timeout*1000;
		Product product = null;
		try {
			q.get(retrievedMessage, gmo);
			product = (Product) retrievedMessage.readObject();
		} catch (MQException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		return product;
	}
	
	protected void put(MQQueue q, Product p) {
		MQMessage m = new MQMessage();
		try {
			m.writeObject(p);
			MQPutMessageOptions pmo = new MQPutMessageOptions(); 
			q.put(m,pmo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void process(List<Void> chunks) {
		status.setText("<html>" + dtoStatus + "</html>");
	}

}
