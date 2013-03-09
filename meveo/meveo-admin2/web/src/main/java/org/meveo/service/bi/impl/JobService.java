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
package org.meveo.service.bi.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.meveo.commons.utils.DateUtils;
import org.meveo.model.bi.Job;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.bi.local.JobServiceLocal;

/**
 * Job service implementation.
 * 
 * @author Gediminas Ubartas
 * @created 2010.09.23
 */
@Stateless
@Name("jobService")
@AutoCreate
public class JobService extends PersistenceService<Job> implements JobServiceLocal {
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
        Query query = em.createQuery("select name from " + Job.class.getName());
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getJobSchedulerInfo(int jobRepositoryId) {
        Query query = dwhEntityManager.createNativeQuery(SELECT_JOB_INFO);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @SuppressWarnings("unchecked")
    public Job getJobInfo(String jobName) {
        Query query = dwhEntityManager.createNativeQuery(SELECT_JOB);
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
            job.setLastExecutionDate(DateUtils.addDaysToDate(job.getNextExecutionDate(),-1));
        }
        return job;
    }

    public void createJob(String name, Date nextExecutionDate, boolean active, int jobFrequency) {
        Job job = new Job();
        job.setName(name);
        job.setNextExecutionDate(nextExecutionDate);
        job.setFrequencyId(jobFrequency);
        job.setActive(active);
        em.persist(job);

    }

}
