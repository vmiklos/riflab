package riflab3;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import riflab3.entities.Product;
import riflab3.logic.ReqAnalysis;
import riflab3.logic.ReqChecking;
import riflab3.logic2.services.DesignService;
import riflab3.logic2.services.DevelopService;
import riflab3.logic2.services.DocService;
import riflab3.logic2.services.IntegrationService;
import riflab3.logic2.services.ReqRefineService;
import riflab3.logic2.services.TestService;
import riflab3.logic2.services.ValidationService;
import riflab3.logic2.services.VerificationService;

import org.osgi.service.log.LogService;

// referenced in component.xml
public class ServiceComponent implements CommandProvider {

	private DesignService designService;
	private DevelopService developService;
	private DocService docService;
	private IntegrationService integrationService;
	private ReqRefineService reqRefineService;
	private TestService testService;
	private ValidationService validationService;
	private VerificationService verificationService;
	
	private LogService logService;

	private Map<Integer, Workflow> workflows; 

	public ServiceComponent() {
		workflows = new HashMap<Integer, Workflow>();
	}

	public List<Product> asList(Product p1) {
		List<Product> l = new LinkedList<Product>();
		l.add(p1);
		return l;
	}

	public List<Product> asList(Product q1, Product q2, Product q3) {
		List<Product> l = new LinkedList<Product>();
		l.add(q1);
		l.add(q2);
		l.add(q3);
		return l;
	}

	public void _riflab3(CommandInterpreter ci) {
		try{
			String arg = ci.nextArgument();
			boolean isConsistent = false;
			if (arg == null) {
				ci.print("usage: riflab3 [true|false]\n");
				ci.print("\n");
				ci.print("the first parameter specifies if the requirement will be consistent or not\n");
				return;
			} else {
				if (arg.equalsIgnoreCase("true")) {
					isConsistent = true;
				} else if (arg.equalsIgnoreCase("false")) {
					isConsistent = false;
				} else {
					ci.println("error: not true|false");
					return;
				}
			}
			Product p1, p2, p3, p4;

			ReqAnalysis reqAnalysis = new ReqAnalysis();
			p1 = reqAnalysis.doIt(new LinkedList<Product>());
			ci.println("after ReqAnalysis: " + p1);

			ReqChecking reqChecking = new ReqChecking();
			p1 = reqChecking.doIt(asList(p1));
			ci.println("after ReqChecking: " + p1);

			if (!isConsistent) {
				p1 = reqRefineService.doReqRefine(asList(p1));
				ci.println("after ReqRefine: " + p1);
			}

			p1 = designService.doDesign(asList(p1));
			ci.println("after Design: " + p1);

			p2 = developService.doDevelopment(asList(p1));
			ci.println("after Development: " + p2);

			p3 = testService.doTesting(asList(p1));
			ci.println("after Testing: " + p3);

			p4 = docService.doDocCreation(asList(p1));
			ci.println("after Doc: " + p4);

			p1 = integrationService.doIntegration(asList(p2, p3, p4));
			ci.println("after Integration: " + p1);

			p1 = verificationService.doVerification(asList(p1));
			ci.println("after Verification: " + p1);

			p1 = validationService.doValidation(asList(p1));
			ci.println("after Validation: " + p1);
		} catch (NullPointerException e) {
			ci.println("service call failed!");
		}
	}

	public void _riflab3wf(CommandInterpreter ci) {
		String arg = ci.nextArgument();
		if (arg == null) {
			ci.print("usage: riflab3_wf command\n");
			ci.print("\n");
			ci.print("command:\n");
			ci.print("  create id [true|false]: Creates a new workflow, \n");
			ci.print("         where the requirements are consistent \n");
			ci.print("         (true), or inconsistent (false).\n");
			ci.print("         The workflow will be identified with id,\n");
			ci.print("         and will not be created if one exists with \n");
			ci.print("         the same id.\n");
			ci.print("  step id: Steps the workflow identified by id. \n");
			ci.print("  kill id: Kills the workflow identified by id. \n");
			ci.print("  ls: List available workflows. \n");
			return;
		} else {
			try {
				if (arg.equalsIgnoreCase("create")) {
					Integer id = getInteger(ci);
					Boolean isConsistent = getBoolean(ci);
					if (workflows.containsKey(id)) {
						ci.println("A workflow with this number exists, new workflow not created.");
						ci.println("You can kill the workflow to replace it with a new one.");
					} else {
						Workflow workflow = new Workflow(isConsistent);
						workflows.put(id, workflow);
						ci.println("after create: " + workflow.getP1());
					}
				} else if (arg.equalsIgnoreCase("step")) {
					Integer id = getInteger(ci);
					Workflow workflow = workflows.get(id);

					if (workflow == null) {
						ci.println("Bad id.");
						return;
					}

					step(workflow, id, ci);
				} else if (arg.equalsIgnoreCase("kill")) {
					Integer id = getInteger(ci);
					if (workflows.containsKey(id)) {
						workflows.remove(id);
						ci.println("Workflow " + id + " killed.");
					} else {
						ci.println("Workflow does not exist.");
					}
				} else if (arg.equalsIgnoreCase("ls")) {
					logService.log(LogService.LOG_DEBUG, "HELLOO!!!!!");
					
					for(Integer id : workflows.keySet()) {
						System.out.println("Id: " + id + ";");
					}
				} else {
					ci.println("Not recognized command.");
				}
			} catch (Exception e) {
				ci.println("Some error occured.");
				ci.println(e.getMessage());
			}
		}
	}
	
