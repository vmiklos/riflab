package hu.bme.mit.toolintegration.workflow.jbpm;

import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.JbpmNodeEvent;
import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.JbpmNodeEvent.JbpmNodeEventType;
import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.JbpmNodeEventNotifier;
import hu.bme.mit.toolintegration.workflow.jbpm.eventhandler.SensoriaEventActionHandler;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipInputStream;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.GraphSession;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.instantiation.Delegation;

public class JbpmExecutor implements IJbpmExecutorTool {

	// String defaultConfigFile = "config/jbpm.cfg.xml";
	String defaultConfigFile = "config/datanodeadd/jbpm.cfg.xml";
	String transactionDisabledConfigFile = "config/datanodeadd/TransactionDi_jbpm.cfg.xml";
	private String GENERIC_PROCESS_STORE_FILE_NAME = "GENERIC_PROCESS_STORE";

	private class SensoriaEvent extends Event {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8707761249639956501L;

		public SensoriaEvent(String eventType) {
			super(eventType);
			Delegation d = new Delegation();
			d.setClassName(SensoriaEventActionHandler.class.getName());

			addAction(new Action(d));
		}
	}

	private class WorkerThreadKiller implements Runnable {

		ReentrantLock lock;
		ProcessInstance processInstance;

		public WorkerThreadKiller(ReentrantLock lock,
				ProcessInstance processInstance) {
			this.lock = lock;
			// lock.lock();
			this.processInstance = processInstance;
		}

