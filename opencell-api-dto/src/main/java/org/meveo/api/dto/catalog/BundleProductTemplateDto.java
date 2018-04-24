package org.meveo.api.dto.catalog;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class BundleProductTemplateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "BundleProductTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class BundleProductTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4914322874611290121L;

    /** The product template. */
    private ProductTemplateDto productTemplate;

    /** The quantity. */
    private int quantity;

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
     * Gets the quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}