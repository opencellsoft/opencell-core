/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.web.listener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.meveo.admin.web.filter.config.PageAccess;
import org.meveo.util.view.PagePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class InitializationListener implements ServletContextListener {

    private static final String PAGE_ACCESS_FILE = "pageAccessFile";

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
            if (pageAccess != null) {
                String pagesDirectory = servletContext.getContextPath() + pageAccess.getPath();
                PagePermission.getInstance().init(pagesDirectory, pageAccess.getPages());
            }
        } catch (JAXBException e) {
            logger.error("Unable to unmarshall page-access rules.", e);
        }
        logger.info("Page access rules initialization complete.");
    }

}
