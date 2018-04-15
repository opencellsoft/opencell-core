package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateSubscriptionDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateSubscriptionDto extends BaseServiceChargeTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6508584475693802506L;

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.dto.catalog.BaseServiceChargeTemplateDto#toString()
     */
    @Override
    public String toString() {
        return "ServiceChargeTemplateSubscriptionDto [getCode()=" + getCode() + ", getWallets()=" + getWallets() + "]";
    }

}
