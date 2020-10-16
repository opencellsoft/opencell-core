package org.meveo.service.script;

import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.payments.*;
import org.meveo.service.notification.DefaultNotificationService;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningDocumentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.valueOf;
import static org.meveo.admin.job.GenericWorkflowJob.*;

public class SimpleDunning extends org.meveo.service.script.Script {

    private static final Logger log = LoggerFactory.getLogger(SimpleDunning.class);

    private CustomerAccountService customerAccountService = (CustomerAccountService) getServiceInterface("CustomerAccountService");
    private DefaultNotificationService defaultNotificationService = (DefaultNotificationService) getServiceInterface("DefaultNotificationService");
    private GenericNotificationService genericNotificationService = (GenericNotificationService) getServiceInterface("GenericNotificationService");
    private DunningDocumentService dunningDocumentService = (DunningDocumentService) getServiceInterface("DunningDocumentService");

    @Override
    public void execute(Map<String, Object> context) {
        log.info(">>> Method context >>>");
        context.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            log.info("{}={}", entry.getKey(), entry.getValue());
        });

        WorkflowInstance workflowInstance = (WorkflowInstance) context.get(WF_INS);
        CustomerAccount customerAccount = (CustomerAccount) context.get(IWF_ENTITY);
        WFStatus wfToStatus = (WFStatus) context.get(TO_STATUS);

        if ("R0".equals(workflowInstance.getCurrentStatus().getCode()) && "R1".equals(wfToStatus.getCode())) {
            sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R1);
        } else if("R1".equals(workflowInstance.getCurrentStatus().getCode()) && "R2".equals(wfToStatus.getCode())){
            sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R2);
        }else if ("R0".equals(wfToStatus.getCode())) {
            sendEmailAndUpdateDunningLevel(customerAccount, DunningLevelEnum.R0);
        }
    }

    private void sendEmailAndUpdateDunningLevel(CustomerAccount customerAccount, DunningLevelEnum dunningLevelEnum) {
        customerAccount = customerAccountService.refreshOrRetrieve(customerAccount);
        if (customerAccount.getDunningLevel() == dunningLevelEnum)
            return;
        customerAccount.setDunningLevel(dunningLevelEnum);
        customerAccount.setPreviousDunningDateLevel(customerAccount.getDateDunningLevel());
        customerAccount.setDateDunningLevel(new Date());
        createDunningDocuments(customerAccount);
        customerAccount = customerAccountService.update(customerAccount);

        if (dunningLevelEnum == DunningLevelEnum.R0) {
            sendEmail(customerAccount, NotificationEventTypeEnum.TO_R0);
        } else if (dunningLevelEnum == DunningLevelEnum.R1) {
            computeDueBalanceAndSendEmail(customerAccount, NotificationEventTypeEnum.TO_R1);
        } else if (dunningLevelEnum == DunningLevelEnum.R2){
            computeDueBalanceAndSendEmail(customerAccount, NotificationEventTypeEnum.TO_R2);
        }
    }

    private List<DunningDocument> createDunningDocuments(CustomerAccount customerAccount) {
        List<AccountOperation> accountOperations = customerAccount.getAccountOperations();
        return List.of(createNewDunningDocument(customerAccount, accountOperations));
    }

    private DunningDocument createNewDunningDocument(CustomerAccount customerAccount, List<AccountOperation> accountOperations) {
        DunningDocument dunningDocument = new DunningDocument();
        dunningDocument.setCustomerAccount(customerAccount);
        if (accountOperations != null) {
            List<RecordedInvoice> ris = new ArrayList<>();
            for (AccountOperation accountOperation : accountOperations) {
                ((RecordedInvoice) accountOperation).setDunningDocument(dunningDocument);
                ris.add((RecordedInvoice) accountOperation);
            }
            dunningDocument.setDueInvoices(ris);
        }

        dunningDocumentService.create(dunningDocument);
        return dunningDocument;
    }

    private boolean isDueRecordedInvoice(AccountOperation ao) {
        BigDecimal dueAmount = ao.getTransactionCategory() == OperationCategoryEnum.DEBIT ? ao.getUnMatchingAmount() : ao.getUnMatchingAmount().multiply(valueOf(-1));
        return ao instanceof RecordedInvoice && ao.getDueDate().after(new Date()) && dueAmount.doubleValue() > 0;
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
}