package org.meveo.api.dto.cpq;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BusinessEntityDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class TaxDTO extends BusinessEntityDto {

    @Schema(description = "tax percent")
    private String percent;
    @Schema(description = "tax amount")
    private BigDecimal tax;
    @Schema(description = "tax index (for Jasper export only)")
    private String index;
    @Schema
    private BigDecimal amountWithoutTax = BigDecimal.ZERO;
    @Schema
    private BigDecimal amountWithTax = BigDecimal.ZERO;
    @Schema
    private BigDecimal amountTax = BigDecimal.ZERO;
    @Schema(description = "tax exoneration")
    private String vatex;

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public String getVatex() {
        return vatex;
    }

    public void setVatex(String vatex) {
        this.vatex = vatex;
    }
}
