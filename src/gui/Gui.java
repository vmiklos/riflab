package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import entities.Product;

import logic.Doable;

public class Gui extends JFrame implements ActionListener {

	private static final String WAIT_FOR_NEXT = "waiting for next button";
	private static final long serialVersionUID = 1L;
	private JButton next;
	private JLabel status;
	private static int poscounter = 0;
	private Doable doable;
	private List<BlockingQueue<Product>> inQueues;
	private List<BlockingQueue<Product>> outQueues;
	private String dtoStatus = "";
	
	public Gui(String name, Doable doable, List<BlockingQueue<Product>> inQueues, List<BlockingQueue<Product>> outQueues) {
		super(name);
		this.doable = doable;
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		
		next = new JButton("next");
		next.addActionListener(this);
		next.setActionCommand("next");
		setMinimumSize(new Dimension(350, 150));
		status = new JLabel(WAIT_FOR_NEXT);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout());
		add(status);
		add(next);
		pack();
		setLocation(400*((int)(poscounter/4)), 200*(poscounter++ % 4));
		setVisible(true);
	}
	
	private class Task extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			List<Product> ins = new LinkedList<Product>();
			Product res;
			for(BlockingQueue<Product> q : inQueues) {
				ins.add(q.poll(60L, TimeUnit.SECONDS));
			}
			dtoStatus = "running input";
			if (ins.size() > 0) {
				for (Product p : ins) {
					dtoStatus += "<br>'"+p+"'";
				}
			}
			status.setText("<html>" + dtoStatus + "</html>");
			res = doable.doIt(ins);
			Thread.sleep(1000);
			for (BlockingQueue<Product> q : outQueues) {
				q.add(res);
			}
			publish();
			return null;
		}
		
		@Override
		protected void process(List<Void> chunks) {
			status.setText("<html>" + dtoStatus + "<br>" + WAIT_FOR_NEXT + "</html>");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("next".equals(e.getActionCommand())) {
			status.setText("waiting for input");
			new Task().execute();
		}
	}
}
