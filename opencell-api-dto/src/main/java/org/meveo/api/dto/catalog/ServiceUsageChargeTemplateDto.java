package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ServiceUsageChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
@XmlRootElement(name = "ServiceUsageChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceUsageChargeTemplateDto extends BaseServiceChargeTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1612154476117247213L;

    /**
     * Instantiates a new service usage charge template dto.
     */
    public ServiceUsageChargeTemplateDto() {

    }
}