	void step(Workflow workflow, Integer id, CommandInterpreter ci) throws Exception {
		switch (workflow.getStep()) {
		case 0:
			ReqAnalysis reqAnalysis = new ReqAnalysis();
			workflow.setP1(reqAnalysis.doIt(new LinkedList<Product>()));
			ci.println("after ReqAnalysis: " + workflow.getP1());
			break;
			
		case 1:
			ReqChecking reqChecking = new ReqChecking();
			workflow.setP1(reqChecking.doIt(asList(workflow.getP1())));
			ci.println("after ReqChecking: " + workflow.getP1());
			break;
			
		case 2:
			if (reqRefineService == null) throw new Exception("reqRefineService is not available.");
			if (!workflow.isConsistent()) {
				workflow.setP1(reqRefineService.doReqRefine(asList(workflow.getP1())));
				ci.println("after ReqRefine: " + workflow.getP1());
			} else {
				workflow.setStep(workflow.getStep()+1);
				step(workflow, id, ci);
				workflow.setStep(workflow.getStep()-1);
			}
			break;

		case 3:
			if (designService == null) throw new Exception("designService is not available.");
			workflow.setP1(designService.doDesign(asList(workflow.getP1())));
			ci.println("after Design: " + workflow.getP1());
			break;

		case 4:
			if (developService == null) throw new Exception("developService is not available.");
			workflow.setP2(developService.doDevelopment(asList(workflow.getP1())));
			ci.println("after Development: " + workflow.getP2());
			break;

		case 5:
			if (testService == null) throw new Exception("testService is not available.");
			workflow.setP3(testService.doTesting(asList(workflow.getP1())));
			ci.println("after Testing: " + workflow.getP3());
			break;

		case 6:
			if (docService == null) throw new Exception("docService is not available.");
			workflow.setP4(docService.doDocCreation(asList(workflow.getP1())));
			ci.println("after Development: " + workflow.getP4());
			break;

		case 7:
			if (integrationService == null) throw new Exception("integrationService is not available.");
			workflow.setP1(integrationService.doIntegration(asList(workflow.getP2(), workflow.getP3(), workflow.getP4())));
			ci.println("after Integration: " + workflow.getP1());
			break;

		case 8:
			if (verificationService == null) throw new Exception("verificationService is not available.");
			workflow.setP1(verificationService.doVerification(asList(workflow.getP1())));
			ci.println("after Verification: " + workflow.getP1());
			break;

		case 9:
			if (validationService == null) throw new Exception("validationService is not available.");
			workflow.setP1(validationService.doValidation(asList(workflow.getP1())));
			ci.println("after Validation: " + workflow.getP1());
			break;

		case 10:
			workflows.remove(id);
			ci.println("workflow " + id + " done. Dequeued.");
			break;
			
		default:
			ci.println("Internal error.");
			break;
		}

		workflow.setStep(workflow.getStep()+1);
	}

	Integer getInteger(CommandInterpreter ci) throws Exception {
		Integer intValue;
		try {
			String str;
			str = ci.nextArgument();
			intValue = Integer.valueOf(str);
		} catch(NumberFormatException e) {
			throw new Exception(e.getMessage());
		}
		return intValue;
	}

	Boolean getBoolean(CommandInterpreter ci) throws Exception {
		Boolean boolValue;
		try {
			String str;
			str = ci.nextArgument();
			boolValue = Boolean.valueOf(str);
		} catch(NumberFormatException e) {
			throw new Exception(e.getMessage());
		}
		return boolValue;
	}

	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("	riflab3 - runs a workflow from the beginning to the end\n");
		buffer.append("	riflab3_wf - handles the lifecycles of workflows\n");
		return buffer.toString();
	}

	public void setDesign(DesignService l2) {
		this.designService = l2;
	}

	public void unsetDesign(DesignService l2) {
		this.designService = null;
	}
	public void setDevelop(DevelopService l2) {
		this.developService = l2;
	}

	public void unsetDevelop(DevelopService l2) {
		this.developService = null;
	}
	public void setDoc(DocService l2) {
		this.docService = l2;
	}

	public void unsetDoc(DocService l2) {
		this.docService = null;
	}
	public void setIntegration(IntegrationService l2) {
		this.integrationService = l2;
	}

	public void unsetIntegration(IntegrationService l2) {
		this.integrationService = null;
	}
	public void setReqRefine(ReqRefineService l2) {
		this.reqRefineService = l2;
	}

	public void unsetReqRefine(ReqRefineService l2) {
		this.reqRefineService = null;
	}
	public void setTest(TestService l2) {
		this.testService = l2;
	}

	public void unsetTest(TestService l2) {
		this.testService = null;
	}
	public void setValidation(ValidationService l2) {
		this.validationService = l2;
	}

	public void unsetValidation(ValidationService l2) {
		this.validationService = null;
	}
	public void setVerification(VerificationService l2) {
		this.verificationService = l2;
	}

	public void unsetVerification(VerificationService l2) {
		this.verificationService = null;
	}
	
	protected void bindLog(LogService log) {
	    this.logService = log;
	}

	protected void unbindLog(LogService log) {
	    this.logService = null;
	}

}