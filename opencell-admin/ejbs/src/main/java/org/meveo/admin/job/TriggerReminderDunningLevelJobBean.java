package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.DONE;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionModeEnum.AUTOMATIC;
import static org.meveo.model.payments.ActionTypeEnum.SCRIPT;
import static org.meveo.model.payments.ActionTypeEnum.SEND_NOTIFICATION;
import static org.meveo.model.shared.DateUtils.addDaysToDate;
import static org.meveo.model.shared.DateUtils.daysBetween;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.*;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class TriggerReminderDunningLevelJobBean extends BaseJobBean {

    private static final long serialVersionUID = -3301732194304559773L;

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

    @Inject
    private InvoiceService invoiceService;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    private final SimpleDateFormat emailDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @TransactionAttribute(REQUIRED)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<DunningPolicy> policies = policyService.getPolicies(true);
        try {
            int numberOFAllInvoicesProcessed = 0;
            for (DunningPolicy policy : policies) {
                DunningCollectionPlan dunningCollectionPlan = collectionPlanService.findByPolicy(policy);
                boolean cpProcessed = false;
                for (DunningPolicyLevel policyLevel : policy.getDunningLevels()) {
                    if (policyLevel.getDunningLevel() != null && policyLevel.getDunningLevel().isReminder()) {
                        List<Invoice> invoices = policyService.findEligibleInvoicesToTriggerReminder(policy);
                        cpProcessed= processInvoices(invoices, policyLevel.getDunningLevel(), policyLevel, dunningCollectionPlan);
                        jobExecutionResult.setNbItemsToProcess(jobExecutionResult.getNbItemsToProcess() + invoices.size());
                        numberOFAllInvoicesProcessed += invoices.size();
                    }
                }

                if(dunningCollectionPlan != null && cpProcessed) {
                    dunningCollectionPlan.setLastActionDate(new Date());
                    collectionPlanService.update(dunningCollectionPlan);
                }
            }
            jobExecutionResult.addNbItemsCorrectlyProcessed(numberOFAllInvoicesProcessed - jobExecutionResult.getNbItemsProcessedWithError());
        } catch (Exception exception) {
            jobExecutionResult.addErrorReport(exception.getMessage());
        }
    }

    private boolean processInvoices(List<Invoice> invoices, DunningLevel reminderLevel, DunningPolicyLevel policyLevel, DunningCollectionPlan dunningCollectionPlan) {
        Date today = new Date();
        boolean processed = false;
        reminderLevel = levelService.findById(reminderLevel.getId(), asList("dunningActions"));
        for (Invoice invoice : invoices) {
            Date dateToCompare = addDaysToDate(invoice.getDueDate(), reminderLevel.getDaysOverdue());
            if (simpleDateFormat.format(dateToCompare).equals(simpleDateFormat.format(today)) && !invoice.isReminderLevelTriggered()) {
                launchActions(invoice, dunningCollectionPlan, reminderLevel);
                markInvoiceAsReminderAlreadySent(invoice);
                createLevelInstance(policyLevel);
                processed = true;
            }
        }
        return processed;
    }

    private void markInvoiceAsReminderAlreadySent(Invoice invoice) {
        invoice.setReminderLevelTriggered(true);
        invoiceService.update(invoice);
    }

    private void launchActions(Invoice invoice, DunningCollectionPlan dunningCollectionPlan, DunningLevel reminderLevel) {
        DunningLevelInstance levelInstance =
                dunningCollectionPlan.getDunningLevelInstances().stream()
                        .filter(level -> level.getDunningLevel().getId().equals(reminderLevel.getId()))
                        .findFirst().orElse(null);
        for (DunningActionInstance action : levelInstance.getActions()) {
            if (action.getActionMode().equals(AUTOMATIC)) {
                if (action.getActionType().equals(SCRIPT) || action.getActionType().equals(SEND_NOTIFICATION)) {
                    if (action.getActionType().equals(SCRIPT)) {
                        ScriptInstance scriptInstance = action.getDunningAction().getScriptInstance();
                        if (scriptInstance != null) {
                            scriptInstanceService.execute(scriptInstance.getCode(), new HashMap<>());
                        }
                    }
                    if (action.getActionType().equals(SEND_NOTIFICATION)) {
                        if (action.getDunningAction().getActionChannel().equals(EMAIL)
                                || action.getDunningAction().getActionChannel().equals(LETTER)) {
                            sendReminderEmail(action.getDunningAction().getActionNotificationTemplate(), invoice, dunningCollectionPlan);
                        }
                    }
                    action.setActionStatus(DunningActionInstanceStatusEnum.DONE);
                    dunningCollectionPlan.setLastAction(action.getCode());
                    dunningCollectionPlan.setLastActionDate(new Date());
                    dunningCollectionPlan.setDaysOpen(Math.abs((int) daysBetween(new Date(), dunningCollectionPlan.getStartDate())));
                }
            }
            List<DunningPolicyLevel> levels = dunningCollectionPlan.getRelatedPolicy().getDunningLevels();
            levels.sort(comparing(DunningPolicyLevel::getSequence));
            List<DunningAction> nextLevelActions = levels.get(1).getDunningLevel().getDunningActions();
            dunningCollectionPlan.setNextAction(nextLevelActions != null && !nextLevelActions.isEmpty()
                    ? nextLevelActions.get(0).getCode() : null);
            dunningCollectionPlan.setNextActionDate(nextLevelActions != null && !nextLevelActions.isEmpty()
                    ? addDaysToDate(dunningCollectionPlan.getStartDate(), dunningCollectionPlan.getDaysOpen()) : null);
        }
    }

    private void sendReminderEmail(EmailTemplate emailTemplate, Invoice invoice, DunningCollectionPlan dunningCollectionPlan) {
        if(invoice.getSeller() != null && invoice.getSeller().getContactInformation() != null
                && invoice.getSeller().getContactInformation().getEmail() != null
                && !invoice.getSeller().getContactInformation().getEmail().isBlank()) {
            Seller seller = invoice.getSeller();
            Map<Object, Object> params = new HashMap<>();
            BillingAccount billingAccount =
                    billingAccountService.findById(invoice.getBillingAccount().getId(), asList("customerAccount"));
            params.put("billingAccountDescription", billingAccount.getDescription());
            params.put("billingAccountAddressAddress1",
                    billingAccount.getAddress() != null ? billingAccount.getAddress().getAddress1() : "");
            params.put("billingAccountAddressZipCode",
                    billingAccount.getAddress() != null ? billingAccount.getAddress().getZipCode() : "");
            params.put("billingAccountAddressCity",
                    billingAccount.getAddress() != null ? billingAccount.getAddress().getCity() : "");

            ContactInformation contactInformation = billingAccount.getContactInformation();
            if(contactInformation != null) {
                params.put("contactInformationEmail",  contactInformation.getEmail() != null ?
                        contactInformation.getEmail() : "");
                params.put("contactInformationPhone",  contactInformation.getPhone() != null ?
                        contactInformation.getPhone() : "");
                params.put("contactInformationMobile",  contactInformation.getMobile()  != null ?
                        contactInformation.getMobile() : "");
            }
            CustomerAccount customerAccount = customerAccountService.findById(billingAccount.getCustomerAccount().getId());
            params.put("customerAccountFirstName",  customerAccount.getName() != null ?
                    customerAccount.getName().getFirstName() : "");
            params.put("customerAccountLastName",  customerAccount.getName() != null ?
                    customerAccount.getName().getLastName() : "");
            params.put("invoiceInvoiceNumber", invoice.getInvoiceNumber());
            params.put("invoiceDueDate", emailDateFormatter.format(invoice.getDueDate()));
            params.put("invoiceInvoiceDate", emailDateFormatter.format(new Date()));
            DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
            params.put("invoiceAmountWithTax", decimalFormat.format(invoice.getAmountWithTax()));
            params.put("invoiceAmountWithoutTax", decimalFormat.format(invoice.getAmountWithoutTax()));
            params.put("invoicePaymentMethodType", invoice.getPaymentMethodType());
            params.put("invoicePaymentStatus", invoice.getPaymentStatus());
            params.put("invoiceOrderOrderNumber", invoice.getOrder() != null ? invoice.getOrder().getOrderNumber() : "");
            if(dunningCollectionPlan != null) {
                params.put("dunningCollectionPlanId", dunningCollectionPlan.getId());
                params.put("dunningCollectionPlanLastAction", dunningCollectionPlan.getLastAction());
                params.put("dunningCollectionPlanLastActionDate", emailDateFormatter.format(dunningCollectionPlan.getLastActionDate()));
                params.put("dunningCollectionPlanStatusStatus", dunningCollectionPlan.getStatus() != null ? dunningCollectionPlan.getStatus().getStatus() : "");
            }
            if(billingAccount.getIsCompany()) {
                params.put("billingAccountLegalEntityTypeCode",
                        ofNullable(billingAccount.getLegalEntityType()).map(Title::getCode).orElse(""));
            } else {
                Name name = ofNullable(billingAccount.getName()).orElse(null);
                Title title = ofNullable(name).map(Name::getTitle).orElse(null);
                params.put("billingAccountLegalEntityTypeCode",
                        ofNullable(title).map(Title::getDescription).orElse(""));
            }
            params.put("customerAccountAddressAddress1",
                    customerAccount.getAddress() != null ? customerAccount.getAddress().getAddress1() : "");
            params.put("customerAccountAddressZipCode",
                   customerAccount.getAddress() != null ? customerAccount.getAddress().getZipCode() : "");
            params.put("customerAccountAddressCity",
                   customerAccount.getAddress() != null ? customerAccount.getAddress().getCity() : "");
            params.put("customerAccountDescription", customerAccount.getDescription());

            params.put("dayDate", emailDateFormatter.format(new Date()));

            List<File> attachments = new ArrayList<>();
            String invoiceFileName = invoiceService.getFullPdfFilePath(invoice, false);
            File attachment = new File(invoiceFileName);
            if (!attachment.exists()) {
                log.warn("No Pdf file exists for the invoice : {}", ofNullable(invoice.getInvoiceNumber()).orElse(invoice.getTemporaryInvoiceNumber()));
            } else {
                attachments.add(attachment);
            }
            if(billingAccount.getContactInformation() != null && billingAccount.getContactInformation().getEmail() != null) {
                collectionPlanService.sendNotification(seller.getContactInformation().getEmail(),
                        billingAccount, emailTemplate, params, attachments);
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