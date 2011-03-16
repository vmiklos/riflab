package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import entities.Product;

import logic.Doable;

public class Gui extends JFrame implements ActionListener {

	public static final String WAIT_FOR_NEXT = "waiting for next button";
	private static final long serialVersionUID = 1L;
	private JButton next;
	private JLabel status;
	public static int poscounter = 0;
	private Doable doable;
	private List<BlockingQueue<Product>> inQueues;
	private List<BlockingQueue<Product>> outQueues;
	int tasktype = 0;
	
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
	
	Gui(String name, Doable doable, List<BlockingQueue<Product>> inQueues, List<BlockingQueue<Product>> outQueues, int tasktype) {
		this(name, doable, inQueues, outQueues);
		this.tasktype = tasktype;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("next".equals(e.getActionCommand())) {
			status.setText("waiting for input");
			if (tasktype == 1) {
				new Task_isConsistent(doable, inQueues, outQueues, status).execute();
			}
			else
				new Task(doable, inQueues, outQueues, status).execute();
		}
	}
}
