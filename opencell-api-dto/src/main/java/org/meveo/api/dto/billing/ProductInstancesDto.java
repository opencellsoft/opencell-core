package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * The Class ProductInstancesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstancesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3365845607399516795L;

    /** The product instances. */
    @XmlElementWrapper(name = "productInstances")
    @XmlElement(name = "productInstance")
    private List<ProductInstanceDto> productInstances = new ArrayList<ProductInstanceDto>();

    /**
     * Gets the product instances.
     *
     * @return the product instances
     */
    public List<ProductInstanceDto> getProductInstances() {
        return productInstances;
    }

    /**
     * Sets the product instances.
     *
     * @param productInstances the new product instances
     */
    public void setProductInstances(List<ProductInstanceDto> productInstances) {
        this.productInstances = productInstances;
    }

}