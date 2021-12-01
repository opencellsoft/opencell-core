/**
 * 
 */
package org.meveo.apiv2.billing.service;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.billing.*;
import org.meveo.apiv2.billing.impl.InvoiceMapper;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.Invoice;
import org.meveo.model.filter.Filter;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.filter.FilterService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

public class InvoiceApiService  implements ApiService<Invoice> {
	
	public static final String ADV = "ADV";
	
    @Inject
    private InvoiceService invoiceService;

	@Inject
	private InvoiceLineService invoiceLinesService;

	@Inject
	private FilterService filterService;

	@Inject
	@CurrentUser
	protected MeveoUser currentUser;

	@Inject
	private InvoiceBaseApi invoiceBaseApi;

	@Inject
	protected ResourceBundle resourceMessages;

	
	@Override
	public List<Invoice> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, null, null, null);
        return invoiceService.listWithlinkedInvoices(paginationConfiguration);
	}
	
	@Override
	public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, null, null, null);
		return invoiceService.count(paginationConfiguration);
	}

	@Override
	public Optional<Invoice> findById(Long id) {
		return ofNullable(invoiceService.findById(id));
	}

	@Override
	public Invoice create(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> update(Long id, Invoice baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> patch(Long id, Invoice baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> delete(Long id) {
        Invoice invoice = invoiceService.findById(id);
        if(invoice != null) {
            invoiceService.remove(invoice);
            return Optional.of(invoice);
        }
        return Optional.empty();
	}

	@Override
	public Optional<Invoice> findByCode(String code) {
		throw new BadRequestException("Use invoice number and type");
	}

	/**
	 * @param invoiceTypeId
	 * @param invoiceNumber
	 * @return
	 */
	public Optional<Invoice> findByInvoiceNumberAndTypeId(Long invoiceTypeId, String invoiceNumber) {
		return ofNullable(invoiceService.findByInvoiceTypeAndInvoiceNumber(invoiceNumber, invoiceTypeId));
	}

	/**
	 * @param invoice
	 * @param generateIfMissing
	 * @return
	 */
	public Optional<byte[]> fetchPdfInvoice(Invoice invoice, boolean generateIfMissing) {
		return ofNullable(invoiceService.getInvoicePdf(invoice, generateIfMissing));
		
	}

    /**
     * @param invoice
     * @return
     */
    public void deleteInvoicePdf(Invoice invoice) {
        if (invoiceService.isInvoicePdfExist(invoice)) {
            try {
                invoiceService.deleteInvoicePdf(invoice);
            } catch (Exception exception) {
                throw new InternalServerErrorException(exception);
            }


        } else {
            throw new NotFoundException("pdf invoice does not exists : ");
        }


    }

    /**
     * @param invoice
     * @return
     */
    public void deleteInvoiceXml(Invoice invoice) {
        if (invoiceService.isInvoiceXmlExist(invoice)) {
            try {
                invoiceService.deleteInvoiceXml(invoice);
            } catch (Exception exception) {
                throw new InternalServerErrorException(exception);
            }


        } else {
            throw new NotFoundException("xml invoice does not exists : ");
        }


    }


    /**
	 * @param basicInvoice
	 * @return
	 */
	public Invoice create(BasicInvoice basicInvoice) {
		return invoiceService.createBasicInvoice(basicInvoice);
	}
	
	/**
	 * @param typeCode
	 * @param invoiceNumber
	 * @return
	 */
	public Optional<Invoice> findByInvoiceNumberAndTypeCode(String typeCode, String invoiceNumber) {
		return ofNullable(invoiceService.findByInvoiceNumberAndTypeCode(invoiceNumber, typeCode));
	}

	/**
	 * @param invoice
	 * @param invoiceLinesInput InvoiceLinesInput
	 */
	public InvoiceLinesInput createLines(Invoice invoice, InvoiceLinesInput invoiceLinesInput) {
		ImmutableInvoiceLinesInput.Builder result = ImmutableInvoiceLinesInput.builder();
		for(InvoiceLine invoiceLineRessource: invoiceLinesInput.getInvoiceLines()) {
			org.meveo.model.cpq.commercial.InvoiceLine invoiceLine  = invoiceLinesService.create(invoice, invoiceLineRessource);
			invoiceLineRessource =  ImmutableInvoiceLine.copyOf(invoiceLineRessource).withId(invoiceLine.getId());
			result.addInvoiceLines(invoiceLineRessource);
		}
		result.skipValidation(invoiceLinesInput.getSkipValidation());
		return result.build();
	}
	

	/**
	 * @param invoice
	 */
	public void rebuildInvoice(Invoice invoice) {
		invoiceService.rebuildInvoice(invoice, true);
	}

	/**
	 * @param invoice
	 * @param invoiceLineInput
	 * @param lineId 
	 */
	public void updateLine(Invoice invoice, InvoiceLineInput invoiceLineInput, Long lineId) {
		invoiceLinesService.update(invoice, invoiceLineInput.getInvoiceLine(), lineId);
	}

	/**
	 * @param invoice
	 * @param lineId
	 */
	public void removeLine(Invoice invoice, Long lineId) {
		invoiceLinesService.remove(invoice, lineId);
	}

	/**
	 * @param invoice
	 */
	public void rejectInvoice(Invoice invoice) {
		invoiceService.rejectInvoice(invoice);
	}

	/**
	 * @param invoice
	 */
	public void validateInvoice(Invoice invoice) {
		invoiceService.validateInvoice(invoice, true);
	}

	/**
	 * @param invoice
	 */
	public void cancelInvoice(Invoice invoice) {
		invoiceService.cancelInvoice(invoice);
	}

	/**
	 * @param input InvoiceInput
	 * @return
	 */
	public Invoice create(org.meveo.apiv2.billing.InvoiceInput input) {
		return invoiceService.createInvoiceV11(input.getInvoice(), input.getSkipValidation(), input.getIsDraft(), input.getIsVirtual(), input.getIsIncludeBalance(), input.getIsAutoValidation());
	}
	
	public Invoice update(Invoice invoice, Invoice input, org.meveo.apiv2.billing.Invoice invoiceResource) {
		return invoiceService.update(invoice, input, invoiceResource);
	}

	/**
	 * @param invoice
	 */
	public void calculateInvoice(Invoice invoice) {
		invoiceService.calculateInvoice(invoice);
	}
	
	public Invoice duplicate(Invoice invoice) {
		return invoiceService.duplicate(invoice);
	}


	/**
	 * Generate Invoice
	 * @param invoice Invoice input
	 * @param isDraft
	 */
    public Optional<List<GenerateInvoiceResult>> generate(GenerateInvoiceRequestDto invoice, boolean isDraft) {
		IBillableEntity entity = invoiceService.getBillableEntity(invoice.getTargetCode(), invoice.getTargetType(),
				invoice.getOrderNumber(), invoice.getBillingAccountCode());
    	Filter ratedTransactionFilter = null;
		if(invoice.getFilter() != null) {
			ratedTransactionFilter = getFilterFromInput(invoice.getFilter());
			if (ratedTransactionFilter == null) {
				throw new NotFoundException("Filter does not exists");
			}
		}
		if (isDraft) {
			if (invoice.getGeneratePDF() == null) {
				invoice.setGeneratePDF(Boolean.TRUE);
			}
			if (invoice.getGenerateAO() != null) {
				invoice.setGenerateAO(Boolean.FALSE);
			}
		}
		ICustomFieldEntity customFieldEntity = new Invoice();
		customFieldEntity =
				invoiceBaseApi.populateCustomFieldsForGenericApi(invoice.getCustomFields(), customFieldEntity, false);
		List<Invoice> invoices = invoiceService.generateInvoice(entity, invoice, ratedTransactionFilter,
				isDraft, customFieldEntity.getCfValues(), true);
		if (invoices == null || invoices.isEmpty()) {
			throw new BusinessException(resourceMessages.getString("error.invoicing.noTransactions"));
		}
		List<GenerateInvoiceResult> generateInvoiceResults = new ArrayList<>();
		InvoiceMapper invoiceMapper = new InvoiceMapper();
		for (Invoice inv : invoices) {
			List<Object[]> invoiceInfo = (List<Object[]>) invoiceService.getEntityManager().createNamedQuery("Invoice.getInvoiceTypeANDRecordedInvoiceID")
					.setParameter("id", inv.getId())
					.getResultList();
			generateInvoiceResults.add(invoiceMapper.toGenerateInvoiceResult(inv, (String) invoiceInfo.get(0)[0], (Long) invoiceInfo.get(0)[1]));
		}
    	return of(generateInvoiceResults);
    }

	private Filter getFilterFromInput(FilterDto filterDto) throws MeveoApiException {
		Filter filter = null;
		if (StringUtils.isBlank(filterDto.getCode()) && StringUtils.isBlank(filterDto.getInputXml())) {
			throw new BadRequestException("code or inputXml");
		}
		if (!StringUtils.isBlank(filterDto.getCode())) {
			filter = filterService.findByCode(filterDto.getCode());
			if (filter == null && StringUtils.isBlank(filterDto.getInputXml())) {
				throw new NotFoundException("Filter with code does not exists : " + filterDto.getCode());
			}
			if (filter != null && !filter.getShared()) {
				if (!filter.getAuditable().isCreator(currentUser)) {
					throw new BadRequestException("INVALID_FILTER_OWNER");
				}
			}
		}
		return filter;
	}
}