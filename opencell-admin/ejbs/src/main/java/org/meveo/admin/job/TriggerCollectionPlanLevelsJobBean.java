package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionTypeEnum.*;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.*;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.*;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.*;
import org.meveo.model.shared.DateUtils;
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
import java.util.stream.Collectors;

@Stateless
public class TriggerCollectionPlanLevelsJobBean extends IteratorBasedJobBean<Long> {

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
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private InvoiceService invoiceService;

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess,
                this::process, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     *
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of active collection to process
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return of(new SynchronizedIterator<>(collectionPlanService.getActiveCollectionPlansIds()));
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
                    && !collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)
                    && today.after(dateToCompare)) {
                nextLevel = index + 1;
                int countAutoActions = 0;
                for (int i = 0; i < levelInstance.getActions().size(); i++) {
                    if (levelInstance.getActions().get(i).getActionMode().equals(ActionModeEnum.AUTOMATIC)
                            && levelInstance.getActions().get(i).getActionStatus().equals(DunningActionInstanceStatusEnum.TO_BE_DONE)) {
                        if (levelInstance.getActions().get(i).getActionType().equals(SCRIPT)) {
                            if (levelInstance.getActions().get(i).getDunningAction() != null) {
                                scriptInstanceService.execute(levelInstance.getActions().get(i).getDunningAction().getScriptInstance().getCode(), new HashMap<>());
                            }
                        }
                        if (levelInstance.getActions().get(i).getActionType().equals(SEND_NOTIFICATION)) {
                            if (levelInstance.getActions().get(i).getDunningAction().getActionChannel().equals(EMAIL)
                                    || levelInstance.getActions().get(i).getDunningAction().getActionChannel().equals(LETTER)) {
                                sendEmail(levelInstance.getActions().get(i).getDunningAction().getActionNotificationTemplate(),
                                        collectionPlan.getRelatedInvoice(), collectionPlan.getLastActionDate(), jobExecutionResult);
                            }
                        }
                        if(levelInstance.getActions().get(i).getActionType().equals(RETRY_PAYMENT)) {
                            BillingAccount billingAccount = collectionPlan.getBillingAccount();
                            if(billingAccount != null && billingAccount.getCustomerAccount() != null
                                    && billingAccount.getCustomerAccount().getPaymentMethods() != null) {
                                PaymentMethod preferredPaymentMethod = collectionPlan.getBillingAccount().getCustomerAccount()
                                        .getPaymentMethods()
                                        .stream()
                                        .filter(PaymentMethod::isPreferred)
                                        .findFirst()
                                        .orElseThrow(() -> new BusinessException("No preferred payment method found"));
                                CustomerAccount customerAccount = billingAccount.getCustomerAccount();
                                long amountToPay = collectionPlan.getRelatedInvoice().getNetToPay().longValue();
                                List<AccountOperation> accountOperationsToPay =
                                        accountOperationService.getAOsToPayOrRefundByCA(new Date(1), new Date(),
                                                OperationCategoryEnum.DEBIT, customerAccount.getId());
                                List<Long> accountOperationsToPayIds = new ArrayList<>();
                                if(accountOperationsToPay != null) {
                                    accountOperationsToPayIds = accountOperationsToPay
                                            .stream()
                                            .map(AccountOperation::getId)
                                            .collect(Collectors.toList());
                                }
                                PaymentGateway paymentGateway = paymentGatewayService.getPaymentGateway(customerAccount, preferredPaymentMethod, null);
                                if(preferredPaymentMethod.getPaymentType().equals(PaymentMethodEnum.DIRECTDEBIT)
                                        || preferredPaymentMethod.getPaymentType().equals(PaymentMethodEnum.CARD)) {
                                    try {
                                        paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds,
                                                true, true, paymentGateway, null, null,
                                                null,null,null, true, preferredPaymentMethod.getPaymentType());
                                    } catch (NoAllOperationUnmatchedException | UnbalanceAmountException exception) {
                                        throw new BusinessException(exception);
                                    }
                                }
                            }
                        }
                        levelInstance.getActions().get(i).setActionStatus(DunningActionInstanceStatusEnum.DONE);
                        countAutoActions++;
                        lastAction = levelInstance.getActions().get(i).getCode();
                        if (i + 1 < levelInstance.getActions().size()) {
                            nextAction = levelInstance.getActions().get(i + 1).getCode();
                        }
                    }
                }
                levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
                collectionPlan.setLastActionDate(new Date());
                collectionPlan.setLastAction(lastAction);
                collectionPlan.setNextAction(nextAction);
                updateCollectionPlan = true;
                if (nextLevel < collectionPlan.getDunningLevelInstances().size()) {
                    collectionPlan.setCurrentDunningLevelSequence(collectionPlan.getDunningLevelInstances().get(nextLevel).getSequence());
                }
                if (levelInstance.getDunningLevel().isEndOfDunningLevel() && dateToCompare.before(today)) {
                    collectionPlan.setStatus(collectionPlanStatusService.findByStatus(FAILED));
                }
                if (countAutoActions == levelInstance.getActions().size()
                        || collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)) {
                    collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
                }
                if (countAutoActions > 0 && countAutoActions < levelInstance.getActions().size()) {
                    levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
                }
            }
            levelInstanceService.update(levelInstance);
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
            params.put("customerAccountLegalEntityTypeCode", ofNullable(customerAccount.getLegalEntityType()).map(Title::getCode).orElse(""));
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

            params.put("dunningCollectionPlanLastActionDate", formatter.format(lastActionDate));
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
}