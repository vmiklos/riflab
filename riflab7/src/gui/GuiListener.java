package gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.jms.JMSException;

public class GuiListener extends WindowAdapter{
	GuiContext guiContext;


	public GuiListener(GuiContext guiContext) {
		this.guiContext = guiContext;
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		try {
			guiContext.getQueueConnection().close();
		} catch (JMSException e) {
			return;
		}
	}
}
