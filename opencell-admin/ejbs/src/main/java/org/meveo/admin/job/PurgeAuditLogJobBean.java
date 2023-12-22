package org.meveo.admin.job;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.audit.logging.AuditDataLogService;
import org.meveo.service.audit.logging.AuditLogService;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Date;

/**
 * Job definition to automatically purge audit log based on a duration.
 *
 * @author Abdellatif BARI
 * @since 16.0.0
 */
public class PurgeAuditLogJobBean extends BaseJobBean {

    @Inject
    private AuditDataLogService auditDataLogService;

    @Inject
    private AuditableFieldService auditableFieldService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    protected ResourceBundle resourceMessages;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        try {
            Long maxAgeDays = (Long) this.getParamOrCFValue(jobInstance, PurgeAuditLogJob.CF_MAX_AGE_DAYS);
            if (maxAgeDays == null) {
                log.warn("Max age days is required");
                result.addReport(resourceMessages.getString("error.purgeAuditLogJob.maxAgeDays.required") + ",");
                return;
            }

            Date purgeDate = DateUtils.addDaysToDate(new Date(), maxAgeDays.intValue() * (-1));
            int nbrPurgedItems = 0;
            int nbrDeleted = auditDataLogService.purgeAuditDataLog(purgeDate);
            if (nbrDeleted > 0) {
                nbrPurgedItems = nbrPurgedItems + nbrDeleted;
            }
            nbrDeleted = auditDataLogService.purgeAuditDataLogRecords(purgeDate);
            if (nbrDeleted > 0) {
                nbrPurgedItems = nbrPurgedItems + nbrDeleted;
            }
            nbrDeleted = auditableFieldService.purgeAuditableField(purgeDate);
            if (nbrDeleted > 0) {
                nbrPurgedItems = nbrPurgedItems + nbrDeleted;
            }
            nbrDeleted = auditLogService.purgeAuditLog(purgeDate);
            if (nbrDeleted > 0) {
                nbrPurgedItems = nbrPurgedItems + nbrDeleted;
            }
            result.setNbItemsToProcess(nbrPurgedItems);
            result.setNbItemsCorrectlyProcessed(nbrPurgedItems);
        } catch (Exception e) {
            log.error("Failed to run purge audit log job", e);
            String detailMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            result.addReport(detailMessage + ",");
        }
    }
}
