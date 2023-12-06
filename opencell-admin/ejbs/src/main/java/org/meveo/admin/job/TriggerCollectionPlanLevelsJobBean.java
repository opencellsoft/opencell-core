package org.meveo.admin.job;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PAID;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.DONE;
import static org.meveo.model.dunning.DunningActionInstanceStatusEnum.TO_BE_DONE;
import static org.meveo.model.payments.ActionChannelEnum.EMAIL;
import static org.meveo.model.payments.ActionChannelEnum.LETTER;
import static org.meveo.model.payments.ActionModeEnum.AUTOMATIC;
import static org.meveo.model.payments.ActionTypeEnum.*;
import static org.meveo.model.payments.DunningCollectionPlanStatusEnum.*;
import static org.meveo.model.payments.PaymentMethodEnum.CARD;
import static org.meveo.model.payments.PaymentMethodEnum.DIRECTDEBIT;
import static org.meveo.model.shared.DateUtils.addDaysToDate;

import org.hibernate.proxy.HibernateProxy;
import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
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
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.*;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.File;
import java.math.BigDecimal;
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

    @Inject
    private DunningActionInstanceService actionInstanceService;

    @EJB
    private TriggerCollectionPlanLevelsJobBean jobBean;

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        List<Long> collectionPlanToProcess = collectionPlanService.getActiveCollectionPlansIds();
        jobExecutionResult.setNbItemsToProcess(collectionPlanToProcess.size());
        for (Long collectionPlanId : collectionPlanToProcess) {
            try {
                jobBean.process(collectionPlanId, jobExecutionResult);
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
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process(Long collectionPlanId, JobExecutionResultImpl jobExecutionResult) {
        DunningCollectionPlan collectionPlan = collectionPlanService.findById(collectionPlanId);
        Date dateToCompare;
        Date today = new Date();
        int index = 0;
        int nextLevel = 0;
        String lastAction = "";
        String nextAction = "";
        boolean updateCollectionPlan = false;
        if (collectionPlan.getDunningLevelInstances() == null || collectionPlan.getDunningLevelInstances().isEmpty()) {
            throw new BusinessException("Collection plan ID : " + collectionPlan.getId() + " has no levelInstances associated");
        }
        if(collectionPlan.getStatus().getStatus() == ACTIVE && collectionPlan.getRelatedInvoice().getPaymentStatus() == PAID) {
            collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
            updateCollectionPlan = true;
        } else {
            collectionPlan.getDunningLevelInstances().sort(Comparator.comparing(DunningLevelInstance::getSequence));
            int nbLevelDone = 0;
            DunningLevelInstance levelInstance= null;
            List<DunningLevelInstance> levelInstances = collectionPlan.getDunningLevelInstances();
            for (int levelsIndex = 0 ; levelsIndex < levelInstances.size(); levelsIndex++) {
                levelInstance = levelInstances.get(levelsIndex);
                dateToCompare = DateUtils.addDaysToDate(collectionPlan.getStartDate(),
                        ofNullable(collectionPlan.getPauseDuration()).orElse(0) + levelInstance.getDaysOverdue());
                if(levelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.DONE) {
                    nbLevelDone++;
                }
                if (levelInstance.getLevelStatus() != DunningLevelInstanceStatusEnum.DONE
                        && !collectionPlan.getRelatedInvoice().getPaymentStatus().equals(PAID)
                        && dateToCompare.before(today)) {
                    nextLevel = index + 1;
                    boolean registerKO = false;
                    for (int i = 0; i < levelInstance.getActions().size(); i++) {
                        DunningActionInstance actionInstance = levelInstance.getActions().get(i);
                        if (actionInstance.getActionMode().equals(AUTOMATIC)
                                && actionInstance.getActionStatus().equals(TO_BE_DONE)) {
                            try {
                                triggerAction(actionInstance, collectionPlan);
                                actionInstance.setActionStatus(DunningActionInstanceStatusEnum.DONE);
                                if (levelInstance.getLevelStatus() == DunningLevelInstanceStatusEnum.TO_BE_DONE) {
                                    levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
                                    levelInstanceService.update(levelInstance);
                                }
                                collectionPlan.setLastActionDate(new Date());
                                collectionPlan.setLastAction(actionInstance.getDunningAction().getActionType().toString());
                            } catch (Exception exception) {
                                registerKO = true;
                                jobExecutionResult.addReport("Collection plan ID : "
                                        + collectionPlan.getId() + "/Level instance ID : "
                                        + levelInstance.getId() + "/Action instance ID : " + actionInstance.getId()
                                        + " : " + exception.getMessage());
                            }
                        }
                        actionInstanceService.update(actionInstance);
                    }
                    if (!registerKO) {
                        if (levelsIndex + 1 < levelInstances.size()) {
                            for (int i = levelsIndex + 1; i < levelInstances.size(); i++) {
                                if(levelInstances.get(i).getActions() != null
                                        && !levelInstances.get(i).getActions().isEmpty()) {
                                    collectionPlan.setNextAction(levelInstances.get(i).getActions().get(0).getDunningAction().getActionType().toString());
                                    collectionPlan.setNextActionDate(addDaysToDate(collectionPlan.getStartDate(), collectionPlan.getDaysOpen()));
                                    break;
                                }
                            }
                        } else {
                            if(levelsIndex == levelInstances.size()) {
                                collectionPlan.setNextAction(null);
                                collectionPlan.setNextActionDate(null);
                            }
                        }
                        collectionPlanService.update(collectionPlan);
                        collectionPlanService.getEntityManager().flush();
                    }
                    updateCollectionPlan = true;
                    if(registerKO) {
                        jobExecutionResult.addNbItemsProcessedWithError(1L);
                    }
                    levelInstance = levelInstanceService.refreshOrRetrieve(levelInstance);
                    if (nextLevel < collectionPlan.getDunningLevelInstances().size()) {
                        collectionPlan.setCurrentDunningLevelSequence(collectionPlan.getDunningLevelInstances().get(nextLevel).getSequence());
                    }
                    if (levelInstance.getDunningLevel() != null
                            && levelInstance.getDunningLevel().isEndOfDunningLevel()
                            && collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.UNPAID)
                            && nbLevelDone == collectionPlan.getDunningLevelInstances().size()) {
                        collectionPlan.setStatus(collectionPlanStatusService.findByStatus(FAILED));
                    }
                    if (collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)) {
                        collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
                    }
                    long countActions = levelInstance.getActions().stream().filter(action -> action.getActionStatus() == DONE).count();
                    if (countActions > 0 && countActions < levelInstance.getActions().size()) {
                        levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.IN_PROGRESS);
                    }
                    if (countActions == levelInstance.getActions().size()) {
                        levelInstance.setLevelStatus(DunningLevelInstanceStatusEnum.DONE);
                    }
                }
                if (levelInstance.getDunningLevel() == null) {
                    throw new BusinessException("No dunning level associated to level instance id " + levelInstance.getId());
                } else {
                    levelInstanceService.update(levelInstance);
                }
                index++;
            }
            if(nbLevelDone == collectionPlan.getDunningLevelInstances().size()) {
                collectionPlan.setNextActionDate(null);
                collectionPlan.setNextAction(null);
                if (collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.UNPAID)) {
                    collectionPlan.setStatus(collectionPlanStatusService.findByStatus(FAILED));
                    updateCollectionPlan = true;
                }
                if (collectionPlan.getRelatedInvoice().getPaymentStatus().equals(InvoicePaymentStatusEnum.PAID)) {
                    collectionPlan.setStatus(collectionPlanStatusService.findByStatus(SUCCESS));
                    updateCollectionPlan = true;
                }
            }
        }
        if (updateCollectionPlan) {
            collectionPlanService.update(collectionPlan);
        }
    }

    private void triggerAction(DunningActionInstance actionInstance, DunningCollectionPlan collectionPlan) {
        if (actionInstance.getActionType().equals(SCRIPT) && actionInstance.getDunningAction() != null) {
            scriptInstanceService.execute(actionInstance.getDunningAction().getScriptInstance().getCode(), new HashMap<>());
        }
        if (actionInstance.getActionType().equals(SEND_NOTIFICATION)
                && (actionInstance.getDunningAction().getActionChannel().equals(EMAIL)
                || actionInstance.getDunningAction().getActionChannel().equals(LETTER))) {
                sendEmail(actionInstance.getDunningAction().getActionNotificationTemplate(),
                        collectionPlan.getRelatedInvoice(), collectionPlan.getLastActionDate());
        }
        if (actionInstance.getActionType().equals(RETRY_PAYMENT)) {
            launchPaymentAction(collectionPlan);
        }
    }

    private void sendEmail(EmailTemplate emailTemplate, Invoice invoice, Date lastActionDate) {
        if (invoice.getSeller() != null && invoice.getSeller().getContactInformation() != null
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
            if (billingAccount.getIsCompany()) {
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
            params.put("customerAccountAddressCity", customerAccount.getAddress() != null ?
                    customerAccount.getAddress().getCity() : "");
            params.put("customerAccountDescription", customerAccount.getDescription());
            params.put("customerAccountLastName", customerAccount.getName() != null ?
                    customerAccount.getName().getLastName() : "");
            params.put("customerAccountFirstName", customerAccount.getName() != null ?
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
                        invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber());
            }
            if (billingAccount.getContactInformation() != null && billingAccount.getContactInformation().getEmail() != null) {
                try {
                    collectionPlanService.sendNotification(seller.getContactInformation().getEmail(),
                            billingAccount, emailTemplate, params, attachments);
                } catch (Exception exception) {
                    throw new BusinessException(exception.getMessage());
                }
            } else {
                throw new BusinessException("The email is missing for the billing account : " + billingAccount.getCode());
            }
        } else {
            throw new BusinessException("The email sending skipped because the from email is missing for the seller : " + invoice.getSeller().getCode());
        }
    }

    private void launchPaymentAction(DunningCollectionPlan collectionPlan) {
        BillingAccount billingAccount = collectionPlan.getBillingAccount();
        if (billingAccount != null && billingAccount.getCustomerAccount() != null
                && billingAccount.getCustomerAccount().getPaymentMethods() != null) {
            PaymentMethod preferredPaymentMethod = billingAccount.getCustomerAccount()
                    .getPaymentMethods()
                    .stream()
                    .filter(PaymentMethod::isPreferred)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("No preferred payment method found for customer account"
                            + billingAccount.getCustomerAccount().getCode()));
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            //PaymentService.doPayment consider amount to pay in cent so amount should be * 100
            long amountToPay = collectionPlan.getRelatedInvoice().getNetToPay().multiply(BigDecimal.valueOf(100)).longValue();
            Invoice invoice = collectionPlan.getRelatedInvoice();
            if (invoice.getRecordedInvoice() == null) {
                throw new BusinessException("No getRecordedInvoice for the invoice "
                        + (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : invoice.getTemporaryInvoiceNumber()));
            }
            PaymentGateway paymentGateway =
                    paymentGatewayService.getPaymentGateway(customerAccount, preferredPaymentMethod, null);
            //jobBean.doPayment(preferredPaymentMethod, customerAccount, amountToPay, asList(invoice.getRecordedInvoice().getId()), paymentGateway);
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void doPayment(PaymentMethod preferredPaymentMethod, CustomerAccount customerAccount,
                          long amountToPay, List<Long> accountOperationsToPayIds, PaymentGateway paymentGateway) {
        if (preferredPaymentMethod.getPaymentType().equals(DIRECTDEBIT) || preferredPaymentMethod.getPaymentType().equals(CARD)) {
            try {
                if (accountOperationsToPayIds != null && !accountOperationsToPayIds.isEmpty()) {
                    if (preferredPaymentMethod.getPaymentType().equals(CARD)) {
                        if (preferredPaymentMethod instanceof HibernateProxy) {
                            preferredPaymentMethod = (PaymentMethod) ((HibernateProxy) preferredPaymentMethod).getHibernateLazyInitializer()
                                    .getImplementation();
                        }
                        CardPaymentMethod paymentMethod = (CardPaymentMethod) preferredPaymentMethod;
                        paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds,
                                true, true, paymentGateway, paymentMethod.getCardNumber(),
                                paymentMethod.getCardNumber(), paymentMethod.getHiddenCardNumber(),
                                paymentMethod.getExpirationMonthAndYear(), paymentMethod.getCardType(),
                                true, preferredPaymentMethod.getPaymentType());
                    } else {
                        paymentService.doPayment(customerAccount, amountToPay, accountOperationsToPayIds,
                                true, true, paymentGateway, null, null,
                                null, null, null, true, preferredPaymentMethod.getPaymentType());
                    }
                }
            } catch (Exception exception) {
                throw new BusinessException("Error occurred during payment process for customer " + customerAccount.getCode(), exception);
            }
        }
    }
}