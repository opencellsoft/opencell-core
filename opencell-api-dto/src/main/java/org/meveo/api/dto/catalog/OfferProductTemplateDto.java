package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class OfferProductTemplateDto.
 */
@XmlRootElement(name = "OfferProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferProductTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1231940046600480645L;

    /** The product template. */
    private ProductTemplateDto productTemplate;

    /** The mandatory. */
    private Boolean mandatory;

    /**
     * Instantiates a new offer product template dto.
     */
    public OfferProductTemplateDto() {
    }

    /**
     * Gets the product template.
     *
     * @return the product template
     */
    public ProductTemplateDto getProductTemplate() {
        return productTemplate;
    }

    /**
     * Sets the product template.
     *
     * @param productTemplate the new product template
     */
    public void setProductTemplate(ProductTemplateDto productTemplate) {
        this.productTemplate = productTemplate;
    }

    /**
     * Gets the mandatory.
     *
     * @return the mandatory
     */
    public Boolean getMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param mandatory the new mandatory
     */
    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

}
