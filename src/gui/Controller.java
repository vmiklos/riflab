package gui;

import com.ibm.mq.*;

import java.util.LinkedList;
import java.util.List;

import com.ibm.mq.MQQueueManager;

import logic.*;

public class Controller {
	
	private MQQueueManager qMgr = null;
	private MQQueue reqAnalysisCheckingQueue;
	private MQQueue isConsistentSwitchQueue;
	private MQQueue reqCheckingRefineQueue;
	private MQQueue reqDesignQueue;
	private MQQueue designDevelopQueue;
	private MQQueue designTestQueue;
	private MQQueue designDocQueue;
	private MQQueue developIntegrateQueue;
	private MQQueue testIntegrateQueue;
	private MQQueue docIntegrateQueue;
	private MQQueue integrateVerificationQueue;
	private MQQueue verificationValidationQueue;
	

	public List<MQQueue> asList(MQQueue q1) {
		List<MQQueue> l = new LinkedList<MQQueue>();
		l.add(q1);
		return l;
	}
	
	public List<MQQueue> asList(MQQueue q1, MQQueue q2) {
		List<MQQueue> l = new LinkedList<MQQueue>();
		l.add(q1);
		l.add(q2);
		return l;
	}

	public List<MQQueue> asList(MQQueue q1, MQQueue q2, MQQueue q3) {
		List<MQQueue> l = new LinkedList<MQQueue>();
		l.add(q1);
		l.add(q2);
		l.add(q3);
		return l;
	}

	@SuppressWarnings("deprecation")
	private MQQueue makeQueue(String name) {
		if (qMgr == null) {
			try {
				qMgr = new MQQueueManager("QM_winxp_vm");
			} catch (MQException e) {
				return null;
			}
		}
		int openOptions = MQC.MQOO_INPUT_AS_Q_DEF |	MQC.MQOO_OUTPUT ;
		MQQueue result = null;
		try {
			result = qMgr.accessQueue("meres." + name, openOptions);
		} catch (MQException e) {
			return null;
		}
		return result;
	}
	
	public Controller(String name) {
		
		System.out.println(name);
		if ("ReqAnalysis".equals(name)) {
			reqAnalysisCheckingQueue = makeQueue("reqAnalysisCheckingQueue");
			new Gui("ReqAnalysis", new ReqAnalysis(), new LinkedList<MQQueue>(), asList(reqAnalysisCheckingQueue), 0, qMgr);
		}
		else if ("ReqChecking".equals(name)) {
			reqAnalysisCheckingQueue = makeQueue("reqAnalysisCheckingQueue");
			isConsistentSwitchQueue = makeQueue("isConsistentSwitchQueue");
			new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(isConsistentSwitchQueue), 1, qMgr);
		}
		else if ("isConsistentSwitch".equals(name)) {
			isConsistentSwitchQueue = makeQueue("isConsistentSwitchQueue");
			reqDesignQueue = makeQueue("reqDesignQueue");
			reqCheckingRefineQueue = makeQueue("reqCheckingRefineQueue");
			new Gui("isConsistentSwitch", new ReqChecking(), asList(isConsistentSwitchQueue), asList(reqDesignQueue, reqCheckingRefineQueue), 2, qMgr, 1);
		}
		else if ("ReqRefine".equals(name)) {
			reqCheckingRefineQueue = makeQueue("reqCheckingRefineQueue");
			reqDesignQueue = makeQueue("reqDesignQueue");
			new Gui("ReqRefine", new ReqChecking(), asList(reqCheckingRefineQueue), asList(reqDesignQueue), 3, qMgr);
		}
		else if ("Design".equals(name)) {
			reqDesignQueue = makeQueue("reqDesignQueue");
			designDevelopQueue = makeQueue("designDevelopQueue");
			designTestQueue = makeQueue("designTestQueue");
			designDocQueue = makeQueue("designDocQueue");
			new Gui("Design", new ReqChecking(), asList(reqDesignQueue), asList(designDevelopQueue, designTestQueue, designDocQueue), 4, qMgr);
		}
		else if ("Develop".equals(name)) {
			designDevelopQueue = makeQueue("designDevelopQueue");
			developIntegrateQueue = makeQueue("developIntegrateQueue");
			new Gui("Develop", new ReqChecking(), asList(designDevelopQueue), asList(developIntegrateQueue), 5, qMgr);
		}
		else if ("Test".equals(name)) {
			designTestQueue = makeQueue("designTestQueue");
			testIntegrateQueue = makeQueue("testIntegrateQueue");
			new Gui("Test", new ReqChecking(), asList(designTestQueue), asList(testIntegrateQueue), 6, qMgr);
		}
		else if ("Doc".equals(name)) {
			designDocQueue = makeQueue("designDocQueue");
			docIntegrateQueue = makeQueue("docIntegrateQueue");
			new Gui("Doc", new ReqChecking(), asList(designDocQueue), asList(docIntegrateQueue), 7, qMgr);
		}
		else if ("Integration".equals(name)) {
			developIntegrateQueue = makeQueue("developIntegrateQueue");
			testIntegrateQueue = makeQueue("testIntegrateQueue");
			docIntegrateQueue = makeQueue("docIntegrateQueue");
			integrateVerificationQueue = makeQueue("integrateVerificationQueue");
			new Gui("Integration", new ReqChecking(), asList(developIntegrateQueue, testIntegrateQueue, docIntegrateQueue), asList(integrateVerificationQueue), 8, qMgr);
		}
		else if ("Verification".equals(name)) {
			integrateVerificationQueue = makeQueue("integrateVerificationQueue");
			verificationValidationQueue = makeQueue("verificationValidationQueue");
			new Gui("Verification", new ReqChecking(), asList(integrateVerificationQueue), asList(verificationValidationQueue), 9, qMgr);
		}
		else if ("Validation".equals(name)) {
			verificationValidationQueue = makeQueue("verificationValidationQueue");
			new Gui("Validation", new ReqChecking(), asList(verificationValidationQueue), new LinkedList<MQQueue>(), 10, qMgr);
		}
		
		//new Gui("ReqChecking", new ReqChecking(), asList(reqAnalysisCheckingQueue), asList(isConsistentSwitchQueue));
		
		/*new Gui("isConsistentSwitch", null, asList(isConsistentSwitchQueue), asList(reqDesignQueue, reqCheckingRefineQueue),1);
		
		new Gui("ReqRefine", new ReqRefine(), asList(reqCheckingRefineQueue), asList(reqDesignQueue));
		new Gui("Design", new Design(), asList(reqDesignQueue), asList(designDevelopQueue, designTestQueue, designDocQueue));
		
		new Gui("Develop", new Develop(), asList(designDevelopQueue), asList(developIntegrateQueue));
		new Gui("Test", new Test(), asList(designTestQueue), asList(testIntegrateQueue));
		new Gui("Doc", new Doc(), asList(designDocQueue), asList(docIntegrateQueue));
		new Gui("Integration", new Integration(), asList(developIntegrateQueue, testIntegrateQueue, docIntegrateQueue), asList(integrateVerificationQueue));
		new Gui("Verification", new Verification(), asList(integrateVerificationQueue), asList(verificationValidationQueue));
		new Gui("Validation", new Validation(), asList(verificationValidationQueue), new LinkedList<BlockingQueue<Product>>());
		*/
	}

	public static void main(String[] args) {
		new Controller(args[0]);
	}
}