package hu.bme.mit.mdsd.transportation.osgilogreader;

import java.util.Iterator;
import java.util.LinkedList;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
    private ConsoleLogImpl m_console = new ConsoleLogImpl();
    private LinkedList<LogReaderService> m_readers = new LinkedList<LogReaderService>();

    //  We use a ServiceListener to dynamically keep track of all the LogReaderService service being
    //  registered or unregistered
    private ServiceListener m_servlistener = new ServiceListener() {
        public void serviceChanged(ServiceEvent event)
        {
            BundleContext bc = event.getServiceReference().getBundle().getBundleContext();
            LogReaderService lrs = (LogReaderService)bc.getService(event.getServiceReference());
            if (lrs != null)
            {
                if (event.getType() == ServiceEvent.REGISTERED)
                {
                    m_readers.add(lrs);
                    lrs.addLogListener(m_console);
                } else if (event.getType() == ServiceEvent.UNREGISTERING)
                {
                    lrs.removeLogListener(m_console);
                    m_readers.remove(lrs);
                }
            }
        }
    };

    public void start(BundleContext context) throws Exception
    {
        // Get a list of all the registered LogReaderService, and add the console listener
        ServiceTracker logReaderTracker = new ServiceTracker(context, org.osgi.service.log.LogReaderService.class.getName(), null);
        logReaderTracker.open();
        Object[] readers = logReaderTracker.getServices();
        if (readers != null)
        {
            for (int i=0; i<readers.length; i++)
            {
                LogReaderService lrs = (LogReaderService)readers[i];
                m_readers.add(lrs);
                lrs.addLogListener(m_console);
            }
        }

        logReaderTracker.close();
       
        // Add the ServiceListener, but with a filter so that we only receive events related to LogReaderService
        String filter = "(objectclass=" + LogReaderService.class.getName() + ")";
        try {
            context.addServiceListener(m_servlistener, filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
    }

    public void stop(BundleContext context) throws Exception
    {
        for (Iterator<LogReaderService> i = m_readers.iterator(); i.hasNext(); )
        {
            LogReaderService lrs = i.next();
            lrs.removeLogListener(m_console);
            i.remove();
        }
    }

}