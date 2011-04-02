package hu.bme.mit.mdsd.transportation.osgilogreader;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class ConsoleLogImpl implements LogListener
{
    public void logged(LogEntry log)
    {
        if (log.getMessage() != null)
            System.out.println("[" + log.getBundle().getSymbolicName() + "] " + log.getMessage());

    }
}