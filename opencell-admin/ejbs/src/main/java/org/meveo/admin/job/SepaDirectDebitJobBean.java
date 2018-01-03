package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.sepa.PaynumFile;
import org.meveo.admin.sepa.SepaFile;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.DDRequestFileFormatEnum;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.DDRequestItemService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class SepaDirectDebitJobBean {

    @Inject
    private Logger log;

    @Inject
    private DDRequestLotOpService dDRequestLotOpService;

    @Inject
    private DDRequestItemService ddRequestItemService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private DDRequestLOTService dDRequestLOTService;

    @Inject
    private PaynumFile paynumFile;

    @Inject
    private SepaFile sepaFile;

    @Inject
    private JobExecutionService jobExecutionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for parameter={}", jobInstance.getParametres());
        try {
            String fileFormat = (String) customFieldInstanceService.getCFValue(jobInstance, "fileFormat");

            List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps(DDRequestFileFormatEnum.valueOf(fileFormat));

            if (ddrequestOps != null) {
                log.info("ddrequestOps found:" + ddrequestOps.size());
            } else {
                log.info("ddrequestOps null");
                return;
            }

            for (DDRequestLotOp ddrequestLotOp : ddrequestOps) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                try {
                    if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
                        DDRequestLOT ddRequestLOT = ddRequestItemService.createDDRquestLot(ddrequestLotOp.getFromDueDate(), ddrequestLotOp.getToDueDate(),
                            DDRequestFileFormatEnum.valueOf(fileFormat));
                        if (ddRequestLOT.getInvoicesNumber() > ddRequestLOT.getRejectedInvoices()) {
                            switch (DDRequestFileFormatEnum.valueOf(fileFormat)) {
                            case PAYNUM:
                                ddRequestLOT.setFileName(paynumFile.getDDFileName(ddRequestLOT));
                                paynumFile.exportDDRequestLot(ddRequestLOT);
                                break;
                            case SEPA:
                                ddRequestLOT.setFileName(sepaFile.getDDFileName(ddRequestLOT));
                                sepaFile.exportDDRequestLot(ddRequestLOT);
                                break;
                            }
                            ddRequestLOT.setSendDate(new Date());
                            dDRequestLOTService.updateNoCheck(ddRequestLOT);
                            ddRequestItemService.createPaymentsForDDRequestLot(ddRequestLOT);
                        }

                    } else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
                        switch (DDRequestFileFormatEnum.valueOf(fileFormat)) {
                        case PAYNUM:
                            paynumFile.exportDDRequestLot(ddrequestLotOp.getDdrequestLOT());

                            break;
                        case SEPA:
                            sepaFile.exportDDRequestLot(ddrequestLotOp.getDdrequestLOT());
                            break;
                        }
                    }

                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);

                } catch (BusinessEntityException e) {
                    log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
                    ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
                    dDRequestLotOpService.updateNoCheck(ddrequestLotOp);

                } catch (Exception e) {
                    log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
                    ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
                    dDRequestLotOpService.updateNoCheck(ddrequestLotOp);
                }
            }
        } catch (Exception e) {
            log.error("Failed to sepa direct debit", e);
        }
    }

}
