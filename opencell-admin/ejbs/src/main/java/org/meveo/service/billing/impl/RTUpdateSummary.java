package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Information to update Rated transactions with
 * 
 * @author Andrius Karpavicius
 */
public class RTUpdateSummary implements Serializable {

    private static final long serialVersionUID = 1186766070163519379L;

    /**
     * Billing run identifier
     */
    private Long billingRunId;

    /**
     * Invoice identifier
     */
    private Long invoiceId;

    /**
     * Invoice subcategory aggregate identifier
     */
    private Long invoiceSubcategoryAggregateId;

    /**
     * Tax identifier
     */
    private Long taxId;

    /**
     * Tax percent to apply
     */
    private BigDecimal taxPercent;

    /**
     * A list of RT ids that should be associated to invoice - without tax change
     */
    private List<Long> ratedTransactionIdsNoTaxChange;

    /**
     * A list of RT ids that should be associated to invoice - with tax change
     */
    private List<Long> ratedTransactionIdsTaxRecalculated;

    /**
     * Constructor
     * 
     * @param billingRunId Billing run identifier
     * @param invoiceId Invoice identifier
     * @param invoiceSubcategoryAggregateId Invoice subcategory aggregate identifier
     * @param taxId Tax identifier
     * @param taxPercent Tax percent to apply
     * @param ratedTransactionIdsNoTaxChange A list of RT ids that should be associated to invoice - without tax change
     * @param ratedTransactionIdsTaxRecalculated A list of RT ids that should be associated to invoice - with tax change
     */
    public RTUpdateSummary(Long billingRunId, Long invoiceId, Long invoiceSubcategoryAggregateId, Long taxId, BigDecimal taxPercent, List<Long> ratedTransactionIdsNoTaxChange,
            List<Long> ratedTransactionIdsTaxRecalculated) {
        super();
        this.billingRunId = billingRunId;
        this.invoiceId = invoiceId;
        this.invoiceSubcategoryAggregateId = invoiceSubcategoryAggregateId;
        this.taxId = taxId;
        this.taxPercent = taxPercent;
        this.ratedTransactionIdsNoTaxChange = ratedTransactionIdsNoTaxChange;
        this.ratedTransactionIdsTaxRecalculated = ratedTransactionIdsTaxRecalculated;
    }

    /**
     * @return Billing run identifier
     */
    public Long getBillingRunId() {
        return billingRunId;
    }

    /**
     * @param billingRunId Billing run identifier
     */
    public void setBillingRunId(Long billingRunId) {
        this.billingRunId = billingRunId;
    }

    /**
     * @return Invoice identifier
     */
    public Long getInvoiceId() {
        return invoiceId;
    }

    /**
     * @param invoiceId Invoice identifier
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * @return Invoice subcategory aggregate identifier
     */
    public Long getInvoiceSubcategoryAggregateId() {
        return invoiceSubcategoryAggregateId;
    }

    /**
     * @param invoiceSubcategoryAggregateId Invoice subcategory aggregate identifier
     */
    public void setInvoiceSubcategoryAggregateId(Long invoiceSubcategoryAggregateId) {
        this.invoiceSubcategoryAggregateId = invoiceSubcategoryAggregateId;
    }

    /**
     * @return Tax identifier
     */
    public Long getTaxId() {
        return taxId;
    }

    /**
     * @param taxId Tax identifier
     */
    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    /**
     * @return Tax percent to apply
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * @param taxPercent Tax percent to apply
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    /**
     * @return A list of RT ids that should be associated to invoice - without tax change
     */
    public List<Long> getRatedTransactionIdsNoTaxChange() {
        return ratedTransactionIdsNoTaxChange;
    }

    /**
     * @param ratedTransactionIdsNoTaxChange A list of RT ids that should be associated to invoice - without tax change
     */
    public void setRatedTransactionIdsNoTaxChange(List<Long> ratedTransactionIdsNoTaxChange) {
        this.ratedTransactionIdsNoTaxChange = ratedTransactionIdsNoTaxChange;
    }

    /**
     * @return A list of RT ids that should be associated to invoice - with tax change
     */
    public List<Long> getRatedTransactionIdsTaxRecalculated() {
        return ratedTransactionIdsTaxRecalculated;
    }

    /**
     * @param ratedTransactionIdsTaxRecalculated A list of RT ids that should be associated to invoice - with tax change
     */
    public void setRatedTransactionIdsTaxRecalculated(List<Long> ratedTransactionIdsTaxRecalculated) {
        this.ratedTransactionIdsTaxRecalculated = ratedTransactionIdsTaxRecalculated;
    }
}
