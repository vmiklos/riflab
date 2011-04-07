package hu.bme.mit.toolintegration.workflow.jbpm.eventhandler;

import org.jbpm.graph.exe.ExecutionContext;

public class JbpmNodeEvent {
	protected String nodeName;
	protected JbpmNodeEventType type;
	protected long piid;
	protected long pdid;
	protected ExecutionContext executionContext;
	
	public enum JbpmNodeEventType{
		node_enter, node_leave, process_started, process_suspended, process_ended, dummy;
	}
	public JbpmNodeEvent(){
		
	}
	public JbpmNodeEvent(String nodeName, JbpmNodeEventType type, long piid,long pdid) {
		super();
		this.nodeName = nodeName;
		this.type = type;
		this.piid = piid;
		this.pdid= pdid;
	}
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	
	public long getPiid() {
		return piid;
	}
	public void setPiid(long piid) {
		this.piid = piid;
	}
	public long getPdid() {
		return pdid;
	}
	public void setPdid(long pdid) {
		this.pdid = pdid;
	}
	public JbpmNodeEventType getType() {
		return type;
	}
	public void setType(JbpmNodeEventType type) {
		this.type = type;
	}
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
	


}
