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

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.payments.impl.*;
import org.meveo.service.script.ScriptInstanceService;

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
    private BillingAccountService billingAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private DunningCollectionPlanService collectionPlanService;

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
        if(invoice.getSeller() != null && invoice.getSeller().getContactInformation() != null
                && invoice.getSeller().getContactInformation().getEmail() != null
                && !invoice.getSeller().getContactInformation().getEmail().isBlank()) {
            Seller seller = invoice.getSeller();
            Map<Object, Object> params = new HashMap<>();
            BillingAccount billingAccount =
                    billingAccountService.findById(invoice.getBillingAccount().getId(), asList("customerAccount"));
            params.put("billingAccount.description", billingAccount.getDescription());
            params.put("billingAccount.address.address1",
                    billingAccount.getAddress() != null ? billingAccount.getAddress().getAddress1() : "");
            params.put("billingAccount.address.zipCode",
                    billingAccount.getAddress() != null ? billingAccount.getAddress().getZipCode() : "");
            params.put("billingAccount.address.city",
                    billingAccount.getAddress() != null ? billingAccount.getAddress().getCity() : "");
            params.put("billingAccount.contactInformation.phone",
                    billingAccount.getContactInformation() != null ? billingAccount.getContactInformation().getPhone() : "");

            CustomerAccount customerAccount = customerAccountService.findById(billingAccount.getCustomerAccount().getId());
            params.put("customerAccount.legalEntityType.code",
                    ofNullable(customerAccount.getLegalEntityType()).map(Title::getCode).orElse(""));
            params.put("customerAccount.address.address1",
                    customerAccount.getAddress() != null ? customerAccount.getAddress().getAddress1() : "");
            params.put("customerAccount.address.zipCode",
                   customerAccount.getAddress() != null ? customerAccount.getAddress().getZipCode() : "");
            params.put("customerAccount.address.city",
                   customerAccount.getAddress() != null ? customerAccount.getAddress().getCity() : "");

            params.put("invoice.invoiceNumber", invoice.getInvoiceNumber());
            params.put("invoice.dueDate", invoice.getDueDate());
            params.put("invoice.total", invoice.getAmountWithTax());
            params.put("day.date", new Date());

            if(billingAccount.getContactInformation() != null && billingAccount.getContactInformation().getEmail() != null) {
                collectionPlanService.sendNotification(seller.getContactInformation().getEmail(),
                        billingAccount.getContactInformation().getEmail(), emailTemplate, params);
            } else {
                throw new BusinessException("Billing account email is missing");
            }
        } else {
            throw new BusinessException("From email is missing, email sending skipped");
        }
    }

    private DunningLevelInstance createLevelInstance(DunningPolicyLevel policyLevel) {
        DunningLevelInstance levelInstance = new DunningLevelInstance();
        levelInstance.setSequence(policyLevel.getSequence());
        levelInstance.setDaysOverdue(policyLevel.getDunningLevel().getDaysOverdue());
        levelInstance.setCollectionPlanStatus(policyLevel.getCollectionPlanStatus());
        levelInstance.setLevelStatus(DONE);
        levelInstance.setDunningLevel(policyLevel.getDunningLevel());
        levelInstanceService.create(levelInstance);
        return levelInstance;
    }
}