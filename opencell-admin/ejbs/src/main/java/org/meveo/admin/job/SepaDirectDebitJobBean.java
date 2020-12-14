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
package org.meveo.admin.job;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.Seller;
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
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
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
 * @lastModifiedVersion 10.0
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

    /** The seller service. */
    @Inject
    private SellerService sellerService;

    /** The app provider. */
    @Inject
    @ApplicationProvider
    private Provider appProvider;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    
    @Inject
    private  ParamBeanFactory paramBeanFactory;
    /**
     * Execute.
     *
     * @param result the result
     * @param jobInstance the job instance
     */
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

		try {
			Long nbRuns = new Long(1);
			Long waitingMillis = new Long(0);

			try {
				nbRuns = (Long) this.getParamOrCFValue(jobInstance, "SepaJob_nbRuns");
				waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "SepaJob_waitingMillis");
				if (nbRuns == -1) {
					nbRuns = (long) Runtime.getRuntime().availableProcessors();
				}

			} catch (Exception e) {
				nbRuns = new Long(1);
				waitingMillis = new Long(0);
				log.warn("Cant get nbRuns and waitingMillis customFields for " + jobInstance.getCode(), e.getMessage());
			}
			DDRequestBuilder ddRequestBuilder = null;
			Seller seller = null;
			String ddRequestBuilderCode = null;
			String sellerCode = null;
			PaymentOrRefundEnum paymentOrRefundEnum = PaymentOrRefundEnum.valueOf(((String) this.getParamOrCFValue(jobInstance, "SepaJob_paymentOrRefund")).toUpperCase());
			if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_ddRequestBuilder") != null) {
				ddRequestBuilderCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_ddRequestBuilder")).getCode();
				ddRequestBuilder = ddRequestBuilderService.findByCode(ddRequestBuilderCode);
			} else {
				throw new BusinessException("Can't find ddRequestBuilder");
			}
			if (ddRequestBuilder == null) {
				throw new BusinessException("Can't find ddRequestBuilder by code:" + ddRequestBuilderCode);
			}
			if ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_seller") != null) {
				sellerCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "SepaJob_seller")).getCode();
				seller = sellerService.findByCode(sellerCode);
			}

			DDRequestBuilderInterface ddRequestBuilderInterface = ddRequestBuilderFactory.getInstance(ddRequestBuilder);
			List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService.getDDRequestOps(ddRequestBuilder, seller, paymentOrRefundEnum);

			if (CollectionUtils.isNotEmpty(ddrequestOps)) {
				log.info("ddrequestOps found:" + ddrequestOps.size());				

			} else {
				final String msg = "ddrequestOps IS EMPTY !";
				log.info(msg);
				result.setNbItemsToProcess(0);
				result.registerWarning(msg);
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
						log.info("start filterAoToPayOrRefund...");
						List<AccountOperation> listAoToPay = this.filterAoToPayOrRefund(ddRequestBuilderInterface, jobInstance, ddrequestLotOp);
						log.info("end filterAoToPayOrRefund listAoToPay.size:" + listAoToPay.size());
						DDRequestLOT ddRequestLOT = dDRequestLOTService.createDDRquestLot(ddrequestLotOp, listAoToPay, ddRequestBuilder, result);
						log.info("end createDDRquestLot");
						if (ddRequestLOT != null && "true".equals(paramBeanFactory.getInstance().getProperty("bayad.ddrequest.split", "true"))) {
							dDRequestLOTService.addItems(ddrequestLotOp, ddRequestLOT, listAoToPay, ddRequestBuilder, result);
							dDRequestLOTService.generateDDRquestLotFile(ddRequestLOT, ddRequestBuilderInterface, appProvider);
							log.info("end generateDDRquestLotFile");
							result.addReport(ddRequestLOT.getRejectedCause());
							dDRequestLOTService.createPaymentsOrRefundsForDDRequestLot(ddRequestLOT, nbRuns, waitingMillis, result);
							log.info("end createPaymentsOrRefundsForDDRequestLot");
							if (isEmpty(ddRequestLOT.getRejectedCause())) {
								result.registerSucces();
							}
						}
					}
					if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.PAYMENT) {
						dDRequestLOTService.createPaymentsOrRefundsForDDRequestLot(ddrequestLotOp.getDdrequestLOT(), nbRuns, waitingMillis, result);
						result.registerSucces();
					}
					if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
						dDRequestLOTService.generateDDRquestLotFile(ddrequestLotOp.getDdrequestLOT(), ddRequestBuilderInterface, appProvider);
						result.registerSucces();
					}
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
					dDRequestLotOpService.update(ddrequestLotOp);
					if (BooleanUtils.isTrue(ddrequestLotOp.getRecurrent())) {
						this.createNewDdrequestLotOp(ddrequestLotOp);
					}
				} catch (Exception e) {
					log.error("Failed to sepa direct debit for id {}", ddrequestLotOp.getId(), e);
					if (BooleanUtils.isTrue(ddrequestLotOp.getRecurrent())) {
						this.createNewDdrequestLotOp(ddrequestLotOp);
					}
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
					ddrequestLotOp.setErrorCause(StringUtils.truncate(e.getMessage(), 255, true));
					dDRequestLotOpService.update(ddrequestLotOp);
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
			scriptInstance = scriptInstanceService.refreshOrRetrieve(scriptInstance);
			if (scriptInstance != null) {

				scriptInstance = scriptInstanceService.retrieveIfNotManaged(scriptInstance);
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
			log.error(" Error on getDueDateRangeScript", e);
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
            newDDRequestLotOp.setSeller(ddrequestLotOp.getSeller());
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
     * @param ddRequestBuilderInterface the ddRequestBuilderInterface
     * @param jobInstance the job instance
     * @param ddRequestLotOp the dd request lot op
     * @return the accountOperation list to process
     */
	private List<AccountOperation> filterAoToPayOrRefund(DDRequestBuilderInterface ddRequestBuilderInterface, JobInstance jobInstance, DDRequestLotOp ddRequestLotOp) {
		AccountOperationFilterScript aoFilterScript = this.getAOScriptInstance(jobInstance);
		if (aoFilterScript != null) {
			Map<String, Object> methodContext = new HashMap<>();				
			 methodContext.put(AccountOperationFilterScript.FROM_DUE_DATE, ddRequestLotOp.getFromDueDate());
			 methodContext.put(AccountOperationFilterScript.TO_DUE_DATE, ddRequestLotOp.getToDueDate());
			 methodContext.put(AccountOperationFilterScript.PAYMENT_METHOD, PaymentMethodEnum.DIRECTDEBIT);
			 methodContext.put(AccountOperationFilterScript.CAT_TO_PROCESS, ddRequestLotOp.getPaymentOrRefundEnum().getOperationCategoryToProcess());

			return aoFilterScript.filterAoToPay(methodContext);
		}
		return ddRequestBuilderInterface.findListAoToPay(ddRequestLotOp);
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