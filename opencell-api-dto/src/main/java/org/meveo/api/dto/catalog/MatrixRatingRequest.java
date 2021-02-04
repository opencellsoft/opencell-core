package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class MatrixRatingRequest {

    @NotNull
    private String ppmCode;
    @NotNull
    private Integer ppmVersion;
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
