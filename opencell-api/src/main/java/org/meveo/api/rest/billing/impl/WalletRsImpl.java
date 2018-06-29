package org.meveo.api.rest.billing.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.billing.WalletApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.FindWalletOperationsDto;
import org.meveo.api.dto.billing.WalletBalanceDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.billing.WalletReservationDto;
import org.meveo.api.dto.billing.WalletTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.FindWalletOperationsResponseDto;
import org.meveo.api.dto.response.billing.GetWalletTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.WalletRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.WalletOperationService;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class WalletRsImpl extends BaseRs implements WalletRs {

    @Inject
    private WalletApi walletApi;
    
    @Inject
    private WalletOperationService walletOperationService;

    @Override
    public ActionStatus currentBalance(WalletBalanceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.getCurrentAmount(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus reservedBalance(WalletBalanceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.getReservedAmount(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus openBalance(WalletBalanceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.getOpenAmount(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createReservation(WalletReservationDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.createReservation(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateReservation(WalletReservationDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.updateReservation(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus cancelReservation(Long reservationId) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.cancelReservation(reservationId);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus confirmReservation(WalletReservationDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            result.setMessage("" + walletApi.confirmReservation(postData));
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOperation(WalletOperationDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            walletApi.createOperation(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public FindWalletOperationsResponseDto findOperations(FindWalletOperationsDto postData, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(postData, new PagingAndFiltering(null, null, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FindWalletOperationsResponseDto listOperationsGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public FindWalletOperationsResponseDto listOperationsPost(PagingAndFiltering pagingAndFiltering) {
        FindWalletOperationsResponseDto result = new FindWalletOperationsResponseDto();

        try {
            result = walletApi.findOperations(null, pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createWalletTemplate(WalletTemplateDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateWalletTemplate(WalletTemplateDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetWalletTemplateResponseDto findWalletTemplate(String walletTemplateCode) {
        GetWalletTemplateResponseDto result = new GetWalletTemplateResponseDto();

        try {
            result.setWalletTemplate(walletApi.find(walletTemplateCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus removeWalletTemplate(String walletTemplateCode) {
        ActionStatus result = new ActionStatus();

        try {
            walletApi.remove(walletTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateWalletTemplate(WalletTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            walletApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public String walletOperations(String AnumberParam, String startDateParam, String endDateParam, String aggregated) {
        
        if (AnumberParam == null) {
            return "Parameter Anumber is required";
        }
        
        String aNumber = AnumberParam;
        Date startDate, endDate;
        try {
            startDate = parseDate(startDateParam);
            endDate = parseDate(endDateParam);
        } catch (BusinessException e) {
            return "Date parsing problem";
        }
       
        boolean isAggregated = "true".equals(aggregated);

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("parameter7IN", aNumber);
        CsvBuilder csvBuilder = new CsvBuilder(";", false);
        if (isAggregated) {
            String query = "select wo.edr.parameter7,wo.chargeInstance.chargeTemplate.invoiceSubCategory.code, sum(wo.inputQuantity),sum(wo.amountWithoutTax),sum(wo.amountWithTax) from WalletOperation wo "
                    + "where wo.edr.parameter7 =:parameter7IN  ";
            if (startDate != null) {
                queryParams.put("startDate", startDate);
                query += "and wo.operationDate >=:startDate ";
            }
            if (endDate != null) {
                queryParams.put("endDate", endDate);
                query += "and wo.operationDate <:endDate ";
            }
            query += "group by wo.edr.parameter7,wo.chargeInstance.chargeTemplate.invoiceSubCategory.code";
            List<Object[]> results = (List<Object[]>) walletOperationService.executeSelectQuery(query, queryParams);
            String[] header = { "A Number", "Call category", "CDR quantity", "Total amount without taxes", "Total amount with taxes" };
            csvBuilder.appendValues(header).startNewLine();
            for (Object[] row : results) {
                csvBuilder.appendValue((String) row[0]);
                csvBuilder.appendValue((String) row[1]);
                csvBuilder.appendValue(round((BigDecimal) row[2]));
                csvBuilder.appendValue(round((BigDecimal) row[3]));
                csvBuilder.appendValue(round((BigDecimal) row[4]));
                csvBuilder.startNewLine();
            }
            
            return csvBuilder.toString();
        } else {
            String query = "select wo from WalletOperation wo where wo.edr.parameter7 =:parameter7IN  ";
            if (startDate != null) {
                queryParams.put("startDate", startDate);
                query += "and wo.operationDate >=:startDate ";
            }
            if (endDate != null) {
                queryParams.put("endDate", endDate);
                query += "and wo.operationDate <:endDate ";
            }
            List<WalletOperation> results = (List<WalletOperation>) walletOperationService.executeSelectQuery(query, queryParams);
            String[] header = { "A Number", "B Number", "Country code", "Start date", "Start time", "Billing volume/quantity", "CDR quantity", "Amount without taxes",
                    "Amount with taxes", "Call category" };
            csvBuilder.appendValues(header).startNewLine();
            for (WalletOperation wo : results) {
                csvBuilder.appendValue(wo.getEdr().getParameter7());
                csvBuilder.appendValue(wo.getEdr().getExtraParameter());
                csvBuilder.appendValue(wo.getEdr().getParameter8());
                csvBuilder.appendValue(DateUtils.formatDateWithPattern(wo.getOperationDate(), "yyyy-MM-dd"));
                csvBuilder.appendValue(DateUtils.formatDateWithPattern(wo.getOperationDate(), "HH:mm:ss"));
                csvBuilder.appendValue(round(wo.getInputQuantity()));
                csvBuilder.appendValue(round(wo.getInputQuantity()));
                csvBuilder.appendValue(round(wo.getAmountWithoutTax()));
                csvBuilder.appendValue(round(wo.getAmountWithTax()));
                csvBuilder.appendValue(wo.getChargeInstance().getChargeTemplate().getInvoiceSubCategory().getCode());
                csvBuilder.startNewLine();
            }
            
            return csvBuilder.toString();
        }
    }
    
    private Date parseDate(String dateString) throws BusinessException {
        Date date = null;
        if (dateString != null) {
            date = DateUtils.guessDate(dateString, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd");
            if (date == null || date.getTime() == 1) {
                throw new BusinessException("Invalid date format, please use yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd");
            }
        }
        return date;
    }

    private String round(BigDecimal amount) {
        amount = amount.setScale(2);
        return amount.toPlainString();
    }
}
