package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionModeEnum.AUTOMATIC;
import static org.meveo.model.payments.ActionTypeEnum.SCRIPT;
import static org.meveo.model.payments.ActionTypeEnum.SEND_NOTIFICATION;
import static org.meveo.model.shared.DateUtils.addDaysToDate;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.communication.impl.EmailSender;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningLevelInstanceService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.util.ApplicationProvider;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class TriggerReminderDunningLevelJobBean extends BaseJobBean {

    @Inject
    private DunningPolicyService policyService;

    @Inject
    private DunningLevelInstanceService levelInstanceService;

    @Inject
    private DunningLevelService levelService;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EmailSender emailSender;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @TransactionAttribute(REQUIRED)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<DunningPolicy> policies = policyService.getPolicies(true);
        jobExecutionResult.setNbItemsToProcess(policies.size());
        try {
            for (DunningPolicy policy : policies) {
                for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
                    if (policyLevel.getDunningLevel() != null && policyLevel.getDunningLevel().isReminder()) {
                        List<Invoice> invoices = policyService.findEligibleInvoicesForPolicy(policy);
                        processInvoices(invoices, policyLevel.getDunningLevel(), policyLevel);
                    }
                }
            }
            jobExecutionResult.addNbItemsCorrectlyProcessed(policies.size() - jobExecutionResult.getNbItemsProcessedWithError());
        } catch (Exception exception) {
            jobExecutionResult.addErrorReport(exception.getMessage());
        }
    }

    private void processInvoices(List<Invoice> invoices, DunningLevel reminderLevel, DunningPolicyLevel policyLevel) {
        Date today = new Date();
        reminderLevel = levelService.findById(reminderLevel.getId(), asList("dunningActions"));
        for (Invoice invoice : invoices) {
            Date dateToCompare = addDaysToDate(invoice.getDueDate(), reminderLevel.getDaysOverdue());
            if (simpleDateFormat.format(dateToCompare).equals(simpleDateFormat.format(today))) {
                launchActions(reminderLevel.getDunningActions(), invoice);
                createLevelInstance(policyLevel);
            }
        }
    }

    private void launchActions(List<DunningAction> actions, Invoice invoice) {
        for (DunningAction action : actions) {
            if (action.getActionMode().equals(AUTOMATIC)) {
                if (action.getActionType().equals(SCRIPT)) {
                    if (action.getScriptInstance() != null) {
                        scriptInstanceService.execute(action.getScriptInstance().getCode(), new HashMap<>());
                    }
                }
                if (action.getActionType().equals(SEND_NOTIFICATION)) {
                    if (action.getActionChannel().equals(EMAIL) || action.getActionChannel().equals(LETTER)) {
                        sendReminderEmail(action.getActionNotificationTemplate(), invoice);
                    }
                }
            }
        }
    }

    private void sendReminderEmail(EmailTemplate emailTemplate, Invoice invoice) {
        Map<Object, Object> params = new HashMap<>();
        BillingAccount billingAccount = billingAccountService.findById(invoice.getBillingAccount().getId(), asList("customerAccount"));
        params.put("billingAccount.description", billingAccount.getDescription());
        params.put("billingAccount.address.address1", billingAccount.getAddress().getAddress1());
        params.put("billingAccount.address.zipCode", billingAccount.getAddress().getZipCode());
        params.put("billingAccount.address.city", billingAccount.getAddress().getCity());
        params.put("billingAccount.contactInformation.phone", billingAccount.getContactInformation().getPhone());

        CustomerAccount customerAccount = customerAccountService.findById(billingAccount.getCustomerAccount().getId());
        params.put("customerAccount.legalEntityType.code",
                ofNullable(customerAccount.getLegalEntityType()).map(Title::getCode).orElse(""));
        params.put("customerAccount.address.address1", customerAccount.getAddress().getAddress1());
        params.put("customerAccount.address.zipCode", customerAccount.getAddress().getZipCode());
        params.put("customerAccount.address.city", customerAccount.getAddress().getCity());

        params.put("invoice.invoiceNumber", invoice.getInvoiceNumber());
        params.put("invoice.dueDate", invoice.getDueDate());
        params.put("invoice.total", invoice.getAmountWithTax());
        params.put("day.date", new Date());

        emailTemplate = emailTemplateService.findById(emailTemplate.getId());
        String subject = evaluateExpression(emailTemplate.getSubject(), params, String.class);
        String content = evaluateExpression(emailTemplate.getTextContent(), params, String.class);
        String contentHtml = evaluateExpression(emailTemplate.getHtmlContent(), params, String.class);
        emailSender.send(appProvider.getEmail(), asList(appProvider.getEmail()),
                asList(billingAccount.getContactInformation().getEmail()), null, null,
                subject, content, contentHtml, null, null, false);
    }

    private DunningLevelInstance createLevelInstance(DunningPolicyLevel policyLevel) {
        DunningLevelInstance levelInstance = new DunningLevelInstance();
        levelInstance.setSequence(policyLevel.getSequence());
        levelInstance.setDaysOverdue(policyLevel.getDunningLevel().getDaysOverdue());
        levelInstance.setCollectionPlanStatus(policyLevel.getCollectionPlanStatus());
        levelInstance.setPolicyLevel(policyLevel);
        levelInstance.setLevelStatus(DONE);
        levelInstance.setDunningLevel(policyLevel.getDunningLevel());
        levelInstanceService.create(levelInstance);
        return levelInstance;
    }
}