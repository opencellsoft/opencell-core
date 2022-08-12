package org.meveo.service.billing.impl;

import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.toList;

import org.meveo.model.billing.Tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TaxDetails {

    private Long taxId;

    private String taxCode;

    private BigDecimal percent;

    private BigDecimal taxAmount;

    private BigDecimal convertedTaxAmount;

    private List<TaxDetails> subTaxes;

    private Boolean composite;

    public TaxDetails() {
    }

    public TaxDetails(Long taxId, String taxCode, BigDecimal percent,
                      BigDecimal taxAmount, BigDecimal convertedTaxAmount, List<TaxDetails> subTaxes, Boolean composite) {
        this.taxId = taxId;
        this.taxCode = taxCode;
        this.percent = percent;
        this.taxAmount = taxAmount;
        this.convertedTaxAmount = convertedTaxAmount;
        this.subTaxes = subTaxes;
        this.composite = composite;
    }

    public TaxDetails(Long taxId, String taxCode, BigDecimal percent, Boolean composite) {
        this.taxId = taxId;
        this.taxCode = taxCode;
        this.percent = percent;
        this.composite = composite;
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
        TaxDetails mainTaxDetails =
                new TaxDetails(tax.getId(), tax.getCode(), tax.getPercent().setScale(4, HALF_UP), TRUE);
        if(tax.getPercent().compareTo(ZERO) == 0) {
            mainTaxDetails.setTaxAmount(ZERO);
            mainTaxDetails.setConvertedTaxAmount(ZERO);
            mainTaxDetails.setSubTaxes(tax.getSubTaxes()
                    .stream()
                    .map(subTax -> new TaxDetails(subTax.getId(),
                            subTax.getCode(), ZERO, ZERO, ZERO, null, null))
                    .collect(toList()));
        } else {
            mainTaxDetails.setTaxAmount(taxAmount.setScale(4, HALF_UP));
            mainTaxDetails.setConvertedTaxAmount(convertedTaxAmount.setScale(4, HALF_UP));
            List<TaxDetails> subTaxesDetails = new ArrayList<>();
            for (Tax subTax : tax.getSubTaxes()) {
                TaxDetails subTaxDetails =
                        new TaxDetails(subTax.getId(), subTax.getCode(), subTax.getPercent().setScale(4, HALF_UP), null);
                BigDecimal percent = subTax.getPercent().divide(tax.getPercent(), 4, FLOOR);
                subTaxDetails.setTaxAmount(taxAmount.multiply(percent).setScale(2, HALF_UP));
                subTaxDetails.setConvertedTaxAmount(convertedTaxAmount.multiply(percent).setScale(2, HALF_UP));
                subTaxesDetails.add(subTaxDetails);
            }
            mainTaxDetails.setSubTaxes(subTaxesDetails);
        }
        return mainTaxDetails;
    }

    public Boolean getComposite() {
        return composite;
    }

    public void setComposite(Boolean composite) {
        this.composite = composite;
    }
}
