package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessEntityException;
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
import org.meveo.service.payments.impl.DDRequestItemService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class SepaDirectDebitJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private DDRequestLotOpService dDRequestLotOpService;

    @Inject
    private DDRequestItemService ddRequestItemService;

    @Inject
    private DDRequestLOTService dDRequestLOTService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    @Inject
    private DDRequestBuilderFactory ddRequestBuilderFactory;
    
    @Inject
    @ApplicationProvider
    private Provider appProvider;

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
                        DDRequestLOT ddRequestLOT = ddRequestItemService.createDDRquestLot(ddrequestLotOp.getFromDueDate(), ddrequestLotOp.getToDueDate(), ddRequestBuilder);
                        if (ddRequestLOT.getInvoicesNumber() > ddRequestLOT.getRejectedInvoices()) { 
                            ddRequestLOT.setFileName(ddRequestBuilderInterface.getDDFileName(ddRequestLOT,appProvider));
                            ddRequestBuilderInterface.generateDDRequestLotFile(ddRequestLOT,appProvider);
                            ddRequestLOT.setSendDate(new Date());
                            dDRequestLOTService.updateNoCheck(ddRequestLOT);
                            ddRequestItemService.createPaymentsForDDRequestLot(ddRequestLOT);
                        }

                    } else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
                        ddRequestBuilderInterface.generateDDRequestLotFile(ddrequestLotOp.getDdrequestLOT(),appProvider);
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
