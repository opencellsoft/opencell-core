package org.meveo.service.script;

import org.joda.time.DateTime;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.UserApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.job.JobApi;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.meveo.commons.utils.ParamBean;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

public class ImportJobNotificationScript extends Script {
    private static final String USER_CODE = "user";
    private static final String FROM_EMAIL = ParamBean.getInstance().getProperty("email.sender", "no_reply@opencellsoft.com");

    private static final String MASS_IMPORT_SUCCESS_EMAIL = "MASS_IMPORT_SUCCESS_EMAIL";
    private static final String MASS_IMPORT_FAILURE_EMAIL = "MASS_IMPORT_FAILURE_EMAIL";
    private static final String MASS_IMPORT_ISSUES_EMAIL = "MASS_IMPORT_ISSUES_EMAIL";

    private static final String CUSTOMER_JOB_CODE = "massImportCustomerJob";
    private static final String CUSTOMER_ACCOUNT_JOB_CODE = "massImportCustomerAccountJob";
    private static final String PAYMENT_METHOD_JOB_CODE = "massImportPaymentMethodJob";
    private static final String BILLING_ACCOUNT_JOB_CODE = "massImportBillingAccountJob";
    private static final String USER_ACCOUNT_JOB_CODE = "massImportUserAccountJob";

    private static final String SUBSCRIPTION_JOB_CODE = "massImportSubscriptionJob";
    private static final String SERVICE_INSTANCE_JOB_CODE = "massImportServiceInstanceJob";
    private static final String ATTRIBUTE_INTSNACE_JOB_CODE = "massImportAttributeInstance";
    private static final String ACCESS_POINT_JOB_CODE = "massImportAccessPoint";

    private final transient JobApi jobApi = (JobApi) getServiceInterface(JobApi.class.getSimpleName());
    private final transient UserApi userApi = (UserApi) getServiceInterface(UserApi.class.getSimpleName());

