package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="ProductRelationship", namespace="http://www.tmforum.org")
@XmlType(name="ProductRelationship", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductRelationship implements Serializable {

    private static final long serialVersionUID = 22693302638836241L;
    private String type;
    private Product product;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
