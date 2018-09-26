package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.DDRequestBuilderFactory;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestBuilderService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * The Class SepaDirectDebitJobBean.
 *
 * @author anasseh
 */
@Stateless
public class SepaDirectDebitJobBean extends BaseJobBean {

    /** The log. */
    @Inject
    private Logger log;

    /** The d D request lot op service. */
    @Inject
    private DDRequestLotOpService dDRequestLotOpService;

    /** The d D request LOT service. */
    @Inject
    private DDRequestLOTService dDRequestLOTService;

    /** The job execution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    /** The dd request builder service. */
    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    /** The dd request builder factory. */
    @Inject
    private DDRequestBuilderFactory ddRequestBuilderFactory;

    /** The app provider. */
    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /**
     * Execute.
     *
     * @param result the result
     * @param jobInstance the job instance
     */
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for parameter={}", jobInstance.getParametres());
        try {

            DDRequestBuilder ddRequestBuilder = null;
            String ddRequestBuilderCode = null;
            if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_ddRequestBuilder") != null) {
                ddRequestBuilderCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_ddRequestBuilder")).getCode();
                ddRequestBuilder = ddRequestBuilderService.findByCode(ddRequestBuilderCode);
            } else {
                throw new BusinessException("Can't find ddRequestBuilder");
            }
            if (ddRequestBuilder == null) {
                throw new BusinessException("Can't find ddRequestBuilder by code:" + ddRequestBuilderCode);
            }

            DDRequestBuilderInterface ddRequestBuilderInterface = ddRequestBuilderFactory.getInstance(ddRequestBuilder);
            List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps(ddRequestBuilder);

            if (ddrequestOps != null) {
                log.info("ddrequestOps found:" + ddrequestOps.size());
                result.setNbItemsToProcess(ddrequestOps.size());

            } else {
                log.info("ddrequestOps null");
                result.setNbItemsToProcess(0);
                return;
            }

            for (DDRequestLotOp ddrequestLotOp : ddrequestOps) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                try {
                    if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
                        DDRequestLOT ddRequestLOT = dDRequestLOTService.createDDRquestLot(ddrequestLotOp.getFromDueDate(), ddrequestLotOp.getToDueDate(), ddRequestBuilder,
                            ddrequestLotOp.getFilter());
                        dDRequestLOTService.createPaymentsForDDRequestLot(ddRequestLOT);
                    } else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
                        ddRequestBuilderInterface.generateDDRequestLotFile(ddrequestLotOp.getDdrequestLOT(), appProvider);
                    }
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
                    result.registerSucces();

                } catch (Exception e) {
                    log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
                    ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
                    result.registerError(ddrequestLotOp.getId(), e.getMessage());
                    result.addReport("ddrequestLotOp id : " + ddrequestLotOp.getId() + " RejectReason : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to sepa direct debit", e);
        }
    }

}
