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
package org.meveo.admin.transformation;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.bi.JobExecutionHisto;
import org.meveo.service.bi.impl.JobExecutionHistoryService;
import org.meveo.service.bi.impl.JobService;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.repository.kdr.delegates.KettleDatabaseRepositoryJobDelegate;
import org.slf4j.Logger;

/**
 * Class to execute Job
 * 
 * @author Gediminas Ubartas
 * @created 2010.09.22
 */
@Named
public class JobExecution  implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = 2060927874567045135L;

    @Inject
	private JobService jobService;

	@Inject
	protected Logger log;
	

    @Inject
    private JobExecutionHistoryService jobExecutionHistoryService;

	@Inject
    private ParamBean paramBean;
	
	private  String REPORTS_FILEPATH ;
	private  String DATABASE_NAME ;
	private  String DATABASE_TYPE ;
	private  String DATABASE_ACCES ;
	private  String DATABASE_HOST ;
	private  String DATABASE ;
	private  String DATABASE_PORT ;
	private  String DATABASE_USER ;
	private  String DATABASE_PASSWORD ;
	private  String DATABASE_SCHEMA ;
	private  String OPERATIONAL_DATABASE_NAME ;
	private  String OPERATIONAL_DATABASE_HOST ;
	private  String OPERATIONAL_DATABASE_PORT ;
	private  String OPERATIONAL_DATABASE_USER ;
	private  String OPERATIONAL_DATABASE_PASSWORD;
	private  String OPERATIONAL_DATABASE_SCHEMA ;

	// Constants for repository records should be same in JobService class
	private static final String SCHEDULER_TYPE_STRING = "schedulerType";
	private static final String INTERVAL_SECONDS_STRING = "intervalSeconds";
	private static final String INTERVAL_MINUTES_STRING = "intervalMinutes";
	private static final String HOUR_STRING = "hour";
	private static final String MINUTES_STRING = "minutes";
	private static final String WEEK_DAY_STRING = "weekDay";
	private static final String DAY_OF_MONTH_STRING = "dayOfMonth";

	public KettleDatabaseRepository repo;
	public KettleDatabaseRepositoryJobDelegate repoDelegate;

	
	@PostConstruct
	public void init(){
        REPORTS_FILEPATH = paramBean.getProperty("reportsURL");
        DATABASE_NAME = paramBean.getProperty("kettleRepo.databaseName");
        DATABASE_TYPE = paramBean.getProperty("kettleRepo.databaseType");
        DATABASE_ACCES = paramBean.getProperty("kettleRepo.databaseAcces");
        DATABASE_HOST = paramBean.getProperty("kettleRepo.databaseHost");
        DATABASE = paramBean.getProperty("kettleRepo.database");
        DATABASE_PORT = paramBean.getProperty("kettleRepo.databasePort");
        DATABASE_USER = paramBean.getProperty("kettleRepo.databaseUser");
        DATABASE_PASSWORD = paramBean.getProperty("kettleRepo.databasePassword");
        DATABASE_SCHEMA = paramBean.getProperty("kettleRepo.databaseSchema");

        OPERATIONAL_DATABASE_NAME = paramBean.getProperty("kettleRepo.operationalDBName");
        OPERATIONAL_DATABASE_HOST = paramBean.getProperty("kettleRepo.operationalDBHost");
        OPERATIONAL_DATABASE_PORT = paramBean.getProperty("kettleRepo.operationalDBPort");
        OPERATIONAL_DATABASE_USER = paramBean.getProperty("kettleRepo.operationalDBUser");
        OPERATIONAL_DATABASE_PASSWORD = paramBean.getProperty("kettleRepo.operationalDBPassword");
        OPERATIONAL_DATABASE_SCHEMA = paramBean.getProperty("kettleRepo.operationalDBSchema");

	}
	
	
	/**
	 * Initiates repository connection
	 * 
	 */
	public void initKettleRepository() throws KettleException {
		KettleEnvironment.init();
		repo = new KettleDatabaseRepository();
		PluginRegistry.getInstance();

		JobEntryPluginType jobEntrPlugType = JobEntryPluginType.getInstance();
		jobEntrPlugType.searchPlugins();

		DatabasePluginType dbPlugType = DatabasePluginType.getInstance();
		dbPlugType.searchPlugins();

		DatabaseMeta databaseMeta = new DatabaseMeta(DATABASE_NAME, DATABASE_TYPE, DATABASE_ACCES,
				DATABASE_HOST, DATABASE, DATABASE_PORT, DATABASE_USER, DATABASE_PASSWORD);

		KettleDatabaseRepositoryMeta repoMeta = new KettleDatabaseRepositoryMeta("1", "repository",
				"desription", databaseMeta);
		repo.init(repoMeta);
		repo.connect("admin", "admin");
		repoDelegate = new KettleDatabaseRepositoryJobDelegate(repo);
	}

	/**
	 * Executes Job from repository
	 * 
	 * @param jobName
	 *            name of Job
	 * @param jobRepositoryId
	 *            Job id in repository
	 * @param executionDate
	 *            Date of job execution
	 * @param jobId
	 *            Job entity id
	 */
	public void executeJob(String jobName, int jobRepositoryId, long jobId, Date executionDate,
			Date lastExecutionDate) {
		try {
			log.info("executeJob(jobName=" + jobName + ",jobRepositoryId=" + jobRepositoryId
					+ ", jobId=" + jobId + ", executionDate=" + executionDate
					+ ", lastExecutionDate=" + lastExecutionDate + ")");
			initKettleRepository();
			RepositoryDirectoryInterface repoDirectoryInterface = repo
					.loadRepositoryDirectoryTree();
			JobMeta jobMeta = repoDelegate.loadJobMeta(jobName, repoDirectoryInterface);
			Job job = new Job(repo, jobMeta);
			job = setVariables(job, executionDate, lastExecutionDate);
			JobEntryCopy startpoint;
			startpoint = jobMeta.findJobEntry(JobMeta.STRING_SPECIAL_START, 0, false);
			JobEntrySpecial jes = (JobEntrySpecial) startpoint.getEntry();
			jes.setSchedulerType(0);
			Result result = new Result();
			job.execute(0, result);
			history(getNextExecutionDate(jobRepositoryId, executionDate), executionDate, jobId);
		} catch (KettleException e) {
			log.error("KettleException", e);
		}
	}

	/**
	 * Saving job Execution History
	 * 
	 * @param nextExecutionDate
	 *            Date of next execution
	 * @param jobId
	 *            Job entity id
	 */
	public void history(Date nextExecutionDate, Date executionDate, long jobId) {
		log.info("Job executed, calculating next execution date:", nextExecutionDate);
		org.meveo.model.bi.Job job = jobService.findById(jobId);
		JobExecutionHisto jobExecutionHistory = new JobExecutionHisto();
		jobExecutionHistory.setJob(job);
		jobExecutionHistory.setExecutionDate(executionDate);
		jobExecutionHistoryService.create(jobExecutionHistory);
		job.setLastExecutionDate(executionDate);
		job.setNextExecutionDate(nextExecutionDate);
		jobService.update(job);

	}

	/**
	 * Sets variables for Job
	 * 
	 * @param Job
	 *            current job
	 */
	public Job setVariables(Job job, Date executionDate, Date lastExecutionDate) {
		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		log.info("Job parameters",
				sdf.format(executionDate).toString() + " "
						+ sdf.format(lastExecutionDate).toString());
		job.setVariable("executionDate", sdf.format(executionDate).toString());
		job.setVariable("lastExecutionDate", sdf.format(lastExecutionDate).toString());
		job.setVariable("db.host", OPERATIONAL_DATABASE_HOST);
		job.setVariable("db.dbname", OPERATIONAL_DATABASE_NAME);
		job.setVariable("db.port", OPERATIONAL_DATABASE_PORT);
		job.setVariable("db.username", OPERATIONAL_DATABASE_USER);
		job.setVariable("db.userpass", OPERATIONAL_DATABASE_PASSWORD);
		job.setVariable("db.schema", OPERATIONAL_DATABASE_SCHEMA);
		job.setVariable("dbdwh.host", DATABASE_HOST);
		job.setVariable("dbdwh.dbname", DATABASE_NAME);
		job.setVariable("dbdwh.port", DATABASE_PORT);
		job.setVariable("dbdwh.username", DATABASE_USER);
		job.setVariable("dbdwh.userpass", DATABASE_PASSWORD);
		job.setVariable("dbdwh.schema", DATABASE_SCHEMA);
		job.setVariable("report.file.folder", REPORTS_FILEPATH);
		return job;
	}

	/**
	 * Calculate next Execution Date for job
	 * 
	 * @param jobRepositoryId
	 *            Job id in repository
	 * @return next Execution Date
	 */
	public Date getNextExecutionDate(int jobRepositoryId, Date date) {

		Date nextExecutionDate = date;
		Map<String, Integer> values = jobService.getJobSchedulerInfo(jobRepositoryId);
		int schedulerType = values.get(SCHEDULER_TYPE_STRING);
		int hour = values.get(HOUR_STRING);
		int minutes = values.get(MINUTES_STRING);
		int interval_miutes = values.get(INTERVAL_MINUTES_STRING);
		int interval_seconds = values.get(INTERVAL_SECONDS_STRING);
		int dayOfWeek = values.get(WEEK_DAY_STRING);
		int dayOfMonth = values.get(DAY_OF_MONTH_STRING);
		Calendar calendar = Calendar.getInstance();
		switch (schedulerType) {
		case 1:
			nextExecutionDate.setTime(date.getTime() + interval_seconds + interval_miutes * 60);
			break;// Interval
		case 2:
			calendar.setTime(nextExecutionDate);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minutes);
			nextExecutionDate = calendar.getTime();
			break;// Daily
		case 3:
			calendar.setTime(nextExecutionDate);
			calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minutes);
			calendar.set(Calendar.SECOND, 0);
			nextExecutionDate = calendar.getTime();
			break;// weekly
		case 4:
			calendar.setTime(nextExecutionDate);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minutes);
			nextExecutionDate = calendar.getTime();
			break;// monthly
		default:
			log.info("No scheduling");// no Scheduling
			break;
		}
		return nextExecutionDate;

	}

	/**
	 * Calculate next Execution Date for job
	 * 
	 * @param jobRepositoryId
	 *            Job id in repository
	 * @return next Execution Date
	 */
	@SuppressWarnings("unchecked")
	public void jobsExecution() {
		List<org.meveo.model.bi.Job> jobList = (List<org.meveo.model.bi.Job>) jobService.list();
		Date date = new Date();
		if (jobList.size() != 0) {
			for (org.meveo.model.bi.Job job : jobList) {
				log.info("Executing jobs");
				if ((job.getNextExecutionDate().before(date) || job.getNextExecutionDate().equals(
						date))
						&& (job.isActive())) {
					log.info("Executing job", new Date());
					executeJob(job.getName(), job.getJobRepositoryId(), job.getId(),
							job.getNextExecutionDate(), job.getLastExecutionDate());
				}
			}
		}
	}

	/**
	 * Load and create entity for Job for Job from repository
	 */
	public void loadJobs() {
		List<String> jobList = jobService.getJobNames();
		try {
			initKettleRepository();
			String[] jobNames = repo.getJobNames();
			if (jobNames != null) {
				for (String jobName : jobNames) {
					if (!jobList.contains(jobName)) {
						if (!jobName.startsWith("TRA")) { // If job is not used
															// as transformation
							org.meveo.model.bi.Job job = jobService.getJobInfo(jobName);
							jobService.create(job);
						}
					}
				}
			}
		} catch (KettleException e) {
			log.error("KettleException", e);
		}

	}

}
