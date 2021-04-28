package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class MatrixRatingRequest {

    @Schema(description = "The price plan matrix code", required = true)
    @NotNull
    private String ppmCode;

    @Schema(description = "The price plan matrix version", required = true)
    @NotNull
    private Integer ppmVersion;

    @Schema(description = "The charge instance code", required = true)
    @NotNull
    private String chargeInstanceCode;

    public String getPpmCode() {
        return ppmCode;
    }

    public void setPpmCode(String ppmCode) {
        this.ppmCode = ppmCode;
    }

    public Integer getPpmVersion() {
        return ppmVersion;
    }

    public void setPpmVersion(Integer ppmVersion) {
        this.ppmVersion = ppmVersion;
    }

    public String getChargeInstanceCode() {
        return chargeInstanceCode;
    }

    public void setChargeInstanceCode(String chargeInstanceCode) {
        this.chargeInstanceCode = chargeInstanceCode;
    }
}
