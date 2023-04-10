package org.meveo.api.dto.cpq;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BaseEntityDto;

import java.math.BigDecimal;

public class CurrencyDetailDto extends BaseEntityDto {

    @Schema(description = "code of the media")
    private String code;
    @Schema(description = "Currency rate")
    private BigDecimal rate;

    public CurrencyDetailDto() {
    }

    public CurrencyDetailDto(String code, BigDecimal rate) {
        this.code = code;
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
