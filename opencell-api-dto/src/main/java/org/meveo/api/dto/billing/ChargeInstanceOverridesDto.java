package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ChargeInstanceOverridesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeInstanceOverridesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3049268862157592555L;

    /** The charge instance override. */
    private List<ChargeInstanceOverrideDto> chargeInstanceOverride;

    /**
     * Gets the charge instance override.
     *
     * @return the charge instance override
     */
    public List<ChargeInstanceOverrideDto> getChargeInstanceOverride() {
        if (chargeInstanceOverride == null)
            chargeInstanceOverride = new ArrayList<ChargeInstanceOverrideDto>();
        return chargeInstanceOverride;
    }

    /**
     * Sets the charge instance override.
     *
     * @param chargeInstanceOverride the new charge instance override
     */
    public void setChargeInstanceOverride(List<ChargeInstanceOverrideDto> chargeInstanceOverride) {
        this.chargeInstanceOverride = chargeInstanceOverride;
    }

    @Override
    public String toString() {
        return "ChargeInstanceOverridesDto [chargeInstanceOverride=" + chargeInstanceOverride + ", toString()=" + super.toString() + "]";
    }

}