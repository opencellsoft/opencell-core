package org.meveo.api.dto.cpq;

import java.util.List;

public class OverrideChargedPricesDto {

    private List<OverrideChargedPriceDto> prices;

    public List<OverrideChargedPriceDto> getPrices() {
        return prices;
    }

    public void setPrices(List<OverrideChargedPriceDto> prices) {
        this.prices = prices;
    }
}
