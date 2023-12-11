package org.meveo.admin.job.partitioning;

import org.hibernate.JDBCException;
import org.meveo.admin.job.BaseJobBean;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Stateless
public class AutoCreateEDRPartitionBean extends BaseJobBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(AutoCreateEDRPartitionBean.class);

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private final static String WO_QUERY_PATTERN = "select count(*) from create_new_wo_partition('%s', '%s', '%s')";

    private final static String WO_PARTITION_SOURCE = "billing_wallet_operation_other";

    private final static String RT_QUERY_PATTERN = "select count(*) from create_new_rt_partition('%s', '%s', '%s')";

    private final static String RT_PARTITION_SOURCE = "billing_rated_transaction_other";

    private final static String EDR_QUERY_PATTERN = "select count(*) from create_new_edr_partition('%s', '%s', '%s')";

    private final static String EDR_PARTITION_SOURCE = "rating_edr_other";


    
    /**
     * Create a new partition for EDR.
     * The partition is created for the next month.
     *
     * @param result The JobExecutionResultImpl object to register the result of the job execution.
     */
    @Interceptors(JobLoggingInterceptor.class)
    public void createNewEDRPartition(JobExecutionResultImpl result) {

        // Partition for next month
        LocalDate firstDayOfNextMonth = LocalDate.now()
                                                 .plusMonths(1)
                                                 .withDayOfMonth(1);

        String startingRange = firstDayOfNextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // prepare partition name
        String partitionName = EDR_PARTITION_SOURCE + "_" + firstDayOfNextMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        String newPartitionQuery = String.format(EDR_QUERY_PATTERN, EDR_PARTITION_SOURCE, partitionName, startingRange);

        try {
            LOG.info("Create new partition [{}] starting from [{}]", partitionName, startingRange);
            EntityManager entityManager = emWrapper.getEntityManager();
            Query nativeQuery = entityManager.createNativeQuery(newPartitionQuery);
            nativeQuery.getSingleResult();
            result.registerSucces();
        } catch (Exception e) {
            String message = handleException(e);
            result.registerError(message);
            result.setStatus(JobExecutionResultStatusEnum.FAILED);
        }
    }

    private String handleException(Exception e) {
        LOG.error("Error while trying to create EDR new partition", e);
        String message = e.getMessage();
        if (e instanceof PersistenceException) {
            Throwable cause = e.getCause();
            if (cause instanceof JDBCException) {
                message = ((JDBCException) cause).getSQLException().getMessage();
            }
        }
        return message;
    }
}
