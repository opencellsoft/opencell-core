/**
 * 
 */
package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.billing.QuarantineBillingRunDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.*;
import org.meveo.apiv2.billing.impl.InvoiceMapper;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.filter.Filter;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.filter.FilterService;

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

	@Inject
    protected RatedTransactionService ratedTransactionService;
	
	private List<String> fieldToFetch = asList("invoiceLines");

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
		return ofNullable(invoiceService.findById(id, fieldToFetch));
	}

	@Override
	public Invoice create(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Invoice> update(Long id, Invoice baseEntity) {
		// TODO Auto-generated method stub
		return empty();
	}

	@Override
	public Optional<Invoice> patch(Long id, Invoice baseEntity) {
		// TODO Auto-generated method stub
		return empty();
	}

	@Override
	public Optional<Invoice> delete(Long id) {
        Invoice invoice = invoiceService.findById(id);
        if(invoice != null) {
            invoiceService.remove(invoice);
            return of(invoice);
        }
        return empty();
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
		for(InvoiceLine invoiceLineResource : invoiceLinesInput.getInvoiceLines()) {
			org.meveo.model.billing.InvoiceLine invoiceLine = invoiceLinesService.create(invoice, invoiceLineResource);
			invoiceLineResource = ImmutableInvoiceLine.copyOf(invoiceLineResource)
					.withId(invoiceLine.getId())
					.withAmountWithoutTax(invoiceLine.getAmountWithoutTax())
					.withAmountWithTax(invoiceLine.getAmountWithTax())
					.withAmountTax(invoiceLine.getAmountTax());
			result.addInvoiceLines(invoiceLineResource);
		}
		invoiceService.calculateInvoice(invoice);
		invoiceService.updateBillingRunStatistics(invoice);
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
		invoiceService.calculateInvoice(invoice);
		invoiceService.updateBillingRunStatistics(invoice);
	}

	/**
	 * @param invoice
	 * @param lineId
	 */
	public void removeLine(Invoice invoice, Long lineId) {
		invoiceLinesService.remove(invoice, lineId);
		invoiceService.calculateInvoice(invoice);
        invoiceService.updateBillingRunStatistics(invoice);
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
		invoiceService.cancelInvoiceWithoutDelete(invoice);
	}

	/**
	 * @param input InvoiceInput
	 * @return
	 */
	public Invoice create(org.meveo.apiv2.billing.InvoiceInput input) {
		return invoiceService.createInvoiceV11(input.getInvoice(), input.getSkipValidation(), input.getIsDraft(), input.getIsVirtual(), input.getIsIncludeBalance(), input.getIsAutoValidation());
	}
	
	public Invoice update(Invoice invoice, Invoice input, org.meveo.apiv2.billing.Invoice invoiceResource) {       
        Invoice updateInvoice = invoiceService.update(invoice, input, invoiceResource);
        invoiceService.calculateInvoice(updateInvoice);
        invoiceService.updateBillingRunStatistics(updateInvoice);
        return updateInvoice;
    }

	/**
	 * @param invoice
	 */
	public void calculateInvoice(Invoice invoice) {
		invoiceService.calculateInvoice(invoice);
        invoiceService.updateBillingRunStatistics(invoice);
	}
	
	public Invoice duplicate(Invoice invoice) {
		return invoiceService.duplicate(invoice);
	}


    public Invoice duplicateInvoiceLines(Invoice invoice, List<Long> invoiceLineIds) {
        List<String> idsInvoiceLineNotFound = new ArrayList<>();
        for(Long lineId : invoiceLineIds) {
            org.meveo.model.billing.InvoiceLine invoiceLine = invoiceLinesService.findById(lineId);
            if (invoiceLine == null) {                
                idsInvoiceLineNotFound.add("" + lineId);
            }
        }

        String idsInvoiceLineNotFoundStr = "";
        if (!idsInvoiceLineNotFound.isEmpty()) {
            for(int i=0; i< idsInvoiceLineNotFound.size() - 1; i++) {
                idsInvoiceLineNotFoundStr += idsInvoiceLineNotFound.get(i) + ", ";
            }
            idsInvoiceLineNotFoundStr += idsInvoiceLineNotFound.get(idsInvoiceLineNotFound.size()-1);
            throw new MeveoApiException("Invoice Line ids does not exist: [" + idsInvoiceLineNotFoundStr + "]."); 
        }
        
        return invoiceService.duplicateInvoiceLines(invoice, invoiceLineIds);        
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
		
		if(true==invoice.isApplyBillingRules()) {
            Date firstTransactionDate = invoice.getFirstTransactionDate() == null ? new Date(0) : invoice.getFirstTransactionDate();
            Date lastTransactionDate = invoice.getLastTransactionDate() == null ? invoice.getInvoicingDate() : invoice.getLastTransactionDate();
            List<RatedTransaction> RTs = ratedTransactionService.listRTsToInvoice(entity, firstTransactionDate, lastTransactionDate, invoice.getInvoicingDate(), ratedTransactionFilter, null);
            ratedTransactionService.applyInvoicingRules(RTs);
        }
		
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
		if(!StringUtils.isBlank(filterDto.getPollingQuery()) && RatedTransaction.class.getSimpleName().equals(filterDto.getEntityClass())){
			filter = new Filter();
			filter.setPollingQuery(filterDto.getPollingQuery() + "AND status = 'OPEN'");
			filter.setEntityClass(filterDto.getEntityClass());
			return filter;
		}
		if (StringUtils.isBlank(filterDto.getCode()) && StringUtils.isBlank(filterDto.getInputXml())) {
			throw new BadRequestException("code or inputXml");
		}
		if (!StringUtils.isBlank(filterDto.getCode())) {
			filter = filterService.findByCode(filterDto.getCode());
			if (filter == null && StringUtils.isBlank(filterDto.getInputXml())) {
				throw new NotFoundException("Filter with code does not exists : " + filterDto.getCode());
			}
			if (filter != null && !filter.getShared() && !filter.getAuditable().isCreator(currentUser)) {
				throw new BadRequestException("INVALID_FILTER_OWNER");
			}
		}
		return filter;
	}
	
	public Invoice createAdjustment(Invoice invoice, InvoiceLinesToReplicate invoiceLinesToReplicate) {
	    Invoice adjInvoice = null;

	    invoice = invoiceService.findById(invoice.getId(), asList("invoiceLines", "invoiceType", "invoiceType.occTemplate", "linkedInvoices"));

	    if (invoice.getStatus() != InvoiceStatusEnum.VALIDATED) {
            throw new ForbiddenException("Invoice should be Validated");
        }
	    
	    if (invoice.getInvoiceType().getOccTemplate().getOccCategory() != OperationCategoryEnum.DEBIT) {
	        throw new ForbiddenException("occCategory must equal DEBIT as invoice type");
        }
	    
	    if (invoiceLinesToReplicate.getGlobalAdjustment() == null) {
            throw new MissingParameterException("globalAdjustment");
        }
	    
	    try {
	        adjInvoice = invoiceService.createAdjustment(invoice, invoiceLinesToReplicate.getInvoiceLinesIds());
    	    
    	    if (invoice.getLinkedInvoices() != null) {
    	        invoice.getLinkedInvoices().size();
            }
    	    else {
    	        invoice.setLinkedInvoices(new HashSet<>());
    	    }
    	    invoice.getLinkedInvoices().add(adjInvoice);
    	    invoiceService.update(invoice);
	    }
	    catch (Exception e) {
	        throw new BusinessApiException("Error when creating adjustment");
        }
	    
	    return adjInvoice;
	}

	public Long quarantineInvoice(Invoice invoice, QuarantineBillingRunDto quarantineBillingRunDto) {       
        return invoiceService.quarantineBillingRun(invoice, quarantineBillingRunDto);
    }

	/**
	 * Invoice refresh rate
	 * @param invoiceId Invoice identifier
	 * @return refresh result
	 */
	public Optional<Invoice> refreshRate(Long invoiceId) {
		Invoice invoice = ofNullable(invoiceService.findById(invoiceId, asList("tradingCurrency")))
				.orElseThrow(() -> new NotFoundException("Invoice not found"));
		if(invoice.getStatus() != InvoiceStatusEnum.NEW && invoice.getStatus() != InvoiceStatusEnum.DRAFT) {
			throw new ForbiddenException("Refresh rate only allowed for invoices with status : NEW or DRAFT");
		}
		BigDecimal currentRate = invoice.getTradingCurrency().getCurrentRate();
		if(currentRate != null && currentRate.equals(invoice.getLastAppliedRate())) {
			return empty();
		} else {
			return of(invoiceService.refreshConvertedAmounts(invoice,
					currentRate, invoice.getTradingCurrency().getCurrentRateFromDate()));
		}
	}
}