package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PAID;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.dunning.DunningLevelInstanceStatusEnum.IN_PROGRESS;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionModeEnum.AUTOMATIC;
import static org.meveo.model.payments.ActionTypeEnum.*;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.*;
import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.*;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.*;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class TriggerCollectionPlanLevelsJobBean extends BaseJobBean {

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private DunningCollectionPlanService collectionPlanService;

    @Inject
    private DunningCollectionPlanStatusService collectionPlanStatusService;

    @Inject
    private DunningLevelInstanceService levelInstanceService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private InvoiceService invoiceService;

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<Long> collectionPlanToProcess = collectionPlanService.getActiveCollectionPlansIds();
        jobExecutionResult.setNbItemsToProcess(collectionPlanToProcess.size());
        for (Long collectionPlanId : collectionPlanToProcess) {
            try {
                process(collectionPlanId, jobExecutionResult);
            } catch (Exception exception) {
                jobExecutionResult.addErrorReport(exception.getMessage());
            }
        }
        jobExecutionResult.setNbItemsCorrectlyProcessed(collectionPlanToProcess.size() - jobExecutionResult.getNbItemsProcessedWithError());
    }

    /**
     * Process collection plans
     *
     * @param collectionPlanId   Collection plan id to process
     * @param jobExecutionResult Job execution result
     */
    private void process(Long collectionPlanId, JobExecutionResultImpl jobExecutionResult) {
        DunningCollectionPlan collectionPlan = collectionPlanService.findById(collectionPlanId);
        Date dateToCompare;
        Date today = new Date();
        int index = 0;
        int nextLevel = 0;
        String lastAction = "";
        String nextAction = "";
        boolean updateCollectionPlan = false;
        for (DunningLevelInstance levelInstance : collectionPlan.getDunningLevelInstances()) {
            dateToCompare = DateUtils.addDaysToDate(collectionPlan.getStartDate(),
                    ofNullable(collectionPlan.getPauseDuration()).orElse(0) + levelInstance.getDaysOverdue());
            if (levelInstance.getLevelStatus() != DunningLevelInstanceStatusEnum.DONE
                    && !collectionPlan.getRelatedInvoice().getPaymentStatus().equals(PAID)
                    && today.after(dateToCompare)) {
                nextLevel = index + 1;
                int countAutoActions = 0;
                for (int i = 0; i < levelInstance.getActions().size(); i++) {
                    DunningActionInstance actionInstance = levelInstance.getActions().get(i);
                    if (actionInstance.getActionMode().equals(AUTOMATIC)
                            && actionInstance.getActionStatus().equals(TO_BE_DONE)) {
                        if (actionInstance.getActionType().equals(SCRIPT)) {
                            if (actionInstance.getDunningAction() != null) {
                                scriptInstanceService.execute(actionInstance.getDunningAction().getScriptInstance().getCode(), new HashMap<>());
                            }
                        }
                        if (actionInstance.getActionType().equals(SEND_NOTIFICATION)) {
                            if (actionInstance.getDunningAction().getActionChannel().equals(EMAIL)
                                    || actionInstance.getDunningAction().getActionChannel().equals(LETTER)) {
                                sendEmail(actionInstance.getDunningAction().getActionNotificationTemplate(),
                                        collectionPlan.getRelatedInvoice(), collectionPlan.getLastActionDate(), jobExecutionResult);
                            }
                        }
                        if(actionInstance.getActionType().equals(RETRY_PAYMENT)) {
                            BillingAccount billingAccount = collectionPlan.getBillingAccount();
                            if(billingAccount != null && billingAccount.getCustomerAccount() != null
                                    && billingAccount.getCustomerAccount().getPaymentMethods() != null) {
                                PaymentMethod preferredPaymentMethod = billingAccount.getCustomerAccount()
                                        .getPaymentMethods()
                                        .stream()
                                        .filter(PaymentMethod::isPreferred)
                                        .findFirst()
                                        .orElseThrow(() -> new BusinessException("No preferred payment method found for customer account"
                                                + billingAccount.getCustomerAccount().getCode()));
                                CustomerAccount customerAccount = billingAccount.getCustomerAccount();
                                long amountToPay = collectionPlan.getRelatedInvoice().getNetToPay().longValue();
                                Invoice invoice = collectionPlan.getRelatedInvoice();
                                if(invoice.getRecordedInvoice() == null) {
                                    throw new BusinessException("No getRecordedInvoice for the invoice "
                                            + invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber());
                                }
                                PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, preferredPaymentMethod, null);
                                doPayment(preferredPaymentMethod, customerAccount, amountToPay,
                                        asList(invoice.getRecordedInvoice().getId()), paymentGateway);
                            }
                        }
                        actionInstance.setActionStatus(DunningActionInstanceStatusEnum.DONE);
                        countAutoActions++;
                        lastAction = actionInstance.getCode();
                        if (i + 1 < levelInstance.getActions().size()) {
                            nextAction = levelInstance.getActions().get(i + 1).getCode();
                        }
                    }
                }
                collectionPlan.setLastActionDate(new Date());
                collectionPlan.setLastAction(lastAction);
                collectionPlan.setNextAction(nextAction);
                updateCollectionPlan = true;
                updateLevelInstanceAndCollectionPlan(collectionPlan, levelInstance, countAutoActions, nextLevel);
            }
            if(levelInstance.getDunningLevel() == null) {
                jobExecutionResult.addErrorReport("No dunning level associated to level instance id " +  levelInstance.getId());
            } else {
                levelInstanceService.update(levelInstance);
            }
            index++;
        }
        if (updateCollectionPlan) {
            collectionPlanService.update(collectionPlan);
        }
    }

    private void sendEmail(EmailTemplate emailTemplate,
                           Invoice invoice, Date lastActionDate, JobExecutionResultImpl jobExecutionResult) {
        if(invoice.getSeller() != null && invoice.getSeller().getContactInformation() != null
                && invoice.getSeller().getContactInformation().getEmail() != null
                && !invoice.getSeller().getContactInformation().getEmail().isBlank()) {
            Seller seller = invoice.getSeller();
            Map<Object, Object> params = new HashMap<>();
            BillingAccount billingAccount =
                    billingAccountService.findById(invoice.getBillingAccount().getId(), asList("customerAccount"));
            params.put("billingAccountDescription", billingAccount.getDescription());
            params.put("billingAccountAddressAddress1", billingAccount.getAddress() != null ?
                    billingAccount.getAddress().getAddress1() : "");
            params.put("billingAccountAddressZipCode", billingAccount.getAddress() != null ?
                    billingAccount.getAddress().getZipCode() : "");
            params.put("billingAccountAddressCity", billingAccount.getAddress() != null ?
                    billingAccount.getAddress().getCity() : "");
            params.put("billingAccountContactInformationPhone", billingAccount.getContactInformation() != null ?
                    billingAccount.getContactInformation().getPhone() : "");

            CustomerAccount customerAccount = customerAccountService.findById(billingAccount.getCustomerAccount().getId());
            if(billingAccount.getIsCompany()) {
                params.put("customerAccountLegalEntityTypeCode",
                        ofNullable(billingAccount.getLegalEntityType()).map(Title::getCode).orElse(""));
            } else {
                Name name = ofNullable(billingAccount.getName()).orElse(null);
                Title title = ofNullable(name).map(Name::getTitle).orElse(null);
                params.put("customerAccountLegalEntityTypeCode",
                        ofNullable(title).map(Title::getDescription).orElse(""));
            }
            params.put("customerAccountAddressAddress1", customerAccount.getAddress() != null ?
                    customerAccount.getAddress().getAddress1() : "");
            params.put("customerAccountAddressZipCode", customerAccount.getAddress() != null ?
                    customerAccount.getAddress().getZipCode() : "");
            params.put("customerAccountAddressCity",  customerAccount.getAddress() != null ?
                    customerAccount.getAddress().getCity() : "");
            params.put("customerAccountDescription", customerAccount.getDescription());
            params.put("customerAccountLastName",  customerAccount.getName() != null ?
                    customerAccount.getName().getLastName() : "");
            params.put("customerAccountFirstName",  customerAccount.getName() != null ?
                    customerAccount.getName().getFirstName() : "");

            params.put("invoiceInvoiceNumber", invoice.getInvoiceNumber());
            params.put("invoiceTotal", invoice.getAmountWithTax());
            params.put("invoiceDueDate", formatter.format(invoice.getDueDate()));
            params.put("dayDate", formatter.format(new Date()));

            params.put("dunningCollectionPlanLastActionDate", lastActionDate != null ? formatter.format(lastActionDate) : "");
            List<File> attachments = new ArrayList<>();
            String invoiceFileName = invoiceService.getFullPdfFilePath(invoice, false);
            File attachment = new File(invoiceFileName);
            if (attachment.exists()) {
                attachments.add(attachment);
            } else {
                log.warn("No Pdf file exists for the invoice : {}",
                        ofNullable(invoice.getInvoiceNumber()).orElse(invoice.getTemporaryInvoiceNumber()));
            }
            if(billingAccount.getContactInformation() != null && billingAccount.getContactInformation().getEmail() != null) {
                try {
                    collectionPlanService.sendNotification(seller.getContactInformation().getEmail(),
                            billingAccount.getContactInformation().getEmail(), emailTemplate, params, attachments);
                } catch (Exception exception) {
                    jobExecutionResult.addErrorReport(exception.getMessage());
                }
            } else {
                jobExecutionResult.addErrorReport("Billing account email is missing");
            }
        } else {
            jobExecutionResult.addErrorReport("From email is missing, email sending skipped");
        }
    }

    private void doPayment(PaymentMethod preferredPaymentMethod, CustomerAccount customerAccount,
                           long amountToPay, List<Long> accountOperationsToPayIds, PaymentGateway paymentGateway) {
        if(preferredPaymentMethod.getPaymentType().equals(DIRECTDEBIT) || preferredPaymentMethod.getPaymentType().equals(CARD)) {
            try {
                if(accountOperationsToPayIds != null && !accountOperationsToPayIds.isEmpty()) {
                    if(preferredPaymentMethod.getPaymentType().equals(CARD)) {
                        CardPaymentMethod paymentMethod = (CardPaymentMethod) preferredPaymentMethod;
                        paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds,
                                true, true, paymentGateway, paymentMethod.getCardNumber(),
                                paymentMethod.getCardNumber(), paymentMethod.getHiddenCardNumber(),
                                paymentMethod.getExpirationMonthAndYear(), paymentMethod.getCardType(),
                                true, preferredPaymentMethod.getPaymentType());
                    } else {
                        paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds,
                                true, true, paymentGateway, null, null,
                                null,null,null, true, preferredPaymentMethod.getPaymentType());
                    }
                }
            } catch (Exception exception) {
                throw new BusinessException("Error occurred during payment process : " + exception.getMessage());
            }
        }
    }

    private void updateLevelInstanceAndCollectionPlan(DunningCollectionPlan collectionPlan,
                                     DunningLevelInstance levelInstance, int countAutoActions, int nextLevel) {
        levelInstance = levelInstanceService.refreshOrRetrieve(levelInstance);
        if (nextLevel < collectionPlan.getDunningLevelInstances().size()) {
            collectionPlan.setCurrentDunningLevelSequence(collectionPlan.getDunningLevelInstances().get(nextLevel).getSequence());
        }
        if (collectionPlan.getRelatedInvoice().getPaymentStatus().equals(PAID)) {
            collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
        }
        if(levelInstance.getActions().size() == countAutoActions) {
            levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
        }
        if (levelInstance.getDunningLevel() != null
                && levelInstance.getDunningLevel().isEndOfDunningLevel()
                && collectionPlan.getRelatedInvoice().getPaymentStatus().equals(UNPAID)) {
            collectionPlan.setStatus(collectionPlanStatusService.findByStatus(FAILED));
        }
        if (countAutoActions > 0 && countAutoActions < levelInstance.getActions().size()) {
            levelInstance.setLevelStatus(IN_PROGRESS);
        }
    }
}