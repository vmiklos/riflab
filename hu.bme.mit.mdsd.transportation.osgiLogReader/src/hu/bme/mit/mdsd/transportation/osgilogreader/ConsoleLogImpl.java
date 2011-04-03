package hu.bme.mit.mdsd.transportation.osgilogreader;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

public class ConsoleLogImpl implements LogListener
{
    public void logged(LogEntry log)
    {
        if ((log.getMessage() != null) && (log.getLevel() == LogService.LOG_ERROR))
        	System.err.println("Error: [" + log.getBundle().getSymbolicName() + "] " + log.getMessage());
        else if ((log.getMessage() != null) && (log.getLevel() == LogService.LOG_WARNING))
            System.out.println("Warning: [" + log.getBundle().getSymbolicName() + "] " + log.getMessage());
        else if ((log.getMessage() != null) && (log.getLevel() == LogService.LOG_INFO))
            System.out.println("Info: [" + log.getBundle().getSymbolicName() + "] " + log.getMessage());
        else if ((log.getMessage() != null) && (log.getLevel() == LogService.LOG_DEBUG))
            System.out.println("Debug: [" + log.getBundle().getSymbolicName() + "] " + log.getMessage());
    }
}