package gui;

import com.ibm.mq.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import logic.Doable;

public class Gui extends JFrame implements ActionListener {

	public static final String WAIT_FOR_NEXT = "waiting for next button";
	private static final long serialVersionUID = 1L;
	private JButton next;
	private JLabel status;
	private Doable doable;
	private List<MQQueue> inQueues;
	private List<MQQueue> outQueues;
	int tasktype = 0;
	MQQueueManager qMgr;
	
	public Gui(String name, Doable doable, List<MQQueue> inQueues, List<MQQueue> outQueues, int poscounter, MQQueueManager qMgr) {
		super(name);
		this.doable = doable;
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		this.qMgr = qMgr;
		
		next = new JButton("next");
		next.addActionListener(this);
		next.setActionCommand("next");
		setMinimumSize(new Dimension(350, 150));
		status = new JLabel(WAIT_FOR_NEXT);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new GuiListener(inQueues, outQueues, qMgr));
		getContentPane().setLayout(new FlowLayout());
		add(status);
		add(next);
		pack();
		setLocation(400*((int)(poscounter/4)), 200*(poscounter++ % 4));
		setVisible(true);
	}
	
	Gui(String name, Doable doable, List<MQQueue> inQueues, List<MQQueue> outQueues, int poscounter, MQQueueManager qMgr, int tasktype) {
		this(name, doable, inQueues, outQueues, poscounter, qMgr);
		this.tasktype = tasktype;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("next".equals(e.getActionCommand())) {
			status.setText("waiting for input");
			if (tasktype == 1) {
				new Task_isConsistent(doable, inQueues, outQueues, status).execute();
			}
			else {
				new Task(doable, inQueues, outQueues, status).execute();
			}
		}
	}
}
