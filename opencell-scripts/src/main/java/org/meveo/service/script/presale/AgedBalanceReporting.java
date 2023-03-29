package org.meveo.service.script.presale;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PENDING;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PPAID;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;
import static org.meveo.model.shared.DateUtils.setDateToEndOfDay;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Name;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.script.finance.ReportExtractScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgedBalanceReporting extends ReportExtractScript {
    private static final Logger log = LoggerFactory.getLogger(AgedBalanceReporting.class);
    private static Provider appProvider;
    private CustomerAccountService customerAccountService = (CustomerAccountService) getServiceInterface(CustomerAccountService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        try {
            appProvider = ((ProviderService) EjbUtils.getServiceInterface("ProviderService")).getProvider();
            String currencyCode = "";
            Map<String, Object> paramsProvider = new HashMap<String, Object>();
            paramsProvider.put("code", appProvider.getCode());
            String queryProvider = "Select pr.currency.currencyCode, pr.id FROM Provider pr where lower(pr.code)=lower(:code)";
            List<Object[]> rowsProvider = (List<Object[]>) customerAccountService.executeSelectQuery(queryProvider, paramsProvider);
            for (Object[] row : rowsProvider) {
                currencyCode = row[0] + "";
            }

            List<String> fetchFields = asList("fields");
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(0, 50, null, null, fetchFields, "dueDate", "DESCENDING");
            int numberOfPeriods = 4;
            String query = getAgedReceivables(null, null, new Date(), null, null, paginationConfiguration, 30, numberOfPeriods, null, null, null, null, null);

            log.debug("execute executeContext:{}", executeContext);
            Map<String, Object> params = new HashMap<String, Object>();

            List<Object[]> rows = (List<Object[]>) customerAccountService.executeSelectQuery(query, params);
            List<AgedReceivableDto> listAgedReceivable = buildDynamicResponse(rows, numberOfPeriods);

            log.debug("execute rows size:{}", rows == null ? null : rows.size());
            String dirOutput = String.valueOf(executeContext.get(ReportExtractScript.DIR));
            String filename = String.valueOf(executeContext.get(ReportExtractScript.FILENAME));
            CsvBuilder csvBuilder = new CsvBuilder(";", false);
            String[] header = { "Customer description", "Customer", "Invoice Number", "Total current", "0_30_DAYS", "30_60_DAYS", "60_90_DAYS", "90_DAYS", "Total overdue", "Total", "Currency", "Amount billed",
                    "Billing currency" };
            csvBuilder.appendValues(header);
            csvBuilder.startNewLine();
            for (AgedReceivableDto agedReceivableDto : listAgedReceivable) {
                csvBuilder.appendValue(agedReceivableDto.getCustomerAccountDescription());
                csvBuilder.appendValue(agedReceivableDto.getCustomerAccountCode());
                csvBuilder.appendValue(agedReceivableDto.getInvoiceNumber());
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getNotYetDue()));
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getTotalAmountByPeriod().get(0)));
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getTotalAmountByPeriod().get(1)));
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getTotalAmountByPeriod().get(2)));
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getTotalAmountByPeriod().get(3)));
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getGeneralTotal()));
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getGeneralTotal()));
                csvBuilder.appendValue(currencyCode);
                csvBuilder.appendValue(round((BigDecimal) agedReceivableDto.getBilledAmount()));
                csvBuilder.appendValue(agedReceivableDto.getTradingCurrency());

                csvBuilder.startNewLine();
            }
            csvBuilder.toFile(dirOutput + File.separator + filename);
            log.debug("execute file generated:{}", dirOutput + File.separator + filename);
        } catch (Exception e) {
            log.error("Error on AgedBalanceReporting:", e);
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

    public String getAgedReceivables(String customerAccountCode, String sellerCode, Date startDate, Date startDueDate, Date endDueDate, PaginationConfiguration paginationConfiguration, Integer stepInDays,
            Integer numberOfPeriods, String invoiceNumber, String customerAccountDescription, String sellerDescription, String tradingCurrency, String functionalCurrency) {
        String datePattern = "yyyy-MM-dd";
        StringBuilder query = new StringBuilder("Select ao.customerAccount.id, sum (case when ao.dueDate >= '").append(DateUtils.formatDateWithPattern(startDate, datePattern))
            .append("'  then  ao.unMatchingAmount else 0 end ) as notYetDue,");
        if (stepInDays != null && numberOfPeriods != null) {
            String alias;
            int step;
            if (numberOfPeriods > 1) {
                query
                    .append("sum (case when ao.dueDate <'" + DateUtils.formatDateWithPattern(startDate, datePattern) + "' and ao.dueDate >'"
                            + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -stepInDays), datePattern) + "' then ao.amountWithoutTax else 0 end ) as sum_1_" + stepInDays + ",")
                    .append("sum (case when ao.dueDate <'" + DateUtils.formatDateWithPattern(startDate, datePattern) + "' and ao.dueDate >'"
                            + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -stepInDays), datePattern) + "' then ao.unMatchingAmount else 0 end ) as sum_1_" + stepInDays + "_awt,")
                    .append("sum (case when ao.dueDate <'" + DateUtils.formatDateWithPattern(startDate, datePattern) + "' and ao.dueDate >'"
                            + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -stepInDays), datePattern) + "' then ao.taxAmount else 0 end ) as sum_1_" + stepInDays + "_tax,");
                for (int iteration = 1; iteration < numberOfPeriods - 1; iteration++) {
                    step = iteration * stepInDays;
                    alias = "as sum_" + (stepInDays * iteration + 1) + "_" + (step * 2);
                    query
                        .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern) + "' and ao.dueDate >'"
                                + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -(step + stepInDays)), datePattern) + "' then ao.amountWithoutTax else 0 end ) ")
                        .append(alias).append(" , ")
                        .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern) + "' and ao.dueDate >'"
                                + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -(step + stepInDays)), datePattern) + "' then ao.unMatchingAmount  else 0 end ) ")
                        .append(alias).append("_awt, ").append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern) + "' and ao.dueDate >'"
                                + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -(step + stepInDays)), datePattern) + "' then  ao.taxAmount else 0 end ) ")
                        .append(alias).append("_tax, ");
                }
            }
            step = numberOfPeriods > 1 ? stepInDays * (numberOfPeriods - 1) : stepInDays;
            query.append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern) + "'  then ao.amountWithoutTax else 0 end ) as sum_" + step + "_up,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern) + "'  then ao.unMatchingAmount else 0 end ) as sum_" + step + "_up_awt,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -step), datePattern) + "' then ao.taxAmount else 0 end ) as sum_" + step + "_up_tax,");
        } else {
            query
                .append("sum (case when ao.dueDate <'" + DateUtils.formatDateWithPattern(startDate, datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern) + "' then ao.amountWithoutTax else 0 end ) as sum_1_30,")
                .append("sum (case when ao.dueDate <'" + DateUtils.formatDateWithPattern(startDate, datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern) + "' then ao.unMatchingAmount else 0 end ) as sum_1_30_awt,")
                .append("sum (case when ao.dueDate <'" + DateUtils.formatDateWithPattern(startDate, datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern) + "' then ao.taxAmount else 0 end ) as sum_1_30_tax,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern) + "' then ao.amountWithoutTax  else 0 end ) as sum_31_60,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern) + "' then ao.unMatchingAmount else 0 end ) as sum_31_60_awt,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -30), datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern) + "' then ao.taxAmount else 0 end ) as sum_31_60_tax,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern) + "' then ao.amountWithoutTax else 0 end ) as sum_61_90,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern) + "' then ao.unMatchingAmount else 0 end ) as sum_61_90_awt,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -60), datePattern) + "' and ao.dueDate >'"
                        + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern) + "' then ao.taxAmount else 0 end ) as sum_61_90_tax,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern) + "'  then ao.amountWithoutTax else 0 end ) as sum_90_up,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern) + "'  then ao.unMatchingAmount else 0 end ) as sum_90_up_awt,")
                .append("sum (case when ao.dueDate <='" + DateUtils.formatDateWithPattern(DateUtils.addDaysToDate(startDate, -90), datePattern) + "'  then ao.taxAmount else 0 end ) as sum_90_up_tax,");
        }
        query.append(
            " ao.customerAccount.dunningLevel, ao.customerAccount.name, ao.customerAccount.description, ao.seller.description, ao.seller.code, ao.dueDate, ao.invoice.tradingCurrency.currency.currencyCode, ao.invoice.id, ao.invoice.invoiceNumber, ao.invoice.amountWithTax, ao.customerAccount.code, ao.invoice.convertedAmountWithTax, ao.invoice.billingAccount.id ")
            .append("from ").append(RecordedInvoice.class.getSimpleName()).append(" as ao");
        QueryBuilder qb = new QueryBuilder(query.toString());
        qb.addSql("(ao.matchingStatus='" + MatchingStatusEnum.O + "' or ao.matchingStatus='" + MatchingStatusEnum.P + "') ");
        qb.addSql("ao.invoice.invoiceType.excludeFromAgedTrialBalance = false");
        ofNullable(customerAccountCode).ifPresent(ca -> qb.addSql("UPPER(ao.customerAccount.code) like '%" + customerAccountCode.toUpperCase() + "%'"));
        ofNullable(customerAccountDescription).ifPresent(caDescription -> qb.addSql("UPPER(ao.customerAccount.description) like '%" + caDescription.toUpperCase() + "%'"));
        ofNullable(sellerDescription).ifPresent(sDescription -> qb.addSql("UPPER(ao.seller.description) like ('%" + sDescription.toUpperCase() + "%')"));
        ofNullable(sellerCode).ifPresent(sel -> qb.addSql("UPPER(ao.seller.code) like '%" + sellerCode.toUpperCase() + "%'"));
        ofNullable(invoiceNumber).ifPresent(invNumber -> qb.addSql("ao.invoice.invoiceNumber = '" + invNumber + "'"));
        ofNullable(tradingCurrency).ifPresent(fc -> qb.addSql("ao.invoice.tradingCurrency.currency.currencyCode = '" + fc + "'"));
        if (startDueDate != null && endDueDate != null) {
            qb.addSql("(ao.dueDate >= '" + DateUtils.formatDateWithPattern(startDueDate, datePattern) + "' and ao.dueDate <= '" + DateUtils.formatDateWithPattern(endDueDate, datePattern) + "')");
        }
        if (DateUtils.compare(startDate, new Date()) < 0) {
            qb.addSql("ao.invoice.status = '" + VALIDATED + "' and ao.invoice.invoiceDate <= '" + DateUtils.formatDateWithPattern(setDateToEndOfDay(startDate), "yyyy-MM-dd HH:mm:ss") + "'");
            qb.addSql("(ao.invoice.paymentStatus = '" + PENDING + "' or ao.invoice.paymentStatus = '" + PPAID + "' or ao.invoice.paymentStatus ='" + UNPAID + "')");
        }
        qb.addGroupCriterion(
            "ao.customerAccount.id, ao.customerAccount.dunningLevel, ao.customerAccount.name, ao.customerAccount.description, ao.seller.description, ao.seller.code, ao.dueDate, ao.amount, ao.invoice.tradingCurrency.currency.currencyCode, ao.invoice.id, ao.invoice.invoiceNumber, ao.invoice.amountWithTax, ao.customerAccount.code, ao.invoice.convertedAmountWithTax, ao.invoice.billingAccount.id ");
        qb.addPaginationConfiguration(paginationConfiguration);
        return qb.getSqlString();
    }

    public List<AgedReceivableDto> buildDynamicResponse(List<Object[]> agedReceivables, int numberOfPeriods) {
        List<AgedReceivableDto> responseDto = new ArrayList<>();
        for (int index = 0; index < agedReceivables.size(); index++) {
            Object[] agedReceivable = agedReceivables.get(index);
            AgedReceivableDto agedReceivableDto = new AgedReceivableDto();
            agedReceivableDto.setNotYetDue((BigDecimal) agedReceivable[1]);
            int sumIndex;
            int startingSumIndex = 2;
            agedReceivableDto.setNetAmountByPeriod(new ArrayList<>());
            agedReceivableDto.setTotalAmountByPeriod(new ArrayList<>());
            agedReceivableDto.setTaxAmountByPeriod(new ArrayList<>());
            for (sumIndex = 0; sumIndex < numberOfPeriods; sumIndex++) {
                agedReceivableDto.getNetAmountByPeriod().add((BigDecimal) agedReceivable[startingSumIndex]);
                agedReceivableDto.getTotalAmountByPeriod().add((BigDecimal) agedReceivable[startingSumIndex + 1]);
                agedReceivableDto.getTaxAmountByPeriod().add((BigDecimal) agedReceivable[startingSumIndex + 2]);
                startingSumIndex += 3;
            }
            agedReceivableDto.setCustomerAccountName(agedReceivable[++startingSumIndex] == null ? null : getName((Name) agedReceivable[startingSumIndex]));
            agedReceivableDto.setCustomerAccountDescription((String) agedReceivable[++startingSumIndex]);
            agedReceivableDto.setSellerDescription((String) agedReceivable[++startingSumIndex]);
            agedReceivableDto.setSellerCode((String) agedReceivable[++startingSumIndex]);
            agedReceivableDto.setDueDate(agedReceivable[++startingSumIndex] == null ? null : ((Date) agedReceivable[startingSumIndex]));
            agedReceivableDto.setTradingCurrency((String) agedReceivable[++startingSumIndex]);
            BigDecimal generalTotal = agedReceivableDto.getTotalAmountByPeriod().stream().reduce(ZERO, BigDecimal::add);
            agedReceivableDto.setGeneralTotal(generalTotal);
            agedReceivableDto.setInvoiceId((Long) agedReceivable[++startingSumIndex]);
            agedReceivableDto.setInvoiceNumber((String) agedReceivable[++startingSumIndex]);
            agedReceivableDto.setBilledAmount((BigDecimal) agedReceivable[++startingSumIndex]);
            agedReceivableDto.setCustomerAccountCode((String) agedReceivable[++startingSumIndex]);
            if (agedReceivable[++startingSumIndex] != null)
                agedReceivableDto.setBilledAmount((BigDecimal) agedReceivable[startingSumIndex]);
            agedReceivableDto.setCustomerId((Long) agedReceivable[++startingSumIndex]);
            responseDto.add(agedReceivableDto);
        }
        return responseDto;
    }

    private String getName(Name name) {
        return (name.getFirstName() != null ? name.getFirstName() : "") + (name.getLastName() != null ? " " + name.getLastName() : "");
    }
}