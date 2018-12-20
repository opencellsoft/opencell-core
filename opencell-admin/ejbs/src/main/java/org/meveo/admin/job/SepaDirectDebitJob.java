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
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.service.job.Job;


/**
 * The Class SepaDirectDebitJob generate sepa/paynum or custom files for available DirectDebit request operations.
 */
@Stateless
public class SepaDirectDebitJob extends Job {

    /** The sepa direct debit job bean. */
    @Inject
    private SepaDirectDebitJobBean sepaDirectDebitJobBean;
    
    private static final String APPLIES_TO_NAME = "JOB_SepaDirectDebitJob";

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        sepaDirectDebitJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();        
        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("SepaJob_ddRequestBuilder");
        payentGatewayCF.setAppliesTo(APPLIES_TO_NAME);
        payentGatewayCF.setActive(true);
        payentGatewayCF.setDescription("DDRequest builder");
        payentGatewayCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        payentGatewayCF.setEntityClazz(DDRequestBuilder.class.getName());
        payentGatewayCF.setValueRequired(true);
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
        aoFilterScript.setValueRequired(false);
        aoFilterScript.setDefaultValue("");
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
        sellerCF.setDefaultValue("");
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
        result.put("SepaJob_paymentOrRefund", creditOrDebitCF);
        return result;
    }
}