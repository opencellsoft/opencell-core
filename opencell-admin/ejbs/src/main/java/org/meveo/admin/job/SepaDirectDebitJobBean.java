package org.meveo.admin.job;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.meveo.service.script.payment.AccountOperationFilterScript.LIST_AO_TO_PAY;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.DDRequestBuilderFactory;
import org.meveo.service.payments.impl.DDRequestBuilderInterface;
import org.meveo.service.payments.impl.DDRequestBuilderService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.meveo.service.script.payment.AccountOperationFilterScript;
import org.meveo.service.script.payment.DateRangeScript;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;;

/**
 * The Class SepaDirectDebitJobBean.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
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

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

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
            PaymentOrRefundEnum paymentOrRefundEnum = PaymentOrRefundEnum.PAYMENT;
            paymentOrRefundEnum = PaymentOrRefundEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "SepaJob_paymentOrRefund")).toUpperCase());
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
            List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps(ddRequestBuilder,paymentOrRefundEnum);

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
                    DateRangeScript dateRangeScript = this.getDueDateRangeScript(ddrequestLotOp);
                    if (dateRangeScript != null) { // computing custom due date range :
                        this.updateOperationDateRange(ddrequestLotOp, dateRangeScript);
                    }
                    if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
                        List<AccountOperation> listAoToPay = this.filterAoToPayOrRefund(ddRequestBuilderInterface.findListAoToPay(ddrequestLotOp), jobInstance, ddrequestLotOp);
                        DDRequestLOT ddRequestLOT = dDRequestLOTService.createDDRquestLot(ddrequestLotOp, listAoToPay, ddRequestBuilder, result);
                        if (ddRequestLOT != null) {
                            result.addReport(ddRequestLOT.getRejectedCause());

                            dDRequestLOTService.createPaymentsOrRefundsForDDRequestLot(ddRequestLOT);

                            if (isEmpty(ddRequestLOT.getRejectedCause())) {
                                result.registerSucces();
                            }
                        }
                    } else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
                        ddRequestBuilderInterface.generateDDRequestLotFile(ddrequestLotOp.getDdrequestLOT(), appProvider);
                        result.registerSucces();
                    }
                    ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
                    if (BooleanUtils.isTrue(ddrequestLotOp.getRecurrent())) {
                        this.createNewDdrequestLotOp(ddrequestLotOp);
                    }
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

    /**
     * Update operation date range.
     *
     * @param ddrequestLotOp the ddrequest lot op
     * @param dateRangeScript the date range script
     */
    private void updateOperationDateRange(DDRequestLotOp ddrequestLotOp, DateRangeScript dateRangeScript) {
        try {
            DateRange dueDateRange = dateRangeScript.computeDateRange(new HashMap<>()); // no addtional params are needed right now for computeDateRange, may be in the future.
            // Due date from :
            Date fromDueDate = dueDateRange.getFrom();
            if (fromDueDate == null) {
                fromDueDate = new Date(1);
            }
            ddrequestLotOp.setFromDueDate(fromDueDate);

            // Due date to :
            Date toDueDate = dueDateRange.getTo();
            if (toDueDate == null) {
                toDueDate = DateUtils.addYearsToDate(fromDueDate, 1000);
            }
            ddrequestLotOp.setToDueDate(toDueDate);
        } catch (Exception e) {
            log.error("Error on updateOperationDateRange {} ", e.getMessage(), e);
        }
    }

    /**
     * Gets the due date range script.
     *
     * @param ddrequestLotOp the ddrequest lot op
     * @return the due date range script
     */
    private DateRangeScript getDueDateRangeScript(DDRequestLotOp ddrequestLotOp) {
        try {
            ScriptInstance scriptInstance = ddrequestLotOp.getScriptInstance();
            if (scriptInstance != null) {
                final String scriptCode = scriptInstance.getCode();
                if (scriptCode != null) {
                    log.debug(" looking for ScriptInstance with code :  [{}] ", scriptCode);
                    ScriptInterface si = scriptInstanceService.getScriptInstance(scriptCode);
                    if (si != null && si instanceof DateRangeScript) {
                        return (DateRangeScript) si;
                    }
                }
            }
        } catch (Exception e) {
            log.error(" Error on getDueDateRangeScript : [{}]", e.getMessage());
        }
        return null;
    }

    /**
     * Creates a new DDRequestLotOp instance, using the initial one's informations. <br>
     * Hence a recurrent job could treat the expected invoices permanently.
     *
     * @param ddrequestLotOp the ddrequest lot op
     */
    private void createNewDdrequestLotOp(DDRequestLotOp ddrequestLotOp) {
        try {
            DDRequestLotOp newDDRequestLotOp = new DDRequestLotOp();
            newDDRequestLotOp.setPaymentOrRefundEnum(ddrequestLotOp.getPaymentOrRefundEnum());
            newDDRequestLotOp.setRecurrent(true);
            newDDRequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
           
            ScriptInstance dueDateRange = ddrequestLotOp.getScriptInstance();
            newDDRequestLotOp.setScriptInstance(dueDateRange);
            if (dueDateRange == null) {
                newDDRequestLotOp.setFromDueDate(ddrequestLotOp.getFromDueDate());
                newDDRequestLotOp.setToDueDate(ddrequestLotOp.getToDueDate());
            }
            newDDRequestLotOp.setDdRequestBuilder(ddrequestLotOp.getDdRequestBuilder());
            newDDRequestLotOp.setFilter(ddrequestLotOp.getFilter());
            newDDRequestLotOp.setDdrequestOp(ddrequestLotOp.getDdrequestOp());

            this.dDRequestLotOpService.create(newDDRequestLotOp);
        } catch (Exception e) {
            log.error(" error on createNewDdrequestLotOp {} ", e.getMessage(), e);
        }
    }

    /**
     * Filter ao to pay or refund, based on a given script, which is set through a job CF.
     *
     * @param listAoToPay the list ao to pay
     * @param jobInstance the job instance
     * @param ddRequestLotOp the dd request lot op
     * @return the accountOperation list to process
     */
    private List<AccountOperation> filterAoToPayOrRefund(List<AccountOperation> listAoToPay, JobInstance jobInstance, DDRequestLotOp ddRequestLotOp) {
        AccountOperationFilterScript aoFilterScript = this.getAOScriptInstance(jobInstance);
        if (aoFilterScript != null) {
            Map<String, Object> methodContext = new HashMap<>();
            methodContext.put(LIST_AO_TO_PAY, listAoToPay);
            listAoToPay = aoFilterScript.filterAoToPay(methodContext);
        }
        if (CollectionUtils.isNotEmpty(listAoToPay)) {
            listAoToPay =  listAoToPay.stream()
                .filter((ao) -> (ao.getPaymentMethod() == PaymentMethodEnum.DIRECTDEBIT && ao.getTransactionCategory() == ddRequestLotOp.getPaymentOrRefundEnum().getOperationCategoryToProcess()
                        && (ao.getMatchingStatus() == MatchingStatusEnum.O || ao.getMatchingStatus() == MatchingStatusEnum.P)
                        ))
                .collect(Collectors.toList());
        }       
        return listAoToPay;
    }

    /**
     * Gets the AO script instance.
     *
     * @param jobInstance the job instance
     * @return the AO script instance
     */
    private AccountOperationFilterScript getAOScriptInstance(JobInstance jobInstance) {
        try {
            String aoFilterScriptCode = null;
            EntityReferenceWrapper entityReferenceWrapper = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_aoFilterScript"));
            if (entityReferenceWrapper != null) {
                aoFilterScriptCode = entityReferenceWrapper.getCode();
            }

            if (aoFilterScriptCode != null) {
                log.debug(" looking for ScriptInstance with code :  [{}] ", aoFilterScriptCode);
                ScriptInterface si = scriptInstanceService.getScriptInstance(aoFilterScriptCode);
                if (si != null && si instanceof AccountOperationFilterScript) {
                    return (AccountOperationFilterScript) si;
                }
            }
        } catch (Exception e) {
            log.error(" Error on newAoFilterScriptInstance : [{}]", e.getMessage());
        }
        return null;
    }

}
