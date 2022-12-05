package org.meveo.api.dto.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoadPricesRequest {

    @Schema(description = "The price plan matrix code", required = true)
    @NotNull
    private String ppmCode;
    
    @Schema(description = "The price plan matrix veriosn", required = true)
    @NotNull
    private Integer ppmVersion;

    @Schema(description = "The quote product id", required = true)
    @NotNull
    private Long quoteProductId;

    public String getPpmCode() {
        return ppmCode;
    }

    public void setPpmCode(String ppmCode) {
        this.ppmCode = ppmCode;
    }

    public Integer getPpmVersion() {
        return ppmVersion;
    }

    public Long getQuoteProductId() {
        return quoteProductId;
    }

    public void setQuoteProductId(Long quoteProductId) {
        this.quoteProductId = quoteProductId;
    }
}
