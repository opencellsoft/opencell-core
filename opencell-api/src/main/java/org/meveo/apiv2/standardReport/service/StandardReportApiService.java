package org.meveo.apiv2.standardReport.service;

import static java.util.Arrays.asList;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

public class StandardReportApiService implements ApiService<RecordedInvoice> {

    @Inject
    private RecordedInvoiceService recordedInvoiceService;
    
    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    @CurrentUser
    private MeveoUser currentUser;

    private List<String> fetchFields = asList("fields");

    private static final Pattern pattern = Pattern.compile("^[a-zA-Z]+\\((.*?)\\)");

    
    public List<Object[]> list(Long offset, Long limit, String sort, String orderBy, String  customerAccountCode, Date startDate) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(),
                limit.intValue(), null, null, fetchFields, orderBy, sort);
        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccountCode != null && customerAccount == null) {
			throw new NotFoundException("Customer account with code "+customerAccountCode+" doesn't exist");
		}
        return recordedInvoiceService.getAgedReceivables(customerAccount, startDate, paginationConfiguration);
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
}