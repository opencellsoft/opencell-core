package org.meveo.apiv2.standardReport.service;

import static java.util.Arrays.asList;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.*;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.billing.impl.*;
import org.meveo.service.payments.impl.RecordedInvoiceService;

public class StandardReportApiService implements ApiService<RecordedInvoice> {

	@Inject
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
	private InvoiceService invoiceService;

    private List<String> fetchFields = asList("fields");

    public List<Object[]> list(Long offset, Long limit, String sort, String orderBy, String customerAccountCode,
							   Date startDate, Date startDueDate, Date endDueDate, String customerAccountDescription,
							   String sellerDescription, String sellerCode, String invoiceNumber, Integer stepInDays,
							   Integer numberOfPeriods, String tradingCurrency, String functionalCurrency) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset != null
				? offset.intValue() : null, limit.intValue() , null, null, fetchFields, orderBy, sort);
        if(invoiceNumber != null && invoiceService.findByInvoiceNumber(invoiceNumber) == null) {
			throw new NotFoundException("Invoice number : " + invoiceNumber + " does not exits");
		}
		if(numberOfPeriods != null && stepInDays == null) {
			throw new BadRequestException("StepInDays parameter is mandatory when numberOfPeriods is provided");
		}
		if(stepInDays != null && numberOfPeriods == null) {
			throw new BadRequestException("numberOfPeriods parameter is mandatory when stepInDays is provided");
		}

		if (startDueDate != null && endDueDate != null && startDueDate.after(endDueDate)) {
			throw new BadRequestException("End due date must be after start due date");
		}

		if (startDueDate != null && endDueDate == null) {
			endDueDate = startDueDate;
		}

		try {
			return recordedInvoiceService.getAgedReceivables(customerAccountCode, sellerCode, startDate, startDueDate,
					endDueDate, paginationConfiguration, stepInDays, numberOfPeriods, invoiceNumber,
					customerAccountDescription, sellerDescription, tradingCurrency, functionalCurrency);
		} catch (Exception exception) {
			throw new BusinessApiException("Error occurred when listing aged balance report : " + exception.getMessage());
		}
    }

	/**
	 * Get all aged receivables
	 * @return List of object
	 */
	public List<Object[]> getAll(Date startDate) {
		try {
			return recordedInvoiceService.getAgedReceivables(null, null, startDate, null, null,
					null, null, null, null, null, null, null, null);
		} catch (Exception exception) {
			throw new BusinessApiException("Error occurred when listing aged balance report : " + exception.getMessage());

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
	public Long getCountAgedReceivables(String customerAccountCode, String customerAccountDescription, String sellerCode, String sellerDescription, String invoiceNumber, String tradingCurrency, 
			Date startDueDate, Date endDueDate, Date startDate) {
		if(invoiceNumber != null && invoiceService.findByInvoiceNumber(invoiceNumber) == null) {
			throw new NotFoundException("Invoice number : " + invoiceNumber + " does not exits");
		}

		if (startDueDate != null && endDueDate != null && startDueDate.after(endDueDate)) {
			throw new BadRequestException("End due date must be after start due date");
		}

		if (startDueDate != null && endDueDate == null) {
			endDueDate = startDueDate;
		}
		
        return recordedInvoiceService.getCountAgedReceivables(customerAccountCode, customerAccountDescription, sellerCode, sellerDescription, invoiceNumber, tradingCurrency, startDueDate, endDueDate, startDate);
	}
}
