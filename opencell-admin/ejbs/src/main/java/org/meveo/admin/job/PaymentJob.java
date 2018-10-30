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
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
@Stateless
public class PaymentJob extends Job {

    private static final String APPLIES_TO_NAME = "JOB_PaymentJob";
    
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
        return JobCategoryEnum.PAYMENT;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("nbRuns");
        nbRuns.setAppliesTo(APPLIES_TO_NAME);
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        result.put("nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("waitingMillis");
        waitingMillis.setAppliesTo(APPLIES_TO_NAME);
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
        createAO.setAppliesTo(APPLIES_TO_NAME);
        createAO.setActive(true);
        createAO.setDefaultValue("YES");
        createAO.setDescription("Create AO");
        createAO.setFieldType(CustomFieldTypeEnum.LIST);
        createAO.setValueRequired(false);
        createAO.setListValues(lisValuesYesNo);
        result.put("PaymentJob_createAO", createAO);

        CustomFieldTemplate matchingAO = new CustomFieldTemplate();
        matchingAO.setCode("PaymentJob_matchingAO");
        matchingAO.setAppliesTo(APPLIES_TO_NAME);
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
        creditOrDebit.setAppliesTo(APPLIES_TO_NAME);
        creditOrDebit.setActive(true);
        creditOrDebit.setDefaultValue("Credit");
        creditOrDebit.setDescription(resourceMessages.getString("jobExecution.paymentOrRefund"));
        creditOrDebit.setFieldType(CustomFieldTypeEnum.LIST);
        creditOrDebit.setValueRequired(true);
        creditOrDebit.setListValues(lisValuesCreditDebit);
        result.put("PaymentJob_creditOrDebit", creditOrDebit);

        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("PaymentJob_paymentGateway");
        payentGatewayCF.setAppliesTo(APPLIES_TO_NAME);
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
        cardOrDD.setAppliesTo(APPLIES_TO_NAME);
        cardOrDD.setActive(true);
        cardOrDD.setDefaultValue("CARD");
        cardOrDD.setDescription(resourceMessages.getString("jobExecution.cardOrDD"));
        cardOrDD.setFieldType(CustomFieldTypeEnum.LIST);
        cardOrDD.setValueRequired(true);
        cardOrDD.setListValues(lisValuesCardDD);
        result.put("PaymentJob_cardOrDD", cardOrDD);

        Map<String, String> lisValuesAOorCA = new HashMap<String, String>();
        lisValuesAOorCA.put("AO", "AO");
        lisValuesAOorCA.put("CA", "CA");

        CustomFieldTemplate AOorCA = new CustomFieldTemplate();
        AOorCA.setCode("PaymentJob_AOorCA");
        AOorCA.setAppliesTo(APPLIES_TO_NAME);
        AOorCA.setActive(true);
        AOorCA.setDefaultValue("CA");
        AOorCA.setDescription(resourceMessages.getString("jobExecution.AOorCA"));
        AOorCA.setFieldType(CustomFieldTypeEnum.LIST);
        AOorCA.setValueRequired(true);
        AOorCA.setListValues(lisValuesAOorCA);
        result.put("PaymentJob_AOorCA", AOorCA);

        CustomFieldTemplate fromDueDate = new CustomFieldTemplate();
        fromDueDate.setCode("PaymentJob_fromDueDate");
        fromDueDate.setAppliesTo(APPLIES_TO_NAME);
        fromDueDate.setActive(true);
        fromDueDate.setDescription(resourceMessages.getString("ddrequestLotOp.fromDueDate"));
        fromDueDate.setFieldType(CustomFieldTypeEnum.DATE);
        fromDueDate.setValueRequired(false);
        fromDueDate.setDefaultValue("");
        result.put("PaymentJob_fromDueDate", fromDueDate);
        
        CustomFieldTemplate toDueDate = new CustomFieldTemplate();
        toDueDate.setCode("PaymentJob_toDueDate");
        toDueDate.setAppliesTo(APPLIES_TO_NAME);
        toDueDate.setActive(true);
        toDueDate.setDescription(resourceMessages.getString("ddrequestLotOp.toDueDate"));
        toDueDate.setFieldType(CustomFieldTypeEnum.DATE);
        toDueDate.setValueRequired(false);
        toDueDate.setDefaultValue("");
        result.put("PaymentJob_toDueDate", toDueDate);
        
        // CF to set a custom script filtering AOs to pay
        CustomFieldTemplate aoFilterScript = new CustomFieldTemplate();
        final String cfAoFilterScriptCode = "PaymentJob_aoFilterScript";
        aoFilterScript.setCode(cfAoFilterScriptCode);
        aoFilterScript.setAppliesTo(APPLIES_TO_NAME);
        aoFilterScript.setActive(true);
        aoFilterScript.setDescription(resourceMessages.getString("paymentJob.aoFilterScript"));
        aoFilterScript.setFieldType(CustomFieldTypeEnum.ENTITY);
        aoFilterScript.setEntityClazz("org.meveo.model.scripts.ScriptInstance");
        aoFilterScript.setValueRequired(false);
        aoFilterScript.setDefaultValue("");
        result.put(cfAoFilterScriptCode, aoFilterScript);
        
        // CF to set a custom script computing Due date range 
        CustomFieldTemplate dueDateRangeScript = new CustomFieldTemplate();
        final String cfDueDateRangeScriptCode = "PaymentJob_dueDateRangeScript";
        dueDateRangeScript.setCode(cfDueDateRangeScriptCode);
        dueDateRangeScript.setAppliesTo(APPLIES_TO_NAME);
        dueDateRangeScript.setActive(true);
        dueDateRangeScript.setDescription(resourceMessages.getString("paymentJob.dueDateRangeScript"));
        dueDateRangeScript.setFieldType(CustomFieldTypeEnum.ENTITY);
        dueDateRangeScript.setEntityClazz("org.meveo.model.scripts.ScriptInstance");
        dueDateRangeScript.setValueRequired(false);
        dueDateRangeScript.setDefaultValue("");
        result.put(cfDueDateRangeScriptCode, dueDateRangeScript);

        return result;
    }
}