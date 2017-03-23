package org.meveo.admin.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author Edward P. Legaspi
 **/
@WebListener
public class StartupServletListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		 System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");		
	}

}
