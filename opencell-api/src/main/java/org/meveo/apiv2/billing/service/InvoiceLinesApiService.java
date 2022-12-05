package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.ImmutableTax;
import org.meveo.apiv2.billing.ImmutableTaxDetails;
import org.meveo.apiv2.billing.InvoiceLinesToMarkAdjustment;
import org.meveo.apiv2.billing.Tax;
import org.meveo.apiv2.billing.TaxDetails;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.generic.services.SearchResult;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.AdjustmentStatusEnum;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.service.billing.impl.InvoiceLineService;

public class InvoiceLinesApiService implements ApiService<InvoiceLine>  {

    @Inject
    private InvoiceLineService invoiceLinesService;
    
    @Inject
    private GenericApiPersistenceDelegate persistenceDelegate;

    @Override
    public List<InvoiceLine> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<InvoiceLine> findById(Long id) {
        return ofNullable(invoiceLinesService.findById(id));
    }

    @Override
    public InvoiceLine create(InvoiceLine invoiceLine) {
        return null;
    }

    @Override
    public Optional<InvoiceLine> update(Long id, InvoiceLine invoiceLine) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> patch(Long id, InvoiceLine invoiceLine) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceLine> findByCode(String code) {
        return Optional.empty();
    }

    /**
     * Get invoice line tax details
     * @param invoiceLineId Invoice line identifier
     * @return TaxDetails
     */
    public TaxDetails getTaxDetails(Long invoiceLineId) {
        org.meveo.model.billing.InvoiceLine invoiceLine =
                ofNullable(invoiceLinesService.findById(invoiceLineId, asList("tax")))
                        .orElseThrow(() -> new NotFoundException("Invoice line not found"));
        Optional<org.meveo.service.billing.impl.TaxDetails> taxDetails =
                invoiceLinesService.getTaxDetails(invoiceLine.getTax(),
                        invoiceLine.getAmountTax(), invoiceLine.getConvertedAmountTax());
        if(taxDetails.isEmpty()) {
            throw new NotFoundException("No tax details found for invoice line id : " + invoiceLineId);
        }
        return to(taxDetails.get());
    }

    private TaxDetails to(org.meveo.service.billing.impl.TaxDetails taxDetails) {
        Tax tax = ImmutableTax.builder()
                .id(taxDetails.getTaxId())
                .code(taxDetails.getTaxCode())
                .composite(taxDetails.getComposite())
                .percent(taxDetails.getPercent())
                .build();
        List<TaxDetails> subTaxesDetails = null;
        if(taxDetails.getSubTaxes() != null && !taxDetails.getSubTaxes().isEmpty()) {
            subTaxesDetails = new ArrayList<>();
            for (org.meveo.service.billing.impl.TaxDetails subTaxDetail : taxDetails.getSubTaxes()) {
                Tax subTax = ImmutableTax.builder()
                        .id(subTaxDetail.getTaxId())
                        .code(subTaxDetail.getTaxCode())
                        .percent(subTaxDetail.getPercent())
                        .build();
                subTaxesDetails.add(ImmutableTaxDetails.builder()
                        .tax(subTax)
                        .taxAmount(subTaxDetail.getTaxAmount())
                        .convertedTaxAmount(subTaxDetail.getConvertedTaxAmount())
                        .build()
                );
            }
        }
        return ImmutableTaxDetails.builder()
                .tax(tax)
                .taxAmount(taxDetails.getTaxAmount())
                .convertedTaxAmount(taxDetails.getConvertedTaxAmount())
                .subTaxes(subTaxesDetails)
                .build();
    }
    

