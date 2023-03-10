package org.meveo.service.script.export;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.script.finance.ReportExtractScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportAccountOperationScript extends ReportExtractScript {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportAccountOperationScript.class);
    private AccountOperationService accountOperationService = (AccountOperationService) getServiceInterface(AccountOperationService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> initContext) throws BusinessException {
        try {
            LOGGER.debug("#####################Starting of script ExportAccountOperationScript");
            Date startDate = (Date) initContext.get(ReportExtractScript.START_DATE);
            Date endDate = (Date) initContext.get(ReportExtractScript.END_DATE);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            List<AccountOperation> accountOperations = accountOperationService.list();
            initContext.put(ReportExtractScript.LINE_COUNT, accountOperations.size());
            String exportDir = String.valueOf(initContext.get(ReportExtractScript.DIR));
            File dir = new File(exportDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            CustomerAccount customerAccount = null;
            StringBuilder sb = new StringBuilder();
            sb.append("Transaction Date;Transaction Type;Amount;Due Date;Reference;Transaction Category;Invoice Date;" + "Customer Account Code;Customer Account Description\n");
            for (AccountOperation accountOperation : accountOperations) {
                if (accountOperation.getTransactionDate().equals(startDate) || accountOperation.getTransactionDate().equals(endDate)
                        || (accountOperation.getTransactionDate().after(startDate) && accountOperation.getTransactionDate().before(endDate))) {
                    customerAccount = accountOperation.getCustomerAccount();
                    sb.append(nil(accountOperation.getTransactionDate().toString()) + ";");
                    sb.append(nil(accountOperation.getType()) + ";");
                    sb.append(nil(accountOperation.getAmount().toString()) + ";");
                    sb.append(isNull(accountOperation.getDueDate()) + ";");
                    sb.append(nil(accountOperation.getReference()) + ";");
                    sb.append(nil(accountOperation.getTransactionCategory().toString()) + ";");
                    if (accountOperation instanceof RecordedInvoice) {
                        RecordedInvoice invoice = (RecordedInvoice) accountOperation;
                        sb.append(invoice.getInvoiceDate().toString() + ";");
                    } else {
                        sb.append(";");
                    }
                    sb.append(nil(customerAccount.getCode()) + ";");
                    sb.append(nil(customerAccount.getDescriptionOrCode()) + ";\n");
                }
            }
            String strFilename = String.valueOf(initContext.get(ReportExtractScript.FILENAME));
            LOGGER.debug("output={}", strFilename);
            File file = new File(dir + File.separator + strFilename);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sb.toString());
            fileWriter.close();
            LOGGER.debug("#####################Ending of script ExportAccountOperationScript");
        } catch (Exception e) {
            initContext.put(ReportExtractScript.ERROR_MESSAGE, e.getMessage());
            LOGGER.error("Exception:", e);
            throw new BusinessException(e.getMessage());
        }
    }

    private String isNull(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    private String nil(String word) {
        if (word == null || "null".equalsIgnoreCase(word)) {
            return "";
        } else {
            return word;
        }
    }
}