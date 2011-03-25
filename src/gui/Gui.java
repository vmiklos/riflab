package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.jms.Queue;
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
	private List<Queue> inQueues;
	private List<Queue> outQueues;
	int tasktype = 0;
	private GuiContext guiContext;
	
	public Gui(String name, Doable doable, List<Queue> inQueues, List<Queue> outQueues, int poscounter, GuiContext guiContext) {
		super(name);
		this.doable = doable;
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		this.guiContext = guiContext;
		
		next = new JButton("next");
		next.addActionListener(this);
		next.setActionCommand("next");
		setMinimumSize(new Dimension(350, 150));
		status = new JLabel(WAIT_FOR_NEXT);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new GuiListener(guiContext));
		getContentPane().setLayout(new FlowLayout());
		add(status);
		add(next);
		pack();
		setLocation(400*((int)(poscounter/4)), 200*(poscounter++ % 4));
		setVisible(true);
	}
	
	public Gui(String name, Doable doable, List<Queue> inQueues, List<Queue> outQueues, int poscounter, GuiContext guiContext, int tasktype) {
		this(name, doable, inQueues, outQueues, poscounter, guiContext);
		this.tasktype = tasktype;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("next".equals(e.getActionCommand())) {
			status.setText("waiting for input");
			if (tasktype == 1) {
				new Task_isConsistent(doable, inQueues, outQueues, status, guiContext).execute();
			}
			else {
				new Task(doable, inQueues, outQueues, status, guiContext).execute();
			}
		}
	}
}
