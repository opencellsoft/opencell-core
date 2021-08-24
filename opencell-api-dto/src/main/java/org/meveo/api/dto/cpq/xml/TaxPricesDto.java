package org.meveo.api.dto.cpq.xml;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.cpq.PriceDTO;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TaxPricesDto extends BaseEntityDto {

    private BigDecimal taxRate;
    private List<PriceDTO> prices;

    public TaxPricesDto(BigDecimal taxRate, List<PriceDTO> prices) {
        this.taxRate = taxRate;
        this.prices = prices;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public List<PriceDTO> getPrices() {
        return prices;
    }

    public void setPrices(List<PriceDTO> prices) {
        this.prices = prices;
    }
}
