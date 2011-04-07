package hu.bme.mit.toolintegration.workflow.jbpm.eventhandler;


import hu.bme.mit.toolintegration.workflow.jbpm.Activator;
import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.JbpmNodeEvent.JbpmNodeEventType;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.EndState;

public class SensoriaEventActionHandler implements ActionHandler {
	
	private static final long serialVersionUID = -1710489188432523221L;

	
	public static final String EVENTTYPE_TOKEN_CREATED = "EVENTTYPE_TOKEN_CREATED";
	public static final String EVENTTYPE_TOKEN_REMOVED = "EVENTTYPE_TOKEN_REMOVED";
	@Override
	public void execute(ExecutionContext ec) throws Exception {
	//	SensoriaWorkflowViewPart s; s.
	//	ec.getToken().get
		
//********************************* sending event with JbpmNodeEventNotifier ***********************
//********************************* used by the OLD user interface *********************************
		
		//System.out.println("Execution "+ message+" the node "+ec.getNode().getName());
		ProcessInstance processInstance= ec.getContextInstance().getProcessInstance();
		long piid=processInstance.getId();
		long pdid=processInstance.getProcessDefinition().getId();
		String nodeName=ec.getNode().getName();
		String eventTypeString = ec.getEvent().getEventType();
		JbpmNodeEventType eventType=JbpmNodeEventType.dummy;
		if (eventTypeString.equals("node-enter")) eventType= JbpmNodeEventType.node_enter;
		if (eventTypeString.equals("node-leave")) eventType= JbpmNodeEventType.node_leave;
		JbpmNodeEvent e= new JbpmNodeEvent(nodeName,eventType,piid,pdid);
		e.setExecutionContext(ec);
		//ec.getJbpmContext().getSession().setCacheMode(CacheMode.IGNORE);
		
		//save Token informations to the database
		//if (ec.getJbpmContext().getSession().getTransaction() != null)
		//	ec.getJbpmContext().getSession().getTransaction().commit();
		ec.getJbpmContext().getSession().getTransaction().begin();
		ec.getJbpmContext().save(processInstance.getRootToken());
		
		//commit the actual tansaction
		//if (! (ec.getNode() instanceof EndState))
		ec.getJbpmContext().getSession().getTransaction().commit();
		//and starting a new one
		/*if(!("node-leave".startsWith(eventType) && ec.getNode() instanceof EndState))
			ec.getJbpmContext().getSession().beginTransaction();*/
		//ec.getJbpmContext().getSession().
		JbpmNodeEventNotifier.getObserverInstance().fireListeners(e);
		//Fork f= new Fork("",1,1);
		
//********************************* sending event with EventAdmin ***********************
//********************************* used by the ProcessManager user interface ***********
		
		Token token = ec.getToken();
		Event event = ec.getEvent();
		Activator.getDefault().postJbpmEvent(token,event);
	
	
	}

	public SensoriaEventActionHandler() {
		super();
		//Map params=new HashMap();
	}

}
