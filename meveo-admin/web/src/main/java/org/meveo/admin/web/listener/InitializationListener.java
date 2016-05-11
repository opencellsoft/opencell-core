package org.meveo.admin.web.listener;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.meveo.admin.web.filter.config.Page;
import org.meveo.admin.web.filter.config.PageAccess;
import org.meveo.util.view.PagePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class InitializationListener implements ServletContextListener {

	private static final String PAGE_ACCESS_FILE = "pageAccessFile";
	private static final String PAGES_DIRECTORY = "pagesDirectory";

	private static final Logger logger = LoggerFactory.getLogger(InitializationListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// do nothing
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		logger.info("Initializing page access filter permissions.");

		ServletContext servletContext = contextEvent.getServletContext();
		// load page access rules from page-access.xml file
		String pageAccessFileName = servletContext.getRealPath(servletContext.getInitParameter(PAGE_ACCESS_FILE));
		File pageAccessFile = new File(pageAccessFileName);
		try {
			JAXBContext context = JAXBContext.newInstance(PageAccess.class);
			PageAccess pageAccess = (PageAccess) context.createUnmarshaller().unmarshal(pageAccessFile);
			Map<String, Page> pages = Collections.synchronizedMap(new HashMap<String, Page>());
			if (pageAccess != null) {
				StringBuilder key = new StringBuilder();
				String pagesDirectory = servletContext.getInitParameter(PAGES_DIRECTORY);
				key.append(servletContext.getContextPath());
				key.append(pagesDirectory);
				pagesDirectory = key.toString();
				for (Page page : pageAccess.getPages()) {
					// key is a concatenation of the pageDirectory, and the
					// view-id
					key.setLength(0);
					key.append(pagesDirectory);
					key.append(page.getViewId());
					pages.put(key.toString(), page);
				}
				PagePermission.getInstance().init(pagesDirectory, pages);
			}
		} catch (JAXBException e) {
			logger.error("Unable to unmarshall page-access rules.", e);
		}
		logger.info("Page access rules initialization complete.");
	}

}
