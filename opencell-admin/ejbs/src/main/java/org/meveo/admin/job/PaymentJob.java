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
import org.meveo.model.payments.PaymentGateway;
import org.meveo.service.job.Job;

/**
 * The Class PaymentJob, create payment or payout for all opened account operations.
 * 
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
@Stateless
public class PaymentJob extends Job {

    /** The payment job bean. */
    @Inject
    private PaymentJobBean paymentJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        paymentJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }


    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("nbRuns");
        nbRuns.setAppliesTo("JOB_PaymentJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        result.put("nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("waitingMillis");
        waitingMillis.setAppliesTo("JOB_PaymentJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        result.put("waitingMillis", waitingMillis);

        Map<String, String> lisValuesYesNo = new HashMap<String, String>();
        lisValuesYesNo.put("YES", "YES");
        lisValuesYesNo.put("NO", "NO");

        CustomFieldTemplate createAO = new CustomFieldTemplate();
        createAO.setCode("PaymentJob_createAO");
        createAO.setAppliesTo("JOB_PaymentJob");
        createAO.setActive(true);
        createAO.setDefaultValue("YES");
        createAO.setDescription("Create AO");
        createAO.setFieldType(CustomFieldTypeEnum.LIST);
        createAO.setValueRequired(false);
        createAO.setListValues(lisValuesYesNo);
        result.put("PaymentJob_createAO", createAO);

        CustomFieldTemplate matchingAO = new CustomFieldTemplate();
        matchingAO.setCode("PaymentJob_matchingAO");
        matchingAO.setAppliesTo("JOB_PaymentJob");
        matchingAO.setActive(true);
        matchingAO.setDefaultValue("YES");
        matchingAO.setDescription("Matching AO");
        matchingAO.setFieldType(CustomFieldTypeEnum.LIST);
        matchingAO.setValueRequired(false);
        matchingAO.setListValues(lisValuesYesNo);
        result.put("PaymentJob_matchingAO", matchingAO);

        Map<String, String> lisValuesCreditDebit = new HashMap<String, String>();
        lisValuesCreditDebit.put("Credit", "Payment");
        lisValuesCreditDebit.put("Debit", "Refund");
        
        CustomFieldTemplate creditOrDebit = new CustomFieldTemplate();
        creditOrDebit.setCode("PaymentJob_creditOrDebit");
        creditOrDebit.setAppliesTo("JOB_PaymentJob");
        creditOrDebit.setActive(true);
        creditOrDebit.setDefaultValue("Credit");
        creditOrDebit.setDescription(resourceMessages.getString("jobExecution.paymentOrRefund"));
        creditOrDebit.setFieldType(CustomFieldTypeEnum.LIST);
        creditOrDebit.setValueRequired(true);
        creditOrDebit.setListValues(lisValuesCreditDebit);
        result.put("PaymentJob_creditOrDebit", creditOrDebit);

        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("PaymentJob_paymentGateway");
        payentGatewayCF.setAppliesTo("JOB_PaymentJob");
        payentGatewayCF.setActive(true);
        payentGatewayCF.setDescription("Payent gateway");
        payentGatewayCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        payentGatewayCF.setEntityClazz(PaymentGateway.class.getName());
        payentGatewayCF.setValueRequired(false);
        result.put("PaymentJob_paymentGateway", payentGatewayCF);
        
        Map<String, String> lisValuesCardDD = new HashMap<String, String>();
        lisValuesCardDD.put("CARD", "Card");
        lisValuesCardDD.put("DIRECTDEBIT", "Sepa");
        
        CustomFieldTemplate cardOrDD = new CustomFieldTemplate();
        cardOrDD.setCode("PaymentJob_cardOrDD");
        cardOrDD.setAppliesTo("JOB_PaymentJob");
        cardOrDD.setActive(true);
        cardOrDD.setDefaultValue("CARD");
        cardOrDD.setDescription(resourceMessages.getString("jobExecution.cardOrDD"));
        cardOrDD.setFieldType(CustomFieldTypeEnum.LIST);
        cardOrDD.setValueRequired(true);
        cardOrDD.setListValues(lisValuesCardDD);
        result.put("PaymentJob_cardOrDD", cardOrDD);
        
        return result;
    }

}