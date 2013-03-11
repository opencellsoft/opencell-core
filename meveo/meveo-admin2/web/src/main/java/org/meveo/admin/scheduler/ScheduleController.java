/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.scheduler;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.meveo.commons.utils.ParamBean;

/**
 * Schedule Controller which iniciates Quartz to execute Jobs and Reports
 * 
 * @author Gediminas Ubartas
 * @created 2010.09.30
 */
@Name("scheduleController")
@Scope(ScopeType.APPLICATION)
@AutoCreate()
@Startup
public class ScheduleController {

    @In
    ScheduleProcessor scheduleProcessor;

    @Create
    public void scheduleTimer() throws NumberFormatException, InterruptedException {
        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
        String importCustomersCron = param.getProperty("connectorCRM.importCustomers.cron");
        String importCustomersStartupDelay = param.getProperty("connectorCRM.importCustomers.startupDelay");

        String recurringChargesCron = param.getProperty("RecurringChargesCron.chargeApplication.cron");
        String recurringChargesCronStartupDelay = param
                .getProperty("RecurringChargesCron.chargeApplication.startupDelay");

        String jobExecutionCron = param.getProperty("meveo.jobExecutionCron");
        String jobExecutionStartupDelay = param.getProperty("meveo.jobExecutionCron.startupDelay");

        // String importJobsCron = param.getProperty("meveo.jobLoadingCron");
        // String importJobsStartupDelay =
        // param.getProperty("meveo.jobLoadingCron.startupDelay");

        String reportExecutionCron = param.getProperty("meveo.reportExecutionCron");
        String reportExecutionStartupDelay = param.getProperty("meveo.reportExecutionCron.startupDelay");

        scheduleProcessor.importCustomers((new Date((new Date()).getTime()
                + Long.parseLong(importCustomersStartupDelay))), importCustomersCron);

        scheduleProcessor.recurringChargeApplication((new Date((new Date()).getTime()
                + Long.parseLong(recurringChargesCronStartupDelay))), recurringChargesCron);
        scheduleProcessor.executeJobs((new Date((new Date()).getTime() + Long.parseLong(jobExecutionStartupDelay))),
                jobExecutionCron);
        // scheduleProcessor.importJobs((new Date((new Date()).getTime() +
        // Long.parseLong(importJobsStartupDelay))),
        // importJobsCron);
        scheduleProcessor.executeReports(
                (new Date((new Date()).getTime() + Long.parseLong(reportExecutionStartupDelay))), reportExecutionCron);

    }
}
