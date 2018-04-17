/**
 * 
 */
package org.meveo.api.dto.response.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetOneShotChargesResponseDto.
 *
 * @author phung
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOneShotChargesResponseDto extends BaseResponse implements Serializable {

    /** serial version uid. */
    private static final long serialVersionUID = -1931425527526409004L;

    /** The oneshot charges. */
    private List<OneShotChargeTemplateDto> oneshotCharges;

    /**
     * Gets the oneshot charges.
     *
     * @return the oneshot charges
     */
    public List<OneShotChargeTemplateDto> getOneshotCharges() {
        if (oneshotCharges == null) {
            oneshotCharges = new ArrayList<OneShotChargeTemplateDto>();
        }
        return oneshotCharges;
    }

    /**
     * Sets the oneshot charges.
     *
     * @param oneshotCharges the new oneshot charges
     */
    public void setOneshotCharges(List<OneShotChargeTemplateDto> oneshotCharges) {
        this.oneshotCharges = oneshotCharges;
    }

    @Override
    public String toString() {
        return "GetOneShotChargesResponseDto [oneshotCharges=" + oneshotCharges + "]";
    }
}