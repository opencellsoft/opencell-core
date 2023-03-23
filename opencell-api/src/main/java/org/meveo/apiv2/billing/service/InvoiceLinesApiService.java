package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.ImmutableTax;
import org.meveo.apiv2.billing.ImmutableTaxDetails;
import org.meveo.apiv2.billing.InvoiceLinesToMarkAdjustment;
import org.meveo.apiv2.billing.Tax;
import org.meveo.apiv2.billing.TaxDetails;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.generic.services.SearchResult;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.AdjustmentStatusEnum;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.service.billing.impl.InvoiceLineService;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

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
                        invoiceLine.getAmountTax(), invoiceLine.getTransactionalAmountTax());
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
    

	public int markInvoiceLinesForAdjustment(Boolean IgnoreInvalidStatuses, List<Long> invoiceLinesIds) {
		
		List<InvoiceLine> sdInvoiceLines = invoiceLinesService.findByIdsAndInvoiceType(invoiceLinesIds, "SECURITY_DEPOSIT");
		if(!CollectionUtils.isEmpty(sdInvoiceLines)) {
			StringBuilder ids = new StringBuilder();
		    sdInvoiceLines.stream().forEach( il -> ids.append(il.getId()+"  "));
			throw new BusinessException("Security deposit invoices can not be marked for mass adjustment. The id(s) of invoice line(s) concerned:  "+ ids);
		}
				
		List<InvoiceLine> invoiceLinesIdsToMark = invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesIds, AdjustmentStatusEnum.NOT_ADJUSTED);
		if ((IgnoreInvalidStatuses == null || !IgnoreInvalidStatuses) && invoiceLinesIdsToMark.size() != invoiceLinesIds.size()) {
			 throw new BusinessException("Only NOT_ADJUSTED invoice lines can be marked TO_ADJUST");
		}
		List<Long> idList = invoiceLinesIdsToMark
	            .stream()
	            .map(InvoiceLine::getId)
	            .collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(idList)) {
			invoiceLinesService.updateForAdjustment(idList, AdjustmentStatusEnum.TO_ADJUST);
		}		
		return invoiceLinesIdsToMark.size();  	
	}
	
	public int unmarkInvoiceLinesForAdjustment(Boolean IgnoreInvalidStatuses, List<Long> invoiceLinesIds) {
		
		List<InvoiceLine> sdInvoiceLines = invoiceLinesService.findByIdsAndInvoiceType(invoiceLinesIds, "SECURITY_DEPOSIT");
		if(!CollectionUtils.isEmpty(sdInvoiceLines)) { 
			StringBuilder ids = new StringBuilder();
		    sdInvoiceLines.stream().forEach( il -> ids.append(il.getId()+"  "));
			throw new BusinessException("Security deposit invoices can not be marked for mass adjustment. The id(s) of invoice line(s) concerned: "+ ids);
		}
		List<InvoiceLine> invoiceLinesIdsToUnmark = invoiceLinesService.findByIdsAndAdjustmentStatus(invoiceLinesIds, AdjustmentStatusEnum.TO_ADJUST);
    		if ((IgnoreInvalidStatuses == null || !IgnoreInvalidStatuses) && invoiceLinesIdsToUnmark.size() != invoiceLinesIds.size()) {
    			 throw new BusinessException("Only TO_ADJUST invoice lines can be marked NOT_ADJUSTED");
			}
    		List<Long> idList = invoiceLinesIdsToUnmark
    	            .stream()
    	            .map(InvoiceLine::getId)
    	            .collect(Collectors.toList());
    		if(!CollectionUtils.isEmpty(idList)) {
    			invoiceLinesService.updateForAdjustment(idList, AdjustmentStatusEnum.NOT_ADJUSTED);
    		}
    		return invoiceLinesIdsToUnmark.size();    	
	}
	

	public List<Long> getInvoiceLineIds(InvoiceLinesToMarkAdjustment invoiceLinesToMark) {
		if(invoiceLinesToMark == null || invoiceLinesToMark.getFilters() == null || invoiceLinesToMark.getFilters().isEmpty()) {
			return new ArrayList<>();
		}
		if(invoiceLinesToMark.getFilters().size() == 1 && invoiceLinesToMark.getFilters().containsKey("inList id")) {
			List<Long> ids = ((List<Integer>) invoiceLinesToMark.getFilters().get("inList id")).stream()
			        .mapToLong(Integer::longValue)
			        .boxed().collect(Collectors.toList());
			return ids;
		}
		List<InvoiceLine> invoiceLines = getInvoiceLinesForAdjustment(invoiceLinesToMark);
    	return emptyIfNull(invoiceLines).stream().map(invoiceLine -> invoiceLine.getId()).collect(Collectors.toList());
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
