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
package org.meveo.service.bi.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.Query;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.bi.Job;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;

/**
 * Job service implementation.
 */
@Stateless
public class JobService extends PersistenceService<Job> {
	private static final String SELECT_JOB = "SELECT R_JOB.NAME, R_JOBENTRY_ATTRIBUTE.VALUE_NUM, MODIFIED_DATE, R_JOB.JOB_STATUS, R_JOB.ID_JOB FROM R_JOB INNER JOIN R_JOBENTRY_ATTRIBUTE ON R_JOB.ID_JOB=R_JOBENTRY_ATTRIBUTE.ID_JOB where R_JOBENTRY_ATTRIBUTE.CODE = 'schedulerType' and R_JOB.NAME= :name";
	private static final String SELECT_JOB_INFO = "SELECT CODE, VALUE_NUM FROM R_JOBENTRY_ATTRIBUTE where ID_JOB=:id";

	// Constants for repository records should be same in JobExecution class
	private static final String SCHEDULER_TYPE_STRING = "schedulerType";
	private static final String INTERVAL_SECONDS_STRING = "intervalSeconds";
	private static final String INTERVAL_MINUTES_STRING = "intervalMinutes";
	private static final String HOUR_STRING = "hour";
	private static final String MINUTES_STRING = "minutes";
	private static final String WEEK_DAY_STRING = "weekDay";
	private static final String DAY_OF_MONTH_STRING = "dayOfMonth";

	@SuppressWarnings("unchecked")
	public List<String> getJobNames() {
		Query query = getEntityManager().createQuery(
				"select name from " + Job.class.getName());
		return query.getResultList();
	}

    @JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getJobSchedulerInfo(int jobRepositoryId) {
		Query query = getEntityManager().createNativeQuery(SELECT_JOB_INFO);
		query.setParameter("id", jobRepositoryId);
		List<Object> list = query.getResultList();
		Map<String, Integer> valuesMap = new HashMap<String, Integer>();
		BigDecimal temp = null;
		for (Object oRow : list) {
			Object[] row = (Object[]) oRow;
			temp = (BigDecimal) row[1];
			if (row[0].equals(SCHEDULER_TYPE_STRING)) {
				valuesMap.put(SCHEDULER_TYPE_STRING, temp.intValue());
			}
			if (row[0].equals(INTERVAL_SECONDS_STRING)) {
				valuesMap.put(INTERVAL_SECONDS_STRING, temp.intValue());
			}
			if (row[0].equals(INTERVAL_MINUTES_STRING)) {
				valuesMap.put(INTERVAL_MINUTES_STRING, temp.intValue());
			}
			if (row[0].equals(HOUR_STRING)) {
				valuesMap.put(HOUR_STRING, temp.intValue());
			}
			if (row[0].equals(MINUTES_STRING)) {
				valuesMap.put(MINUTES_STRING, temp.intValue());
			}
			if (row[0].equals(WEEK_DAY_STRING)) {
				valuesMap.put(WEEK_DAY_STRING, temp.intValue());
			}
			if (row[0].equals(DAY_OF_MONTH_STRING)) {
				valuesMap.put(DAY_OF_MONTH_STRING, temp.intValue());
			}

		}
		return valuesMap;
	}

    @JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@SuppressWarnings({ "rawtypes" })
	public Job getJobInfo(String jobName) {
		Query query = getEntityManager().createNativeQuery(SELECT_JOB);
		query.setParameter("name", jobName);
		List results = query.getResultList();
		Job job = new Job();
		for (int i = 0; i < results.size(); i++) {
			Object[] o = (Object[]) (results.get(i));
			job.setName((String) o[0]);
			BigDecimal temp = (BigDecimal) o[1];
			job.setFrequencyId(temp.intValue());
			job.setNextExecutionDate((Date) o[2]);
			temp = (BigDecimal) o[3];
			if (temp.intValue() == 0) {
				job.setActive(true);
			} else {
				job.setActive(false);
			}
			temp = (BigDecimal) o[4];
			job.setJobRepositoryId(temp.intValue());
			job.setLastExecutionDate(DateUtils.addDaysToDate(
					job.getNextExecutionDate(), -1));
		}
		return job;
	}

	public void createJob(String name, Date nextExecutionDate, boolean active,
			int jobFrequency) {
		Job job = new Job();
		job.setName(name);
		job.setNextExecutionDate(nextExecutionDate);
		job.setFrequencyId(jobFrequency);
		job.setActive(active);
		getEntityManager().persist(job);

	}

}
