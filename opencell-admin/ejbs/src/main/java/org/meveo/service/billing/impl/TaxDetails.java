package org.meveo.service.billing.impl;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.BaseEntity.NB_DECIMALS;

import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TaxDetails {

    private Long taxId;

    private String taxCode;

    private BigDecimal percent;

    private BigDecimal taxAmount;

    private BigDecimal convertedTaxAmount;

    private List<TaxDetails> subTaxes;

    public TaxDetails() {
    }

    public TaxDetails(Long taxId, String taxCode, BigDecimal percent,
                      BigDecimal taxAmount, BigDecimal convertedTaxAmount, List<TaxDetails> subTaxes) {
        this.taxId = taxId;
        this.taxCode = taxCode;
        this.percent = percent;
        this.taxAmount = taxAmount;
        this.convertedTaxAmount = convertedTaxAmount;
        this.subTaxes = subTaxes;
    }

    public TaxDetails(Long taxId, String taxCode, BigDecimal percent) {
        this.taxId = taxId;
        this.taxCode = taxCode;
        this.percent = percent;
    }

    public Long getTaxId() {
        return taxId;
    }

    public void setTaxId(Long taxId) {
        this.taxId = taxId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getConvertedTaxAmount() {
        return convertedTaxAmount;
    }

    public void setConvertedTaxAmount(BigDecimal convertedTaxAmount) {
        this.convertedTaxAmount = convertedTaxAmount;
    }

    public List<TaxDetails> getSubTaxes() {
        return subTaxes;
    }

    public void setSubTaxes(List<TaxDetails> subTaxes) {
        this.subTaxes = subTaxes;
    }

    public static TaxDetails fromTax(Tax tax, BigDecimal taxAmount, BigDecimal convertedTaxAmount) {
        TaxDetails mainTaxDetails = new TaxDetails(tax.getId(), tax.getCode(), tax.getPercent());
        if(tax.getPercent().compareTo(ZERO) == 0) {
            mainTaxDetails.setTaxAmount(ZERO);
            mainTaxDetails.setConvertedTaxAmount(ZERO);
            mainTaxDetails.setSubTaxes(tax.getSubTaxes()
                    .stream()
                    .map(subTax -> new TaxDetails(subTax.getId(), subTax.getCode(), ZERO, ZERO, ZERO, null))
                    .collect(toList()));
        } else {
            mainTaxDetails.setTaxAmount(taxAmount);
            mainTaxDetails.setConvertedTaxAmount(convertedTaxAmount);
            List<TaxDetails> subTaxesDetails = new ArrayList<>();
            for (Tax subTax : tax.getSubTaxes()) {
                TaxDetails subTaxDetails = new TaxDetails(subTax.getId(), subTax.getCode(), subTax.getPercent());
                BigDecimal percent = subTax.getPercent().divide(tax.getPercent(), NB_DECIMALS, HALF_UP);
                subTaxDetails.setTaxAmount(taxAmount.multiply(percent));
                subTaxDetails.setConvertedTaxAmount(convertedTaxAmount.multiply(percent));
                subTaxesDetails.add(subTaxDetails);
            }
            mainTaxDetails.setSubTaxes(subTaxesDetails);
        }
        return mainTaxDetails;
    }
}
