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
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.job.Job;

@Stateless
public class PaymentCardJob extends Job {

    @Inject
    private PaymentCardJobBean paymentCardJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        paymentCardJobBean.execute(result, jobInstance);
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
        nbRuns.setAppliesTo("JOB_PaymentCardJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        result.put("nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("waitingMillis");
        waitingMillis.setAppliesTo("JOB_PaymentCardJob");
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
        createAO.setCode("PaymentCardJob_createAO");
        createAO.setAppliesTo("JOB_PaymentCardJob");
        createAO.setActive(true);
        createAO.setDefaultValue("YES");
        createAO.setDescription("Create AO");
        createAO.setFieldType(CustomFieldTypeEnum.LIST);
        createAO.setValueRequired(false);
        createAO.setListValues(lisValuesYesNo);
        result.put("PaymentCardJob_createAO", createAO);

        CustomFieldTemplate matchingAO = new CustomFieldTemplate();
        matchingAO.setCode("PaymentCardJob_matchingAO");
        matchingAO.setAppliesTo("JOB_PaymentCardJob");
        matchingAO.setActive(true);
        matchingAO.setDefaultValue("YES");
        matchingAO.setDescription("Matching AO");
        matchingAO.setFieldType(CustomFieldTypeEnum.LIST);
        matchingAO.setValueRequired(false);
        matchingAO.setListValues(lisValuesYesNo);
        result.put("PaymentCardJob_matchingAO", matchingAO);

        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("PaymentCardJob_paymentGateway");
        payentGatewayCF.setAppliesTo("JOB_PaymentCardJob");
        payentGatewayCF.setActive(true);
        payentGatewayCF.setDescription("Payent gateway");
        payentGatewayCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        payentGatewayCF.setEntityClazz(PaymentGateway.class.getName());
        payentGatewayCF.setValueRequired(true);
        result.put("PaymentCardJob_paymentGateway", payentGatewayCF);
        
        return result;
    }
}