package org.meveo.api.dto.cpq;

import java.util.List;

public class OverridePricesDto {

    private List<OverridePriceDto> prices;

    public List<OverridePriceDto> getPrices() {
        return prices;
    }

    public void setPrices(List<OverridePriceDto> prices) {
        this.prices = prices;
    }
}
