package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class LoadPricesRequest {
    @NotNull
    private String ppmCode;
    @NotNull
    private Integer ppmVersion;
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