		@Override
		public void run() {
			lock.lock(); // move this call here, to ensure that our thread owns
							// the monitor of the lock
			while (!(processInstance.hasEnded() || processInstance
					.isTerminatedImplicitly())) {

				synchronized (this) {
					try {
						wait(5000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			lock.unlock();
		}
	}

	// Legacy interface method
	@Override
	public void executeProcessArchive(String path) {
		// tODO : change to update of process deifnition
		// tODO : user-supplied config file

		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		config.startJobExecutor();
		ProcessInstance processInstance;
		try {
			ZipInputStream zipInputStream = new ZipInputStream(
					new FileInputStream(path));
			ProcessDefinition processDefinition = ProcessDefinition
					.parseParZipInputStream(zipInputStream);
			context.deployProcessDefinition(processDefinition);
			processInstance = new ProcessInstance(processDefinition);

			// start process
			processInstance.signal();

			Thread.sleep(5000L);

			// wait for process end, and kill worker threads afterwards
			ReentrantLock lock = new ReentrantLock();
			Thread t = new Thread(new WorkerThreadKiller(lock, processInstance));
			t.start();

			lock.lock();
			config.getJobExecutor().stopAndJoin();
			lock.unlock();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.close();
		}

	}

	// Legacy interface method
	@Override
	public void executeProcessDefinition(String xmlProcessDefinition) {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		config.startJobExecutor();
		try {
			ProcessDefinition processDefinition = ProcessDefinition
					.parseXmlString(xmlProcessDefinition);
			context.deployProcessDefinition(processDefinition);
			ProcessInstance processInstance = new ProcessInstance(
					processDefinition);
			// start process
			processInstance.signal();

			// wait for process end, and kill worker threads afterwards
			ReentrantLock lock = new ReentrantLock();
			Thread t = new Thread(new WorkerThreadKiller(lock, processInstance));
			t.start(); // <-- this thread will gain the lock
			// t will block the lock until all worker threads die
			lock.lock(); // <-- main thead is blocked until the
							// workerthreadkiller is executing
			config.getJobExecutor().stopAndJoin();
			lock.unlock();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.close();
		}

	}

	@Override
	public void clearDatabase() {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		try {
			config.dropSchema();
			config.createSchema();
		} finally {
			context.close();
		}
	}

	@Override
	public void createSchema() {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		try {
			config.createSchema();
		} finally {
			context.close();
		}
	}

	@Override
	public int deployProcessArchive(String path) {

		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		config.startJobExecutor();
		int pdid = -1;
		try {

			ZipInputStream zipInputStream = new ZipInputStream(
					new FileInputStream(path));
			ProcessDefinition processDefinition = ProcessDefinition
					.parseParZipInputStream(zipInputStream);
			// processDefinition.getNode("name").
			context.deployProcessDefinition(processDefinition);
			pdid = (int) processDefinition.getId();
			// processDefinition.getId()
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.close();
		}

		return pdid;
	}

	@Override
	public void executeProcessInstance(String processID,
			HashMap<String, String> variableMap) throws InterruptedException {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(transactionDisabledConfigFile);
		JbpmContext context = config.createJbpmContext();
		config.startJobExecutor();
		ProcessInstance processInstance=null;
		long id=-1, piid =-1, pdid = -1;
		try {
			/*
			 * ProcessDefinition processDefinition = ProcessDefinition
			 * .parseXmlString(xmlProcessDefinition);
			 * context.deployProcessDefinition(processDefinition);
			 */

			 id = Long.parseLong(processID);
			processInstance = context.getProcessInstance(id);
			// processInstance.getContextInstance().
			if (processInstance == null) {
				throw new JbpmException("ProcessInstanceID " + processID
						+ " doesn't exist in database.");
	
			}
			if (variableMap != null && !variableMap.isEmpty()) {
				processInstance.getContextInstance().setTransientVariables(
						variableMap);
			}
			 piid = id;
			 pdid = processInstance.getProcessDefinition().getId();

			// set The current Date
			context.getSession().beginTransaction();
			processInstance.setStart(Calendar.getInstance().getTime());
			context.save(processInstance);
			context.getSession().getTransaction().commit();

			// fire process_start event for OLD interface
			JbpmNodeEvent event = new JbpmNodeEvent("",
					JbpmNodeEventType.process_started, piid, pdid);
			JbpmNodeEventNotifier.getObserverInstance().fireListeners(event);

			// fire process_start event for Process Manager interface
			Activator.getDefault().postJbpmEvent(
					processInstance.getRootToken(),
					new Event(Event.EVENTTYPE_PROCESS_START));

			try {
			// start process
			processInstance.signal();
			} catch (JbpmException e){
				throw new JbpmException ("Error during executing processinstance with id: "+piid,e);
			}

		}finally {
			// set the current date
			context.getSession().beginTransaction();
			processInstance.setEnd(/*Calendar.getInstance().getTime()*/new Date());
			context.save(processInstance);
			context.getSession().getTransaction().commit();

			// fire process_ended event for OLD interface
			JbpmNodeEvent event = new JbpmNodeEvent("", JbpmNodeEventType.process_ended,
					piid, pdid);
			JbpmNodeEventNotifier.getObserverInstance().fireListeners(event);
			// fire process_ended event for Process Manager interface
			Activator.getDefault().postJbpmEvent(
					processInstance.getRootToken(),
					new Event(Event.EVENTTYPE_PROCESS_END));

			// wait for process end, and kill worker threads afterwards
			ReentrantLock lock = new ReentrantLock();
			Thread t = new Thread(new WorkerThreadKiller(lock, processInstance));
			t.start(); // <-- this thread will gain the lock
			// t will block the lock until all worker threads die
			lock.lock(); // <-- main thead is blocked until the
							// workerthreadkiller is executing
			config.getJobExecutor().stopAndJoin();
			lock.unlock();
			context.close();
			config.close();
		}

	}

	/*
	 * @see hu.bme.mit.toolintegration.workflow.jbpm.IJbpmExecutorTool#
	 * createNewProcessInstance(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public int createNewProcessInstance(String processDefinitionName,
			String processDefinitionId, String processInstanceName) {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		config.startJobExecutor();
		int piid = -1;
		// org.jbpm.db.GraphSession m;m.findAllProcessDefinitionVersions(arg0)
		try {

			GraphSession graphS = context.getGraphSession();
			@SuppressWarnings("unchecked")
			List<ProcessDefinition> l = graphS
					.findAllProcessDefinitionVersions(processDefinitionName);
			ProcessDefinition p = null;

			for (ProcessDefinition p2 : l) {
				try {
					if (p.getId() == Long.parseLong(processDefinitionId)) {
						p = p2;
					}
				} catch (NumberFormatException e) {
					Logger.logError("ProcessDefinition id must be a number", e);
					throw new JbpmException ("ProcessDefinition id must be a number",e);
				}
			}

			ProcessInstance processInstance = p.createProcessInstance();
			processInstance.setStart(null);
			processInstance.setKey(processInstanceName);
			context.save(processInstance);
			piid = (int) processInstance.getId();
		} /*catch (Exception e) {
			e.printStackTrace();
		} */finally {
			context.close();
		}
		return piid;
	}

	@Override
	public HashMap<String, String> addParameterToMap(
			HashMap<String, String> map, String variableName,
			String variableValue) {
		if (map == null)
			map = new HashMap<String, String>();
		map.put(variableName, variableValue);
		return map;
	}

	@Override
	public HashMap<String, String> createProcessInstance(
			String processDefinitionId, String processInstanceName,
			HashMap<String, String> parameters) {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		config.startJobExecutor();
		try {
			GraphSession graphS = context.getGraphSession();
			long pdid = Long.valueOf(processDefinitionId);
			ProcessDefinition p = graphS.getProcessDefinition(pdid);
			if (p == null) {
				Logger.logError("ProcessDefinition with id " + pdid
						+ " doesn't exist.", null);
				throw new JbpmException ("ProcessDefinition with id " + pdid
						+ " doesn't exist in db.");
			}
			ProcessInstance processInstance = p.createProcessInstance();
			processInstance.setStart(null);
			processInstance.setKey(processInstanceName);
			if (parameters != null && !parameters.isEmpty()) {
				processInstance.getContextInstance().setVariables(parameters);
			}
			context.save(processInstance);
			return getProcessInstanceProperty(processInstance);
		} catch (NumberFormatException e) {
			Logger.logError("Processdefinitionid must be a number (id was"
					+ processDefinitionId + " )", e);
			throw new JbpmException("Processdefinitionid must be a number (id was"
					+ processDefinitionId + " )",e);
		}// catch (Exception e) {
			// Logger.logError("createNewProcessInstance", e);
		// }
		finally {
			context.close();
		}

	}

	@Override
	public boolean deleteProcessDefinition(String processDefinitionId) {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		try {
			// parse processinstanceid
			long pdid = Long.valueOf(processDefinitionId);
			ProcessDefinition pd = context.getGraphSession()
					.getProcessDefinition(pdid);
			if (pd == null) {
				Logger.logError("ProcessDefinition with id "
						+ processDefinitionId + " doesn't exist.", null);
				throw new JbpmException("ProcessDefinition with id "
						+ processDefinitionId + " doesn't exist.");
			}
			// this method deletes all instances first
			context.getGraphSession().deleteProcessDefinition(pdid);

		} catch (NumberFormatException e) {
			Logger.logError("ProcessDefinitionId must be a number (id was "
					+ processDefinitionId + " )", e);
			throw new JbpmException("ProcessDefinitionId must be a number (id was "
					+ processDefinitionId + " )", e);
		} /*catch (Exception e) {
			Logger.logError(
					"Error in delete operation  (processDefinitionId was "
							+ processDefinitionId + " )", e);
			return false;
		} */finally {
			context.close();
			config.close();
		}
		return true;
	}

	@Override
	public boolean deleteProcessInstance(String processInstanceId) {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		try {
			// parse processinstanceid
			long pid = Long.valueOf(processInstanceId);
			ProcessInstance pi = context.getGraphSession().getProcessInstance(
					pid);
			if (pi == null) {
				Logger.logError("ProcessInstance with id " + processInstanceId
						+ " doesn't exist.", null);
				throw new JbpmException ("ProcessInstance with id " + processInstanceId
						+ " doesn't exist.");
			}
			if (!pi.hasEnded()) {
				pi.getRootToken().end();
			}
			context.getGraphSession().deleteProcessInstance(pid);

		} catch (NumberFormatException e) {
			Logger.logError("ProcessInstanceId must be a number (id was"
					+ processInstanceId + " )", e);
			throw new JbpmException ("ProcessInstanceId must be a number (id was"
					+ processInstanceId + " )", e);
		} /*catch (Exception e) {
			Logger.logError(
					"Error in delete operation  (processInstanceId was "
							+ processInstanceId + " )", e);
			return false;
		} */finally {
			context.close();
			config.close();
		}
		return true;
	}

	@Override
	public HashMap<String, String> deployProcessDefinition(
			String jPDLSourceString) throws Exception {
		return deployProcessDefinitionWithGPDId(jPDLSourceString, null);

	}

	@Override
	public HashMap<String, String> deployProcessDefinitionWithGPDId(
			String jPDLSourceString, String GPDId) throws Exception {
		JbpmConfiguration config = null;
		JbpmContext context = null;
		try {
		
			config = JbpmConfiguration.getInstance(defaultConfigFile);
			context = config.createJbpmContext();
			config.startJobExecutor();

			// parsing process definition
			ProcessDefinition processDefinition = ProcessDefinition
					.parseXmlString(jPDLSourceString);
			// adding handlers to handle NODE_ENTER and NODE_LEAVE events, if
			// they haven't been added yet
			if (processDefinition.getEvent(Event.EVENTTYPE_NODE_ENTER) == null)
				processDefinition.addEvent(new SensoriaEvent(
						Event.EVENTTYPE_NODE_ENTER));
			if (processDefinition.getEvent(Event.EVENTTYPE_NODE_LEAVE) == null)
				processDefinition.addEvent(new SensoriaEvent(
						Event.EVENTTYPE_NODE_LEAVE));
			//Adding eventHandler for TOKEN_CREATED and TOKEN_REMOVED Events
			processDefinition.addEvent(new SensoriaEvent(
					SensoriaEventActionHandler.EVENTTYPE_TOKEN_CREATED));
			processDefinition.addEvent(new SensoriaEvent(
					SensoriaEventActionHandler.EVENTTYPE_TOKEN_REMOVED));
			// if (processDefinition.getEvent(Event.EVENTTYPE_PROCESS_START)==
			// null)
			// processDefinition.addEvent(new
			// SensoriaEvent(Event.EVENTTYPE_PROCESS_START));
			// if (processDefinition.getEvent(Event.EVENTTYPE_PROCESS_END)==
			// null)
			// processDefinition.addEvent(new
			// SensoriaEvent(Event.EVENTTYPE_PROCESS_END));

			context.deployProcessDefinition(processDefinition);
			// store the GENERIC_PROCESS_STORE_ID (in a file in DB)
			if (GPDId != null) {
				FileDefinition definition = processDefinition
						.getFileDefinition();
				if (definition == null) {
					definition = new FileDefinition();
					processDefinition.addDefinition(definition);
				}
				definition.addFile(GENERIC_PROCESS_STORE_FILE_NAME,
						GPDId.getBytes());
			}

			return getProcessDefinitionProperty(processDefinition);
		} /*catch (JbpmException ex) {
			throw new Exception(ex);
		}*/ finally {

			if (context != null) {
				context.close();
			}
			if (config != null) {
				config.close();
			}
		}
	}

	@Override
	public ArrayList<HashMap<String, String>> getAllProcessDefinitions() {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		try {

			@SuppressWarnings("unchecked")
			List<ProcessDefinition> procdefs = context.getGraphSession()
					.findAllProcessDefinitions();
			ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
			for (ProcessDefinition p : procdefs) {
				HashMap<String, String> propertysMap = getProcessDefinitionProperty(p);
				ret.add(propertysMap);
			}
			return ret;
		} finally {
			context.close();
			config.close();
		}
	}

	@Override
	public ArrayList<HashMap<String, String>> getAllProcessInstances() {
		JbpmConfiguration config = JbpmConfiguration
				.getInstance(defaultConfigFile);
		JbpmContext context = config.createJbpmContext();
		try {
			@SuppressWarnings("unchecked")
			List<ProcessDefinition> procdefs = context.getGraphSession()
					.findAllProcessDefinitions();
			ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();

			for (ProcessDefinition p : procdefs) {
				@SuppressWarnings("unchecked")
				List<ProcessInstance> processInstanceList = context
						.getGraphSession().findProcessInstances(p.getId());
				for (ProcessInstance pi : processInstanceList) {
					HashMap<String, String> propertysMap = getProcessInstanceProperty(pi);
					ret.add(propertysMap);
				}
			}
			return ret;
		} finally {

			if (context != null) {
				context.close();
			}
			if (config != null) {
				config.close();
			}
		}
	}

	protected HashMap<String, String> getProcessInstanceProperty(
			ProcessInstance pi) {
		DateFormat df = DateFormat.getDateTimeInstance();
		HashMap<String, String> propertysMap = new HashMap<String, String>();
		propertysMap.put("process_definition_id",
				String.valueOf(pi.getProcessDefinition().getId()));
		propertysMap.put("process_instance_id", String.valueOf(pi.getId()));
		propertysMap.put("start_date", pi.getStart() == null ? "not started"
				: df.format(pi.getStart()));
		propertysMap.put("end_date",
				pi.getEnd() == null ? "not ended" : df.format(pi.getEnd()));
		propertysMap.put("name", pi.getKey());
		propertysMap.put("token_list", getTokenInfoString(pi));

		return propertysMap;
	}

	protected String getTokenInfoString(ProcessInstance pi) {
		StringBuilder s = new StringBuilder();
		for (Object o : pi.findAllTokens()) {
			Token token = (Token) o;
			s.append("(" + token.getId() + ";" + token.getNode().getName()
					+ ")");
		}
		return s.toString();
	}

	protected HashMap<String, String> getProcessDefinitionProperty(
			ProcessDefinition pd) {
		HashMap<String, String> propertysMap = new HashMap<String, String>();
		propertysMap.put("process_definition_id", String.valueOf(pd.getId()));
		propertysMap.put("name",
				"" + pd.getName() + " v" + String.valueOf(pd.getVersion()));
		// propertysMap.put("description", pd.getDescription());
		// propertysMap.put("version", String.valueOf(pd.getVersion()));
		String gpsId = "";
		try {
			// this method throws a JbpmException if file named
			// GENERIC_PROCESS_STORE_FILE_NAME doesn't exists in DB
			byte[] gpsIdBytes = pd.getFileDefinition().getBytes(
					GENERIC_PROCESS_STORE_FILE_NAME);
			gpsId = /* gpsIdBytes == null ? "" : */new String(gpsIdBytes);
		} catch (JbpmException e) {
			// do nothing
		}
		propertysMap.put("generic_process_definition_id", gpsId);
		return propertysMap;
	}

}
