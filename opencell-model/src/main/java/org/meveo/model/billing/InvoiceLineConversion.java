package org.meveo.model.billing;

import static javax.persistence.FetchType.LAZY;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ObservableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@ObservableEntity
@Table(name = "billing_invoice_line_conversion")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_invoice_line_conversion_seq")})
public class InvoiceLineConversion extends AuditableEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "invoice_line_id")
    private InvoiceLine invoiceLine;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "invoice_conversion_id")
    private InvoiceConversion invoiceConversion;

    @Column(name = "applied_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal appliedRate;

    @Column(name = "applied_rate_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date appliedRateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_currency_id")
    private TradingCurrency invoiceCurrency;

    @Column(name = "unit_price", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitPrice;

    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithTax;

    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountTax;

    @Column(name = "discount_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountAmount;

    @Column(name = "raw_amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rawAmount;

    public InvoiceLine getInvoiceLine() {
        return invoiceLine;
    }

    public void setInvoiceLine(InvoiceLine invoiceLine) {
        this.invoiceLine = invoiceLine;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public InvoiceConversion getInvoiceConversion() {
        return invoiceConversion;
    }

    public void setInvoiceConversion(InvoiceConversion invoiceConversion) {
        this.invoiceConversion = invoiceConversion;
    }

    public BigDecimal getAppliedRate() {
        return appliedRate;
    }

    public void setAppliedRate(BigDecimal appliedRate) {
        this.appliedRate = appliedRate;
    }

    public Date getAppliedRateDate() {
        return appliedRateDate;
    }

    public void setAppliedRateDate(Date appliedRateDate) {
        this.appliedRateDate = appliedRateDate;
    }

    public TradingCurrency getInvoiceCurrency() {
        return invoiceCurrency;
    }

    public void setInvoiceCurrency(TradingCurrency invoiceCurrency) {
        this.invoiceCurrency = invoiceCurrency;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getRawAmount() {
        return rawAmount;
    }

    public void setRawAmount(BigDecimal rawAmount) {
        this.rawAmount = rawAmount;
    }
}