    private final transient EmailSender emailSender = (EmailSender) getServiceInterface(EmailSender.class.getSimpleName());
    private final transient EmailTemplateService emailTemplateService = (EmailTemplateService) getServiceInterface(EmailTemplateService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        try {
            @SuppressWarnings("unchecked")
            String category = (String) methodContext.get("category");
            JobExecutionResultImpl jobExecutionResult = (JobExecutionResultImpl) methodContext.get("entityOrEvent");
            JobResultStatusEnum jobStatus = getJobResultStatus(jobExecutionResult);
            String userName = jobExecutionResult.getJobInstance().getCfValues() != null ? 
            								jobExecutionResult.getJobInstance().getCfValues().getValues() != null ? 
            											(String) jobExecutionResult.getJobInstance().getCfValues().getValues().get(USER_CODE) : null : null;

            if (category == null || !category.equals("customer") || userName == null) return;
            executeNextJob(jobExecutionResult);

            // if there are no rows to process, we don't send a notification
            if(jobExecutionResult.getNbItemsToProcess() == 0) return;

            String subject;
            String contentHtml;
            Map<Object, Object> params = new HashMap<>();
            EmailTemplate emailTemplate;


            String jobCode = jobExecutionResult.getJobInstance().getCode();
            params.put("entityName", EntityNameEnum.valueOf(jobCode).entity);
            switch (jobStatus) {
                case SUCCESS:
                    emailTemplate = emailTemplateService.findByCode(MASS_IMPORT_SUCCESS_EMAIL);
                    DateTime startDateTime = new DateTime(jobExecutionResult.getStartDate());
                    params.put("date", startDateTime.toLocalDate());
                    params.put("time", startDateTime.toLocalTime());
                    contentHtml = evaluateExpression(emailTemplate.getHtmlContent(), params, String.class);
                    subject = evaluateExpression(emailTemplate.getSubject(), params, String.class);
                    break;
                case FAILURE:
                    emailTemplate = emailTemplateService.findByCode(MASS_IMPORT_FAILURE_EMAIL);
                    contentHtml = evaluateExpression(emailTemplate.getHtmlContent(), params, String.class);
                    subject = evaluateExpression(emailTemplate.getSubject(), params, String.class);
                    break;
                default:
                    emailTemplate = emailTemplateService.findByCode(MASS_IMPORT_ISSUES_EMAIL);
                    contentHtml = evaluateExpression(emailTemplate.getHtmlContent(), params, String.class);
                    subject = evaluateExpression(emailTemplate.getSubject(), params, String.class);
                    break;
            }

            List<String> toEmails = List.of(userApi.find(userName).getEmail());
            emailSender.send(FROM_EMAIL, null, toEmails, subject, null, contentHtml);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeNextJob(JobExecutionResultImpl jobExecutionResult) {
        String nextJobCode;
        switch (jobExecutionResult.getJobInstance().getCode()) {
            case CUSTOMER_JOB_CODE:
                nextJobCode = CUSTOMER_ACCOUNT_JOB_CODE;
                break;
            case CUSTOMER_ACCOUNT_JOB_CODE:
                nextJobCode = PAYMENT_METHOD_JOB_CODE;
                break;
            case PAYMENT_METHOD_JOB_CODE:
                nextJobCode = BILLING_ACCOUNT_JOB_CODE;
                break;
            case BILLING_ACCOUNT_JOB_CODE:
                nextJobCode = USER_ACCOUNT_JOB_CODE;
                break;
            case SUBSCRIPTION_JOB_CODE:
                nextJobCode = SERVICE_INSTANCE_JOB_CODE;
                break;
            case SERVICE_INSTANCE_JOB_CODE:
                nextJobCode = ATTRIBUTE_INTSNACE_JOB_CODE;
                break;
            case ATTRIBUTE_INTSNACE_JOB_CODE:
                nextJobCode = ACCESS_POINT_JOB_CODE;
                break;
            default:
                return;
        }
        JobInstanceInfoDto jobInstanceInfoDto = new JobInstanceInfoDto();
        jobInstanceInfoDto.setCode(nextJobCode);
        CustomFieldsDto customFields = new CustomFieldsDto();
        CustomFieldDto customFieldDto = new CustomFieldDto();
        customFieldDto.setCode(USER_CODE);
        customFieldDto.setStringValue((String) jobExecutionResult.getJobInstance().getCfValues().getValues().get(USER_CODE));
        customFields.setCustomField(List.of(customFieldDto));
        jobInstanceInfoDto.setCustomFields(customFields);

        jobApi.executeJob(jobInstanceInfoDto, true);
    }

    private JobResultStatusEnum getJobResultStatus(JobExecutionResultImpl jobExecutionResult) {
        if(jobExecutionResult.getNbItemsToProcess() == 0)
            return JobResultStatusEnum.NULL;
        else if (jobExecutionResult.getNbItemsProcessedWithError() + 1 == jobExecutionResult.getNbItemsToProcess())
            return JobResultStatusEnum.FAILURE;
        else if(jobExecutionResult.getNbItemsCorrectlyProcessed() < jobExecutionResult.getNbItemsToProcess())
            return JobResultStatusEnum.PARTIAL_SUCCESS;
        else return JobResultStatusEnum.SUCCESS;
    }

    private enum JobResultStatusEnum {
        SUCCESS,
        FAILURE,
        PARTIAL_SUCCESS,
        NULL
    }

    private enum EntityNameEnum {
        massImportCustomerJob("Customer"),
        massImportCustomerAccountJob("CustomerAccount"),
        massImportPaymentMethodJob("PaymentMethod"),
        massImportBillingAccountJob("BillingAccount"),
        massImportUserAccountJob("UserAccount"),
        massImportSubscriptionJob("Subscription"),
        massImportServiceInstanceJob("ServiceInstance"),
        massImportAttributeInstance("AttributeInstance"),
        massImportAccessPoint("AccessPoint");

        String entity;

        EntityNameEnum(String entity) {
            this.entity = entity;
        }

    }
}