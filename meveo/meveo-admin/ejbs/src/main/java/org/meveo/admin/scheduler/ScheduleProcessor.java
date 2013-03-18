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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.report.ReportExecution;
import org.meveo.admin.transformation.JobExecution;
import org.meveo.connector.crm.ImportAccounts;
import org.meveo.connector.crm.ImportCustomers;
import org.meveo.connector.crm.ImportSubscriptions;
import org.meveo.service.billing.impl.RecurringChargeCron;
import org.slf4j.Logger;

/**
 * Quartz Schedule Processor for Jobs and Reports execution
 * 
 * @author Gediminas Ubartas
 * @created 2010.09.30
 */

@Named
@ApplicationScoped
public class ScheduleProcessor {

	@Inject
	private JobExecution jobExecution;

	@Inject
	private ReportExecution reportExecution;

	@Inject
	private ImportCustomers importCustomers;

	@Inject
	private ImportAccounts importAccounts;

	@Inject
	private ImportSubscriptions importSubscriptions;

	@Inject
	private RecurringChargeCron recurringChargeCron;

	@Inject
	protected Logger log;

	/**
	 * Creates quartz timer for Job execution
	 * 
	 * @param when
	 *            Expiration date
	 * @param interval
	 *            interval of execution
	 */

	/*TODO: @Asynchronous
	public synchronized QuartzTriggerHandle executeJobs(@Expiration Date when,
			@IntervalCron String frequency) {
		jobExecution.jobsExecution();
		return null;
	}*/

	/**
	 * Creates quartz timer for Job loading from repository
	 * 
	 * @param when
	 *            Expiration date
	 * @param interval
	 *            interval of execution
	 */
	/*TODO: @Asynchronous
	public synchronized QuartzTriggerHandle importJobs(@Expiration Date when,
			@IntervalCron String frequency) {
		jobExecution.loadJobs();
		return null;
	}*/

	/**
	 * Creates quartz timer for Report execution
	 * 
	 * @param when
	 *            Expiration date
	 * @param interval
	 *            interval of execution
	 */

	/*TODO:@Asynchronous
	public synchronized QuartzTriggerHandle executeReports(@Expiration Date when,
			@IntervalCron String frequency) {
		reportExecution.reportsExecution();
		return null;
	}*/

	/*TODO: @Asynchronous
	public synchronized QuartzTriggerHandle importCustomers(@Expiration Date when,
			@IntervalCron String frequency) throws InterruptedException {
		ParamBean param = ParamBean.getInstance("meveo-admin.properties");
		importCustomers.handleFiles(param.getProperty("connectorCRM.importCustomers.inputDir"),
				param.getProperty("connectorCRM.importCustomers.prefix"),
				param.getProperty("connectorCRM.importCustomers.extention"),
				param.getProperty("connectorCRM.importCustomers.ouputDir.processed"),
				param.getProperty("connectorCRM.importCustomers.ouputDir.error"));
		importAccounts.handleFiles(param.getProperty("connectorCRM.importAccounts.inputDir"),
				param.getProperty("connectorCRM.importAccounts.prefix"),
				param.getProperty("connectorCRM.importAccounts.extention"),
				param.getProperty("connectorCRM.importAccounts.ouputDir.processed"),
				param.getProperty("connectorCRM.importAccounts.ouputDir.error"));
		importSubscriptions.handleFiles(
				param.getProperty("connectorCRM.importSubscriptions.inputDir"),
				param.getProperty("connectorCRM.importSubscriptions.prefix"),
				param.getProperty("connectorCRM.importSubscriptions.extention"),
				param.getProperty("connectorCRM.importSubscriptions.ouputDir.processed"),
				param.getProperty("connectorCRM.importSubscriptions.ouputDir.error"));

		return null;
	}*/

	/*TODO: @Asynchronous
	public synchronized QuartzTriggerHandle recurringChargeApplication(@Expiration Date when,
			@IntervalCron String frequency) {

		recurringChargeCron.recurringChargeApplication();

		return null;
	}*/
}
