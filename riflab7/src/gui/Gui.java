package gui;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.jms.Queue;

import logic.Doable;

public class Gui {

	public static final String WAIT_FOR_INPUT = "waiting for input";
	private static final long serialVersionUID = 1L;
	private Doable doable;
	private List<Queue> inQueues;
	private List<Queue> outQueues;
	int tasktype = 0;
	private GuiContext guiContext;
	
	public Gui(String name, Doable doable, List<Queue> inQueues, List<Queue> outQueues, int poscounter, GuiContext guiContext) {
		this.doable = doable;
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		this.guiContext = guiContext;
	}
	
	public Gui(String name, Doable doable, List<Queue> inQueues, List<Queue> outQueues, int poscounter, GuiContext guiContext, int tasktype) {
		this(name, doable, inQueues, outQueues, poscounter, guiContext);
		this.tasktype = tasktype;
	}
	
	public void loop() {
		while(true) {
			Task task = null;
			if (tasktype == 1) {
				task = new Task_isConsistent(doable, inQueues, outQueues, guiContext);
			}
			else {
				task = new Task(doable, inQueues, outQueues, guiContext);
			}
			task.execute();
			try {
				task.get();
			} catch (InterruptedException e) {
				System.err.println("Somehow running is interrupted. Exiting...");
				break;
			} catch (ExecutionException e) {
				System.err.println("Somehow running is failed. Exiting...");
				break;
			}
		}
	}
}