	public int markInvoiceLinesForAdjustment(InvoiceLinesToMarkAdjustment invoiceLinesToMark, List<Long> invoiceLinesIds) {
		
		if(invoiceLinesToMark.getIgnoreInvalidStatuses() == null || !invoiceLinesToMark.getIgnoreInvalidStatuses()) {
    		List<InvoiceLine> invoiceLines = invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesIds, AdjustmentStatusEnum.NOT_ADJUSTED);
    		if (invoiceLines != null && invoiceLines.size() != invoiceLinesIds.size()) {
    			 throw new BusinessException("Only NOT_ADJUSTED invoice lines can be marked TO_ADJUST");
			}
    		invoiceLines.stream().forEach(invoiceLine -> {
    										if(invoiceLine.getInvoice().getInvoiceType().getCode().equalsIgnoreCase("SECURITY_DEPOSIT")) {
    											throw new BusinessException("Security deposit invoices can not be marked for mass adjustment.");
    										}
    										invoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.TO_ADJUST);
											invoiceLinesService.update(invoiceLine);}
    									);
    		return invoiceLines.size();
    	}else {
    		List<InvoiceLine> invoiceLines = invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesIds, AdjustmentStatusEnum.NOT_ADJUSTED);
    		invoiceLines.stream().forEach(invoiceLine -> {
											    			if(invoiceLine.getInvoice().getInvoiceType().getCode().equalsIgnoreCase("SECURITY_DEPOSIT")) {
																throw new BusinessException("Security deposit invoices can not be marked for mass adjustment.");
															}
											    			invoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.TO_ADJUST);
    														invoiceLinesService.update(invoiceLine);}
    									);
    		return invoiceLines == null ? 0 : invoiceLines.size();
    	}
	}
	
	public int unmarkInvoiceLinesForAdjustment(InvoiceLinesToMarkAdjustment invoiceLinesToUnmark, List<Long> invoiceLinesIds) {
		
		
		if(invoiceLinesToUnmark.getIgnoreInvalidStatuses() == null || !invoiceLinesToUnmark.getIgnoreInvalidStatuses()) {			
    		List<InvoiceLine> invoiceLines = invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesIds, AdjustmentStatusEnum.TO_ADJUST);
    		if (invoiceLines != null && invoiceLines.size() != invoiceLinesIds.size()) {
    			 throw new BusinessException("Only TO_ADJUST invoice lines can be marked NOT_ADJUSTED");
			}
    		invoiceLines.stream().forEach(invoiceLine -> {invoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.NOT_ADJUSTED);
											invoiceLinesService.update(invoiceLine);}
    									);
    		return invoiceLines.size();
    	}else {
    		List<InvoiceLine> invoiceLines = invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesIds, AdjustmentStatusEnum.TO_ADJUST);
    		invoiceLines.stream().forEach(invoiceLine -> {invoiceLine.setAdjustmentStatus(AdjustmentStatusEnum.NOT_ADJUSTED);
    														invoiceLinesService.update(invoiceLine);}
    									);
    		return invoiceLines == null ? 0 : invoiceLines.size();
    	}
	}
	

	public List<Long> getInvoiceLineIds(InvoiceLinesToMarkAdjustment invoiceLinesToUnmark) {
		List<InvoiceLine> invoiceLines = getInvoiceLinesForAdjustment(invoiceLinesToUnmark);
    	return invoiceLines.stream().map(invoiceLine -> invoiceLine.getId()).collect(Collectors.toList());
	}

	private List<InvoiceLine> getInvoiceLinesForAdjustment(InvoiceLinesToMarkAdjustment invoiceLinesToUnmark) {
		Class entityClass = GenericHelper.getEntityClass("invoiceLine");
    	GenericRequestMapper genericRequestMapper = new GenericRequestMapper(entityClass, PersistenceServiceHelper.getPersistenceService());
    	Map evaluateFilters = genericRequestMapper.evaluateFilters(invoiceLinesToUnmark.getFilters(), entityClass);
    	PaginationConfiguration searchConfig = new PaginationConfiguration(evaluateFilters);
    	Set<String> genericFields = Set.of("id"); 
    	SearchResult searchResult = persistenceDelegate.list(entityClass, searchConfig);
    	List<InvoiceLine> invoiceLines = (List<InvoiceLine>) searchResult.getEntityList();
		return invoiceLines;
	}
}
