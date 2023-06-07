package org.meveo.service.script.presale;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.script.finance.ReportExtractScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * @author anasseh * */
public class CustomerPerOfferReporting extends ReportExtractScript {
    private static final Logger log = LoggerFactory.getLogger(CustomerPerOfferReporting.class);
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface(SubscriptionService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        try {
            log.debug("execute executeContext:{}", executeContext);
            Date startDate = (Date) executeContext.get(ReportExtractScript.START_DATE);
            String offerCode = (String) executeContext.get("OFFER_CODE");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("subDateIN", startDate);
            String query = "Select distinct sub.offer, sub.userAccount from Subscription sub where sub.subscriptionDate >:subDateIN ";
            if (!StringUtils.isBlank(offerCode)) {
                query += " and sub.offer.code =:offCodeIN ";
                params.put("offCodeIN", offerCode);
            }
            List<Object[]> rows = (List<Object[]>) subscriptionService.executeSelectQuery(query + "order by sub.offer.code", params);
            log.debug("execute rows size:{}", rows == null ? null : rows.size());
            String dirOutput = String.valueOf(executeContext.get(ReportExtractScript.DIR));
            String filename = String.valueOf(executeContext.get(CustomerPerOfferReporting.FILENAME));
            CsvBuilder csvBuilder = new CsvBuilder(";", false);
            String[] header = { "OFF_CODE", "OFF_DESC", "UA_DESC", "BA_DESC", "CA_DESC", "C_DESC" };
            csvBuilder.appendValues(header);
            csvBuilder.startNewLine();
            for (Object[] row : rows) {
                csvBuilder.appendValue(((OfferTemplate) row[0]).getCode());
                csvBuilder.appendValue(((OfferTemplate) row[0]).getDescription());
                csvBuilder.appendValue(((UserAccount) row[1]).getDescription());
                csvBuilder.appendValue(((UserAccount) row[1]).getBillingAccount().getDescription());
                csvBuilder.appendValue(((UserAccount) row[1]).getBillingAccount().getCustomerAccount().getDescription());
                csvBuilder.appendValue(((UserAccount) row[1]).getBillingAccount().getCustomerAccount().getCustomer().getDescription());
                csvBuilder.startNewLine();
            }
            csvBuilder.toFile(dirOutput + File.separator + filename);
            log.debug("execute file generated:{}", dirOutput + File.separator + filename);
        } catch (Exception e) {
            log.error("Error on CustomerPerOfferReporting:", e);
            throw new BusinessException(e.getMessage());
        }
    }
}