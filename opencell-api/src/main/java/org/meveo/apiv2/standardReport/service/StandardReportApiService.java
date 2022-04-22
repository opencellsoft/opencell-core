package org.meveo.apiv2.standardReport.service;

import static java.util.Arrays.asList;
import static org.meveo.api.MeveoApiErrorCodeEnum.INVALID_PARAMETER;
import static org.meveo.api.MeveoApiErrorCodeEnum.MISSING_PARAMETER;
import static org.meveo.api.dto.ActionStatusEnum.FAIL;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.*;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.*;
import org.meveo.api.exception.*;
import org.meveo.api.rest.exception.NotAuthorizedException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.*;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

public class StandardReportApiService implements ApiService<RecordedInvoice> {

    @Inject
    private RecordedInvoiceService recordedInvoiceService;
    
    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
	private InvoiceService invoiceService;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    private List<String> fetchFields = asList("fields");

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+\\((.*?)\\)");

    public List<Object[]> list(Long offset, Long limit, String sort, String orderBy, String customerAccountCode,
							   Date startDate, String customerAccountDescription, String invoiceNumber,
							   Integer stepInDays, Integer numberOfPeriods) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(),
                limit.intValue(), null, null, fetchFields, orderBy, sort);
        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccountCode != null && customerAccount == null) {
			throw new NotFoundException("Customer account with code " + customerAccountCode + " doesn't exist");
		}
        if(invoiceNumber != null && invoiceService.findByInvoiceNumber(invoiceNumber) == null) {
			throw new NotAuthorizedException(new ActionStatus(FAIL, INVALID_PARAMETER,
					"Invoice number : " + invoiceNumber + " does not exits"));
		}
		if(numberOfPeriods != null && stepInDays == null) {
			throw new NotAuthorizedException(new ActionStatus(FAIL, MISSING_PARAMETER,
					"StepInDays parameter is mandatory when numberOfPeriods is provided"));
		}
		if(stepInDays != null && numberOfPeriods == null) {
			throw new NotAuthorizedException(new ActionStatus(FAIL, MISSING_PARAMETER,
					"numberOfPeriods parameter is mandatory when stepInDays is provided"));
		}
		try {
			return recordedInvoiceService.getAgedReceivables(customerAccount, startDate, paginationConfiguration, stepInDays, numberOfPeriods);
		} catch (Exception exception) {
			throw new BusinessApiException("Error occurred when listing aged balance report");
		}
    }

	@Override
	public List<RecordedInvoice> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, fetchFields, null, null);
        return recordedInvoiceService.count(paginationConfiguration);
    }

	@Override
	public Optional<RecordedInvoice> findById(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public RecordedInvoice create(RecordedInvoice baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<RecordedInvoice> update(Long id, RecordedInvoice baseEntity) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<RecordedInvoice> patch(Long id, RecordedInvoice baseEntity) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<RecordedInvoice> delete(Long id) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<RecordedInvoice> findByCode(String code) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	/**
	 * @param customerAccountCode
	 * @return Count of aged receivables
	 */
	public Long getCountAgedReceivables(String customerAccountCode) {
		CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccountCode != null && customerAccount == null) {
			throw new NotFoundException("Customer account with code "+customerAccountCode+" doesn't exist");
		}
        return recordedInvoiceService.getCountAgedReceivables(customerAccount);
	}
}