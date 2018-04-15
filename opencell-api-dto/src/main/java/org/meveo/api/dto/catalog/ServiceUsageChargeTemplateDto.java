package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ServiceUsageChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServiceUsageChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceUsageChargeTemplateDto extends BaseServiceChargeTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1612154476117247213L;

    /** The counter template. */
    private String counterTemplate;

    /**
     * Instantiates a new service usage charge template dto.
     */
    public ServiceUsageChargeTemplateDto() {

    }

    /**
     * Instantiates a new service usage charge template dto.
     *
     * @param usageChargeTemplate the usage charge template
     * @param counterTemplate the counter template
     */
    public ServiceUsageChargeTemplateDto(String usageChargeTemplate, String counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    /**
     * Gets the counter template.
     *
     * @return the counter template
     */
    public String getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Sets the counter template.
     *
     * @param counterTemplate the new counter template
     */
    public void setCounterTemplate(String counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.dto.catalog.BaseServiceChargeTemplateDto#toString()
     */
    @Override
    public String toString() {
        return "ServiceUsageChargeTemplateDto [counterTemplate=" + counterTemplate + ", getCode()=" + getCode() + ", getWallets()=" + getWallets() + "]";
    }

}
