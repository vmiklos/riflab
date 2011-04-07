package hu.bme.mit.toolintegration.workflow.jbpm.eventhandler;

import java.util.ArrayList;
import java.util.List;

public class JbpmNodeEventNotifier {
	protected List<IJbpmNodeEventListener> listeners;
	protected static JbpmNodeEventNotifier observer;
	
	private JbpmNodeEventNotifier(){
		listeners= new ArrayList<IJbpmNodeEventListener>();
	}
	
	public synchronized static JbpmNodeEventNotifier getObserverInstance(){
		if(observer==null) observer= new JbpmNodeEventNotifier();
		return observer;
	}
	public synchronized void registerListener(IJbpmNodeEventListener wl){
		listeners.add(wl);
	}
	public synchronized void removeListener(IJbpmNodeEventListener wl){
		listeners.remove(wl);
	}
	public void fireListeners(JbpmNodeEvent event){
		for(IJbpmNodeEventListener wl : listeners){
			wl.handleJbpmNodeEvent(event);
		}
/*		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
*/	}
}
