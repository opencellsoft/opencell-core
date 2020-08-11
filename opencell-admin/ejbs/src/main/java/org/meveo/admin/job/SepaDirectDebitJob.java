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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.service.job.Job;


/**
 * The Class SepaDirectDebitJob generate sepa/paynum or custom files for available DirectDebit request operations.
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 10.0
 */
@Stateless
public class SepaDirectDebitJob extends Job {

    /** The sepa direct debit job bean. */
    @Inject
    private SepaDirectDebitJobBean sepaDirectDebitJobBean;
    
    private static final String APPLIES_TO_NAME = "JobInstance_SepaDirectDebitJob";

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        sepaDirectDebitJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.PAYMENT;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();        
       
        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("SepaJob_nbRuns");
        nbRuns.setAppliesTo(APPLIES_TO_NAME);
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("SepaJob_waitingMillis");
        waitingMillis.setAppliesTo(APPLIES_TO_NAME);
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, waitingMillis);
        
        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("SepaJob_ddRequestBuilder");
        payentGatewayCF.setAppliesTo(APPLIES_TO_NAME);
        payentGatewayCF.setActive(true);
        payentGatewayCF.setDescription("DDRequest builder");
        payentGatewayCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        payentGatewayCF.setEntityClazz(DDRequestBuilder.class.getName());
        payentGatewayCF.setValueRequired(true);
        payentGatewayCF.setGuiPosition("tab:Configuration:0;field:2");
        result.put("SepaJob_ddRequestBuilder", payentGatewayCF);
        
        // CF to set a custom script filtering AOs to pay
        CustomFieldTemplate aoFilterScript = new CustomFieldTemplate();
        final String cfAoFilterScriptCode = "SepaJob_aoFilterScript";
        aoFilterScript.setCode(cfAoFilterScriptCode);
        aoFilterScript.setAppliesTo(APPLIES_TO_NAME);
        aoFilterScript.setActive(true);
        aoFilterScript.setDescription(resourceMessages.getString("paymentJob.aoFilterScript"));
        aoFilterScript.setFieldType(CustomFieldTypeEnum.ENTITY);
        aoFilterScript.setEntityClazz("org.meveo.model.scripts.ScriptInstance");
        aoFilterScript.setGuiPosition("tab:Configuration:0;field:3");
        result.put(cfAoFilterScriptCode, aoFilterScript);
        
        CustomFieldTemplate sellerCF = new CustomFieldTemplate();
        final String sellerCFcode = "SepaJob_seller";
        sellerCF.setCode(sellerCFcode);
        sellerCF.setAppliesTo(APPLIES_TO_NAME);
        sellerCF.setActive(true);
        sellerCF.setDescription(resourceMessages.getString("seller.title"));
        sellerCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        sellerCF.setEntityClazz("org.meveo.model.admin.Seller");
        sellerCF.setValueRequired(false);
        sellerCF.setGuiPosition("tab:Configuration:0;field:4");
        result.put(sellerCFcode, sellerCF);

        Map<String, String> lisValuesCreditDebit = new HashMap<String, String>();
        lisValuesCreditDebit.put(PaymentOrRefundEnum.PAYMENT.name(), PaymentOrRefundEnum.PAYMENT.name());
        lisValuesCreditDebit.put(PaymentOrRefundEnum.REFUND.name(), PaymentOrRefundEnum.REFUND.name());
        
        CustomFieldTemplate creditOrDebitCF = new CustomFieldTemplate();
        creditOrDebitCF.setCode("SepaJob_paymentOrRefund");
        creditOrDebitCF.setAppliesTo(APPLIES_TO_NAME);
        creditOrDebitCF.setActive(true);
        creditOrDebitCF.setDefaultValue(PaymentOrRefundEnum.PAYMENT.name());
        creditOrDebitCF.setDescription(resourceMessages.getString("jobExecution.paymentOrRefund"));
        creditOrDebitCF.setFieldType(CustomFieldTypeEnum.LIST);
        creditOrDebitCF.setValueRequired(true);
        creditOrDebitCF.setListValues(lisValuesCreditDebit);
        creditOrDebitCF.setGuiPosition("tab:Configuration:0;field:5");
        result.put("SepaJob_paymentOrRefund", creditOrDebitCF);
        return result;
    }
}