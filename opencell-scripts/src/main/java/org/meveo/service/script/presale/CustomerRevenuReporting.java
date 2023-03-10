package org.meveo.service.script.presale;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.script.finance.ReportExtractScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * @author anasseh * */
public class CustomerRevenuReporting extends ReportExtractScript {
    private static final Logger log = LoggerFactory.getLogger(CustomerRevenuReporting.class);
    private CustomerAccountService customerAccountService = (CustomerAccountService) getServiceInterface(CustomerAccountService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        try {
            log.debug("execute executeContext:{}", executeContext);
            Date startDate = (Date) executeContext.get(ReportExtractScript.START_DATE);
            Date endDate = (Date) executeContext.get(ReportExtractScript.END_DATE);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("startDateIN", startDate);
            params.put("endDateIN", endDate);
            String query = "Select ao.customerAccount.id , sum (ao.amount) as sum_amount from AccountOperation ao where ao.transactionCategory = 'DEBIT' and ao.dueDate >=:startDateIN "
                    + "and ao.dueDate <:endDateIN  group by ao.customerAccount order by sum_amount desc";
            List<Object[]> aos = (List<Object[]>) customerAccountService.executeSelectQuery(query, params);
            log.debug("execute aos size:{}", aos == null ? null : aos.size());
            String dirOutput = String.valueOf(executeContext.get(ReportExtractScript.DIR));
            String filename = String.valueOf(executeContext.get(CustomerRevenuReporting.FILENAME));
            CsvBuilder csvBuilder = new CsvBuilder(";", false);
            String[] header = { "CA_DESC", "C_DESC", "SUM_REVENUE" };
            csvBuilder.appendValues(header);
            csvBuilder.startNewLine();
            for (Object[] ao : aos) {
                CustomerAccount ca = customerAccountService.findById(((Long) ao[0]));
                csvBuilder.appendValue(ca.getDescription());
                csvBuilder.appendValue(ca.getCustomer().getDescription());
                csvBuilder.appendValue(round((BigDecimal) ao[1]));
                csvBuilder.startNewLine();
            }
            csvBuilder.toFile(dirOutput + File.separator + filename);
            log.debug("execute file generated:{}", dirOutput + File.separator + filename);
        } catch (Exception e) {
            log.error("Error on CustomerRevenuReporting:", e);
            throw new BusinessException(e.getMessage());
        }
    }

    private String round(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        if (amount.scale() > 4) {
            String amountAsString = "" + amount;
            amount = new BigDecimal(amount.longValue() + "." + amountAsString.substring(amountAsString.indexOf(".") + 1).substring(0, 4));
        }
        amount = amount.setScale(2, RoundingMode.UP);
        return amount.toPlainString();
    }
}