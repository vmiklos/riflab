package hu.bme.mit.toolintegration.workflow.jbpm;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Ágoston I.
 *
 */
public class Logger {
	
	public static void logError(String message, Throwable exception){
		Activator.getDefault().getLog().log(new Status(
				IStatus.ERROR,
				Activator.PLUGIN_ID,
				message,
				exception
		));
	}
	public static void logWarning(String message){
		Activator.getDefault().getLog().log(new Status(
				IStatus.WARNING,
				Activator.PLUGIN_ID,
				message
		));
			
	}
	public static void logInfo(String message){
		Activator.getDefault().getLog().log(new Status(
				IStatus.INFO,
				Activator.PLUGIN_ID,
				message	
		));
		
	}
}
