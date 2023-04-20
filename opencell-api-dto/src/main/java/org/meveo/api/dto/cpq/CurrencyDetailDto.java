package org.meveo.api.dto.cpq;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.BaseEntityDto;

import java.math.BigDecimal;

public class CurrencyDetailDto extends BaseEntityDto {

    @Schema(description = "code of the currency")
    private String code;
    @Schema(description = "symbol of the currency")
    private String symbol;
    @Schema(description = "Currency rate")
    private BigDecimal rate;
    @Schema(description = "BA code of the currency")
    private String baCode;
    @Schema(description = "BA symbol of the currency")
    private String baSymbol;
    @Schema(description = "BA Currency rate")
    private BigDecimal baRate;

    public CurrencyDetailDto() {
    }

    public CurrencyDetailDto(String code, String symbol, BigDecimal rate) {
        this.code = code;
        this.symbol = symbol;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBaCode() {
        return baCode;
    }

    public void setBaCode(String baCode) {
        this.baCode = baCode;
    }

    public String getBaSymbol() {
        return baSymbol;
    }

    public void setBaSymbol(String baSymbol) {
        this.baSymbol = baSymbol;
    }

    public BigDecimal getBaRate() {
        return baRate;
    }

    public void setBaRate(BigDecimal baRate) {
        this.baRate = baRate;
    }
}
