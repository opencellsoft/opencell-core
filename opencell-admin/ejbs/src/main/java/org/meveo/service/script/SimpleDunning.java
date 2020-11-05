package org.meveo.service.script;

import org.apache.commons.beanutils.PropertyUtils;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.dunning.DunningDocumentStatus;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.payments.*;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.notification.DefaultNotificationService;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.meveo.admin.job.GenericWorkflowJob.IWF_ENTITY;
import static org.meveo.admin.job.GenericWorkflowJob.WF_ACTUAL_TRANSITION;

public class SimpleDunning extends org.meveo.service.script.Script {

    private static final Logger log = LoggerFactory.getLogger(SimpleDunning.class);

    private CustomerAccountService customerAccountService = (CustomerAccountService) getServiceInterface("CustomerAccountService");
    private AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface("AccountOperationService");
    private DefaultNotificationService defaultNotificationService = (DefaultNotificationService) getServiceInterface("DefaultNotificationService");
    private GenericNotificationService genericNotificationService = (GenericNotificationService) getServiceInterface("GenericNotificationService");
    private DunningDocumentService dunningDocumentService = (DunningDocumentService) getServiceInterface("DunningDocumentService");
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");

    @Override
    public void execute(Map<String, Object> context) {
        log.info(">>> Method context >>>");
        context.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            log.info("{}={}", entry.getKey(), entry.getValue());
        });

        CustomerAccount customerAccount = (CustomerAccount) context.get(IWF_ENTITY);
        GWFTransition gwfTransition = (GWFTransition) context.get(WF_ACTUAL_TRANSITION);

        if ("R0".equals(gwfTransition.getFromStatus()) && "R1".equals(gwfTransition.getToStatus())) {
            sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R1);
        } else if ("R1".equals(gwfTransition.getFromStatus()) && "R2".equals(gwfTransition.getToStatus())) {
            sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R2);
        } else if ("R2".equals(gwfTransition.getFromStatus()) && "R3".equals(gwfTransition.getToStatus())) {
            customerAccount = updateDunningLevel(customerAccount, DunningLevelEnum.R3);
            suspendSubsciptions(customerAccount);
            createDunningDocument(customerAccount);
        } else if ("R3".equals(gwfTransition.getFromStatus()) && "R4".equals(gwfTransition.getToStatus())) {
            customerAccount = updateDunningLevel(customerAccount, DunningLevelEnum.R4);
            updateAccountOperationsToWritteOff(customerAccount);
            customerAccount.setStatus(CustomerAccountStatusEnum.CLOSE);
            customerAccount.setDateStatus(new Date());
            customerAccountService.update(customerAccount);
        } else if ("R3".equals(gwfTransition.getFromStatus()) && "R0".equals(gwfTransition.getToStatus())) {
            customerAccount = sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R0);
            activateSubscription(customerAccount);
            closeDunningDocuments(customerAccount);
        } else if ("R0".equals(gwfTransition.getToStatus())) {
            sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R0);
        }
    }

    private void updateAccountOperationsToWritteOff(CustomerAccount customerAccount) {
        List<AccountOperation> writeOffs = new ArrayList<>();
        List<AccountOperation> accountOperations = customerAccount.getAccountOperations();
        for (AccountOperation accountOperation : accountOperations) {
            WriteOff writeOff = new WriteOff();
            try {
                PropertyUtils.copyProperties(writeOff, accountOperation);
                writeOff.setId(null);
                writeOff.setAccountingWritings(null);
                writeOff.setMatchingAmounts(null);
                writeOff.setPaymentHistories(null);
                writeOff.setInvoices(null);
                writeOff.setAuditableFields(null);
                if (accountOperation.getTransactionCategory() == OperationCategoryEnum.DEBIT)
                    writeOff.setTransactionCategory(OperationCategoryEnum.CREDIT);
                else
                    writeOff.setTransactionCategory(OperationCategoryEnum.DEBIT);
                writeOff.setMatchingStatus(MatchingStatusEnum.L);
                accountOperation.setMatchingStatus(MatchingStatusEnum.L);
                accountOperationService.create(writeOff);
                writeOffs.add(writeOff);
            } catch (Exception e) {
                log.error("error while copy ao to write off: " + accountOperation);
            }
        }
        customerAccount.getAccountOperations().addAll(writeOffs);
    }

    private void closeDunningDocuments(CustomerAccount customerAccount) {
        for (AccountOperation accountOperation : customerAccount.getAccountOperations()) {
            if (accountOperation instanceof RecordedInvoice) {
                ((RecordedInvoice) accountOperation).setDunningDocument(null);
                ((RecordedInvoice) accountOperation).getInvoice().setStatus(InvoiceStatusEnum.PAID);
                accountOperationService.update(accountOperation);
            }
        }

        for(DunningDocument dd : customerAccount.getDunningDocuments()){
            dd.setStatus(DunningDocumentStatus.CLOSED);
            dunningDocumentService.update(dd);
        }
    }

    private void activateSubscription(CustomerAccount customerAccount) {
        subscriptionService.listByCustomer(customerAccount.getCustomer())
                .forEach(sub -> subscriptionService.subscriptionReactivation(sub, new Date()));
    }

    private void suspendSubsciptions(CustomerAccount customerAccount) {
        subscriptionService.listByCustomer(customerAccount.getCustomer())
                .forEach(sub -> subscriptionService.subscriptionSuspension(sub, new Date()));
    }

    private CustomerAccount sendEmailAndUpdateDunningLevel(CustomerAccount customerAccount, DunningLevelEnum dunningLevelEnum) {
        customerAccount = updateDunningLevel(customerAccount, dunningLevelEnum);
        if (dunningLevelEnum == DunningLevelEnum.R0) {
            sendEmail(customerAccount, NotificationEventTypeEnum.TO_R0);
        } else if (dunningLevelEnum == DunningLevelEnum.R1) {
            computeDueBalanceAndSendEmail(customerAccount, NotificationEventTypeEnum.TO_R1);
        } else if (dunningLevelEnum == DunningLevelEnum.R2) {
            computeDueBalanceAndSendEmail(customerAccount, NotificationEventTypeEnum.TO_R2);
        }
        return customerAccount;
    }

    private CustomerAccount updateDunningLevel(CustomerAccount customerAccount, DunningLevelEnum dunningLevelEnum) {
        customerAccount = customerAccountService.refreshOrRetrieve(customerAccount);
        customerAccount.setDunningLevel(dunningLevelEnum);
        customerAccount.setPreviousDunningDateLevel(customerAccount.getDateDunningLevel());
        customerAccount.setDateDunningLevel(new Date());
        return customerAccountService.update(customerAccount);
    }

    private void computeDueBalanceAndSendEmail(CustomerAccount customerAccount, NotificationEventTypeEnum notificationEventType) {
        BigDecimal dueBalance = customerAccountService.customerAccountBalanceDue(customerAccount, new Date());
        customerAccount.setDueBalance(String.format("%s %s", dueBalance.setScale(2, RoundingMode.HALF_UP).toString(), customerAccount.getTradingCurrency().getCurrencyCode()));
        sendEmail(customerAccount, notificationEventType);
    }

    private void sendEmail(CustomerAccount customerAccount, NotificationEventTypeEnum notificationEventType) {
        genericNotificationService.getApplicableNotifications(notificationEventType, customerAccount)
                .forEach(notif -> defaultNotificationService.fireNotification(notif, customerAccount));
    }

    private void createDunningDocument(CustomerAccount customerAccount) {
        List<AccountOperation> accountOperations = customerAccount.getAccountOperations();
        DunningDocument dunningDocument = new DunningDocument();
        dunningDocument.setCustomerAccount(customerAccount);
        dunningDocument.setStatus(DunningDocumentStatus.OPEN);

        if (accountOperations != null) {
            List<RecordedInvoice> ris = new ArrayList<>();
            for (AccountOperation accountOperation : accountOperations) {
                if (accountOperation instanceof RecordedInvoice) {
                    if (accountOperation instanceof RecordedInvoice) {
                        ((RecordedInvoice) accountOperation).setDunningDocument(dunningDocument);
                        ((RecordedInvoice) accountOperation).getInvoice().setStatus(InvoiceStatusEnum.DISPUTED);
                        ris.add((RecordedInvoice) accountOperation);
                    }
                }
            }
            dunningDocument.setDueInvoices(ris);
        }

        dunningDocumentService.create(dunningDocument);
    }
}