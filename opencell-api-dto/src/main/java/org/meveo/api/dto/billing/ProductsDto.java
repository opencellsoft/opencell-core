package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ProductsDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6816362012819614661L;

    /** The products. */
    private List<ProductDto> products;

    /**
     * Gets the products.
     *
     * @return the products
     */
    public List<ProductDto> getProducts() {
        if (products == null) {
            products = new ArrayList<ProductDto>();
        }

        return products;
    }

    /**
     * Sets the products.
     *
     * @param products the new products
     */
    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "ProductsDto [products=" + products + "]";
    }

}