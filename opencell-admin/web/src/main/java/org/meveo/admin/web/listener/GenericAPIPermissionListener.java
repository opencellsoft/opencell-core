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

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.job.JobApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class GenericAPIPermissionListener implements ServletContextListener {

    @Inject
    private JobApi jobApi;

    private static final String jobInstanceCode = "APIv2PermissionsSyncJob";
    
    private static final Logger logger = LoggerFactory.getLogger(GenericAPIPermissionListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // do nothing
    }

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        logger.info("Launching APIv2PermissionsSyncJob.");
        
        JobInstanceInfoDto jobInstanceInfoDto = new JobInstanceInfoDto();
        jobInstanceInfoDto.setCode(jobInstanceCode); 
        jobApi.executeJob(jobInstanceInfoDto);
    }

}
