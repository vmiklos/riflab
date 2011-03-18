package gui;

import com.ibm.mq.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import com.ibm.mq.MQQueue;

public class GuiListener extends WindowAdapter{
	private List<MQQueue> inQueues;
	private List<MQQueue> outQueues;
	MQQueueManager qMgr;


	public GuiListener(List<MQQueue> inQueues, List<MQQueue> outQueues, MQQueueManager qMgr) {
		this.inQueues = inQueues;
		this.outQueues = outQueues;
		this.qMgr = qMgr;
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		try {
			for(MQQueue q : inQueues) {
				q.close();
			}
			for(MQQueue q : outQueues) {
				q.close();
			}
			qMgr.close();
			System.out.println("Closing...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
