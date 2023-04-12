package org.meveo.api.dto.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.math.BigDecimal;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TaxDetailDTO {

    private BigDecimal totalAmountWithoutTax = BigDecimal.ZERO;
    private BigDecimal totalAmountWithTax = BigDecimal.ZERO;
    private BigDecimal totalAmountTax = BigDecimal.ZERO;
    private String vatex;
    @XmlElementWrapper(name = "taxes")
    @XmlElement(name = "tax")
    private List<TaxDTO> taxes;

    public List<TaxDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<TaxDTO> taxes) {
        this.taxes = taxes;
    }

    public String getVatex() {
        return vatex;
    }

    public void setVatex(String vatex) {
        this.vatex = vatex;
    }

    public BigDecimal getTotalAmountWithoutTax() {
        return totalAmountWithoutTax;
    }

    public void setTotalAmountWithoutTax(BigDecimal totalAmountWithoutTax) {
        this.totalAmountWithoutTax = totalAmountWithoutTax;
    }

    public BigDecimal getTotalAmountWithTax() {
        return totalAmountWithTax;
    }

    public void setTotalAmountWithTax(BigDecimal totalAmountWithTax) {
        this.totalAmountWithTax = totalAmountWithTax;
    }

    public BigDecimal getTotalAmountTax() {
        return totalAmountTax;
    }

    public void setTotalAmountTax(BigDecimal totalAmountTax) {
        this.totalAmountTax = totalAmountTax;
    }
}
