package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import org.meveo.apiv2.billing.ImmutableTax;
import org.meveo.apiv2.billing.ImmutableTaxDetails;
import org.meveo.apiv2.billing.Tax;
import org.meveo.apiv2.billing.TaxDetails;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.service.billing.impl.InvoiceLineService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceLinesApiService implements ApiService<InvoiceLine>  {

    @Inject
    private InvoiceLineService invoiceLinesService;

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
}
