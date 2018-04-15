package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateTerminationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateTerminationDto extends BaseServiceChargeTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -191541706032220541L;

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.dto.catalog.BaseServiceChargeTemplateDto#toString()
     */
    @Override
    public String toString() {
        return "ServiceChargeTemplateTerminationDto [getCode()=" + getCode() + ", getWallets()=" + getWallets() + "]";
    }

}
