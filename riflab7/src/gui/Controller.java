package gui;

import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import entities.Product;

import logic.*;

public class Controller {

	// private MQQueueManager qMgr = null;
	private GuiContext guiContext;
	private Queue initQueue;
	private Queue reqAnalysisCheckingQueue;
	private Queue isConsistentSwitchQueue;
	private Queue reqCheckingRefineQueue;
	private Queue reqDesignQueue;
	private Queue designDevelopQueue;
	private Queue designTestQueue;
	private Queue designDocQueue;
	private Queue developIntegrateQueue;
	private Queue testIntegrateQueue;
	private Queue docIntegrateQueue;
	private Queue integrateVerificationQueue;
	private Queue verificationValidationQueue;


	public List<Queue> asList(Queue q1) {
		List<Queue> l = new LinkedList<Queue>();
		l.add(q1);
		return l;
	}

	public List<Queue> asList(Queue q1, Queue q2) {
		List<Queue> l = new LinkedList<Queue>();
		l.add(q1);
		l.add(q2);
		return l;
	}

	public List<Queue> asList(Queue q1, Queue q2, Queue q3) {
		List<Queue> l = new LinkedList<Queue>();
		l.add(q1);
		l.add(q2);
		l.add(q3);
		return l;
	}

	private Queue makeQueue(String name) {
		Queue result = null;

		try {
			if (guiContext == null) {
				Context ctx = new InitialContext();
				QueueConnectionFactory queueConnectionFactory = 
					(QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
				QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
				QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				queueConnection.start();
				
				guiContext = new GuiContext();
				guiContext.setCtx(ctx);
				guiContext.setQueueConnection(queueConnection);
				guiContext.setQueueSession(queueSession);
			}

			result = (Queue) guiContext.getCtx().lookup("queue/" + name);
		} catch (JMSException e) {
			e.printStackTrace();
			return null;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}

		return result;
	}

	public Controller(String name, boolean isConsistent) {
		System.out.println(name);
		if ("ReqAnalysis".equals(name)) {
			int id = 0;
			initQueue = makeQueue("initQueue");
			
			for(int i = 0; i < 10; i++) {
				Product product = new Product(id++);
				product.setAnswer(isConsistent);
				Task.put(initQueue, product, guiContext);
			}
			reqAnalysisCheckingQueue = makeQueue("reqAnalysisCheckingQueue");
			new Gui("ReqAnalysis", new ReqAnalysis(), asList(initQueue), asList(reqAnalysisCheckingQueue), 0, guiContext).loop();
		}
		else if ("ReqChecking".equals(name)) {
			reqAnalysisCheckingQueue = makeQueue("reqAnalysisCheckingQueue");
			isConsistentSwitchQueue = makeQueue("isConsistentSwitchQueue");
			new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(isConsistentSwitchQueue), 1, guiContext).loop();
		}
		else if ("isConsistentSwitch".equals(name)) {
			isConsistentSwitchQueue = makeQueue("isConsistentSwitchQueue");
			reqDesignQueue = makeQueue("reqDesignQueue");
			reqCheckingRefineQueue = makeQueue("reqCheckingRefineQueue");
			new Gui("isConsistentSwitch", null, asList(isConsistentSwitchQueue), asList(reqDesignQueue, reqCheckingRefineQueue), 2, guiContext, 1).loop();
		}
		else if ("ReqRefine".equals(name)) {
			reqCheckingRefineQueue = makeQueue("reqCheckingRefineQueue");
			reqDesignQueue = makeQueue("reqDesignQueue");
			new Gui("ReqRefine", new ReqRefine(), asList(reqCheckingRefineQueue), asList(reqDesignQueue), 3, guiContext).loop();
		}
		else if ("Design".equals(name)) {
			reqDesignQueue = makeQueue("reqDesignQueue");
			designDevelopQueue = makeQueue("designDevelopQueue");
			designTestQueue = makeQueue("designTestQueue");
			designDocQueue = makeQueue("designDocQueue");
			new Gui("Design", new Design(), asList(reqDesignQueue), asList(designDevelopQueue, designTestQueue, designDocQueue), 4, guiContext).loop();
		}
		else if ("Develop".equals(name)) {
			designDevelopQueue = makeQueue("designDevelopQueue");
			developIntegrateQueue = makeQueue("developIntegrateQueue");
			new Gui("Develop", new Develop(), asList(designDevelopQueue), asList(developIntegrateQueue), 5, guiContext).loop();
		}
		else if ("Test".equals(name)) {
			designTestQueue = makeQueue("designTestQueue");
			testIntegrateQueue = makeQueue("testIntegrateQueue");
			new Gui("Test", new Test(), asList(designTestQueue), asList(testIntegrateQueue), 6, guiContext).loop();
		}
		else if ("Doc".equals(name)) {
			designDocQueue = makeQueue("designDocQueue");
			docIntegrateQueue = makeQueue("docIntegrateQueue");
			new Gui("Doc", new Doc(), asList(designDocQueue), asList(docIntegrateQueue), 7, guiContext).loop();
		}
		else if ("Integration".equals(name)) {
			developIntegrateQueue = makeQueue("developIntegrateQueue");
			testIntegrateQueue = makeQueue("testIntegrateQueue");
			docIntegrateQueue = makeQueue("docIntegrateQueue");
			integrateVerificationQueue = makeQueue("integrateVerificationQueue");
			new Gui("Integration", new Integration(), asList(developIntegrateQueue, testIntegrateQueue, docIntegrateQueue), asList(integrateVerificationQueue), 8, guiContext).loop();
		}
		else if ("Verification".equals(name)) {
			integrateVerificationQueue = makeQueue("integrateVerificationQueue");
			verificationValidationQueue = makeQueue("verificationValidationQueue");
			new Gui("Verification", new Verification(), asList(integrateVerificationQueue), asList(verificationValidationQueue), 9, guiContext).loop();
		}
		else if ("Validation".equals(name)) {
			verificationValidationQueue = makeQueue("verificationValidationQueue");
			new Gui("Validation", new Validation(), asList(verificationValidationQueue), new LinkedList<Queue>(), 10, guiContext).loop();
		}

	}

	public static void main(String[] args) {
		new Controller(args[0], (args.length>=2 && args[1].equalsIgnoreCase("true")));
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
