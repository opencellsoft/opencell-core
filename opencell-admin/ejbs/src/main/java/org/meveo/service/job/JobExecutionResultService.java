/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;

/**
 * The Class JobExecution result service.
 * 
 * @author Andrius Karpavicius
 * @lastModifiedVersion 11.0
 * 
 */
@Stateless
public class JobExecutionResultService extends PersistenceService<JobExecutionResultImpl> {

    @Inject
    private JobInstanceService jobInstanceService;

    /**
     * Persist job execution results.
     * 
     * @param result Job execution result
     * @return True if job is completely done. False if any data are left to process.
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void persistResult(JobExecutionResultImpl result) {

        if (result.getId() == null) {
            create(result);

        } else {

            // Not interested in tracking job execution history when no work was done
            if (result.getStatus() == JobExecutionResultStatusEnum.COMPLETED && (result.getNbItemsCorrectlyProcessed() + result.getNbItemsProcessedWithError() + result.getNbItemsProcessedWithWarning()) == 0) {
                result.addReport("No items were processed");
                log.info("{}/{}: No items were processed", result.getJobInstance().getJobTemplate(), result.getJobInstance().getCode());

                if ("false".equals(paramBeanFactory.getInstance().getProperty("meveo.job.persistResult", "true"))) {
                    remove(result);
                    return;
                }
            }
            if (result.getStatus() != JobExecutionResultStatusEnum.RUNNING) {
                log.info("Job execution finished {}", result);
            }
            getEntityManager().createNamedQuery("JobExecutionResult.updateProgress").setParameter("id", result.getId()).setParameter("endDate", result.getEndDate())
                .setParameter("nbItemsToProcess", result.getNbItemsToProcess()).setParameter("nbItemsCorrectlyProcessed", result.getNbItemsCorrectlyProcessed())
                .setParameter("nbItemsProcessedWithError", result.getNbItemsProcessedWithError()).setParameter("nbItemsProcessedWithWarning", result.getNbItemsProcessedWithWarning())
                .setParameter("report", result.getReport()).setParameter("status", result.getStatus()).executeUpdate();
        }
    }

    /**
     * Gets the find query.
     *
     * @param jobName job name
     * @param configuration configuration
     * @return querry builder
     */
    private QueryBuilder getFindQuery(String jobName, PaginationConfiguration configuration) {
        QueryBuilder qb = new QueryBuilder("select distinct t from JobExecutionResultImpl t");

        if (!StringUtils.isEmpty(jobName)) {
            qb.addCriterion("t.jobInstance.code", "=", jobName, false);
        }
        qb.addPaginationConfiguration(configuration);

        return qb;
    }

    /**
     * Count job execution history records which end date is older then a given date and belong to a given job (optional)
     * 
     * @param jobName job name (optional)
     * @param date Date to check
     * @return A number of job execution history records which is older then a given date
     */
    public long countJobExecutionHistoryToDelete(String jobName, Date date) {
        long result = 0;

        if (jobName == null) {
            result = getEntityManager().createNamedQuery("JobExecutionResult.countHistoryToPurgeByDate", Long.class).setParameter("date", date).getSingleResult();
        } else {
            JobInstance jobInstance = jobInstanceService.findByCode(jobName);
            if (jobInstance == null) {
                log.error("No Job instance by code {} was found. No Job execution history will be removed.", jobName);
                return 0;
            }
            result = getEntityManager().createNamedQuery("JobExecutionResult.countHistoryToPurgeByDateAndJobInstance", Long.class).setParameter("date", date).setParameter("jobInstance", jobInstance).getSingleResult();
        }

        return result;
    }

    /**
     * Remove job execution history older than a given date and belong to a given job (optional)
     * 
     * @param jobName Job name to match (optional)
     * @param date Date to check
     * @return A number of records that were removed
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long deleteJobExecutionHistory(String jobName, Date date) {
        log.debug("Removing Job execution history of job {} which date is older then a {} date", jobName == null ? "ALL" : jobName, date);

        long itemsDeleted = 0;

        if (jobName == null) {
            itemsDeleted = getEntityManager().createNamedQuery("JobExecutionResult.purgeHistoryByDate").setParameter("date", date).executeUpdate();

        } else {
            JobInstance jobInstance = jobInstanceService.findByCode(jobName);
            if (jobInstance == null) {
                log.error("No Job instance by code {} was found. No Job execution history will be removed.", jobName);
                return 0;
            }
            itemsDeleted = getEntityManager().createNamedQuery("JobExecutionResult.purgeHistoryByDateAndJobInstance").setParameter("date", date).setParameter("jobInstance", jobInstance).executeUpdate();
        }

        log.info("Removed {} Job execution history of job {} which date is older then a {} date", itemsDeleted, jobName == null ? "ALL" : jobName, date);

        return itemsDeleted;
    }

    /**
     * Find JobExecutionResultImpl.
     *
     * @param jobName job's name
     * @param configuration pagination configuration
     * @return list of job's result.
     */
    @SuppressWarnings("unchecked")
    public List<JobExecutionResultImpl> find(String jobName, PaginationConfiguration configuration) {
        return getFindQuery(jobName, configuration).find(getEntityManager());
    }

    /**
     * Count.
     *
     * @param jobName job name
     * @param configuration configuration
     * @return number of job
     */
    public long count(String jobName, PaginationConfiguration configuration) {
        return getFindQuery(jobName, configuration).count(getEntityManager());
    }

    /**
     * Gets the job instance service.
     *
     * @return job instance service
     */
    public JobInstanceService getJobInstanceService() {
        return jobInstanceService;
    }

    /**
     * Find by code like.
     *
     * @param code the code
     * @return list of job's result
     * @see org.meveo.service.base.PersistenceService#findByCodeLike(java.lang.String)
     */
    @Override
    public List<JobExecutionResultImpl> findByCodeLike(String code) {
        throw new UnsupportedOperationException();
    }

    /**
     * Finds the last job execution result by a given job instance.
     * 
     * @param jobInstance JobInstance filter
     * @return last job execution result
     */
    public JobExecutionResultImpl findLastExecutionByInstance(JobInstance jobInstance) {
        QueryBuilder qb = new QueryBuilder(JobExecutionResultImpl.class, "j");
        qb.addCriterionEntity("jobInstance", jobInstance);
        qb.addOrderCriterionAsIs("id", false);

        return (JobExecutionResultImpl) qb.getQuery(getEntityManager()).setMaxResults(1).getResultList().get(0);
    }
}