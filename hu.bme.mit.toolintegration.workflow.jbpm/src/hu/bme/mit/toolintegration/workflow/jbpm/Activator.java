package hu.bme.mit.toolintegration.workflow.jbpm;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

import org.jbpm.graph.exe.Token;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin  implements  ILogListener{

	// The plug-in ID
	public static final String PLUGIN_ID = "hu.bme.mit.toolintegration.workflow.jbpm";

	private String logTopicName = "hu/bme/mit/toolintegration/workflow/jbpm/LOGGING_EVENT";
	private String jbpmNodeTopicName = "hu/bme/mit/toolintegration/workflow/jbpm/NODE_EVENT";
	
	// The shared instance
	private static Activator plugin;

	private EventAdmin eventAdmin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		plugin.getLog().addLogListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin.getLog().removeLogListener(this);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	
	/**
	 * Called by OSGI DS, when EventAdmin registers.
	 * @param eventAdmin
	 */
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
		System.out.println("**** EventAdmin registered");
	}
	/**
	 * Called by OSGI DS, when EventAdmin is removed.
	 * @param eventAdmin
	 */
	public void removeEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
		System.out.println("**** EventAdmin removed");
	}
	
	protected void postNewEvent(Event event){
		if (eventAdmin != null)
			eventAdmin.postEvent(event);
		
	}

	@Override
	public void logging(IStatus status, String plugin) {
		if (!plugin.equals(PLUGIN_ID)) return;
		String message = status.getMessage();
		String severity;
		switch ( status.getSeverity()){
			case IStatus.CANCEL: severity = "CANCEL"; break;
			case IStatus.ERROR: severity = "ERROR"; break;
			case IStatus.WARNING: severity = "WARNING"; break;
			case IStatus.INFO: severity = "INFO"; break;
			case IStatus.OK: 
			default: severity = "OK";		
		}
		Throwable ex = status.getException();
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("message", message);
		params.put("severity",severity);
		if (ex != null)
			params.put("exception", ex);
		params.put("plugin", PLUGIN_ID);
		
		
		postNewEvent ( new Event(logTopicName, params));
		
	}
	public void postJbpmEvent (Token token, org.jbpm.graph.def.Event event){
		Map<String,String> params = new HashMap<String,String>();
		String tokenId = token == null ? "" : String.valueOf(token.getId());
		String currentNodeName = token == null ? "" :token.getNode().getName();
		String processInstanceId = token == null ? "" :String.valueOf(token.getProcessInstance().getId());
		String eventType = event.getEventType();
		
		params.put("tokenId",tokenId);
		params.put("currentNodeName",currentNodeName);
		params.put("processInstanceId",processInstanceId);
		params.put("eventType",eventType);
		
		//params.put("plugin", PLUGIN_ID);
		postNewEvent ( new Event(jbpmNodeTopicName, params));
	}

}
