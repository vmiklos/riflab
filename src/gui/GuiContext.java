package gui;

import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.naming.Context;

public class GuiContext {
	private QueueConnection queueConnection;
	private QueueSession queueSession;
	private Context ctx;

	public QueueConnection getQueueConnection() {
		return queueConnection;
	}
	public void setQueueConnection(QueueConnection queueConnection) {
		this.queueConnection = queueConnection;
	}
	public QueueSession getQueueSession() {
		return queueSession;
	}
	public void setQueueSession(QueueSession queueSession) {
		this.queueSession = queueSession;
	}
	public Context getCtx() {
		return ctx;
	}
	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
}
