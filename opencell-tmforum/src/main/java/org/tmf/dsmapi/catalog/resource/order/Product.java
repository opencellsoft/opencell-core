package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.catalog.resource.RelatedParty;
import org.tmf.dsmapi.catalog.resource.product.Place;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name="Product", namespace="http://www.tmforum.org")
@XmlType(name="Product", namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class Product implements Serializable {

    private static final long serialVersionUID = 5933865642305663840L;
    private String id;
    private String href;
    private Place place;
    private List<ProductCharacteristic> productCharacteristic = new ArrayList<ProductCharacteristic>();
    private List<RelatedParty> relatedParty;
    private List<ProductRelationship> productRelationship;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public List<ProductCharacteristic> getProductCharacteristic() {
        return productCharacteristic;
    }

    public void setProductCharacteristic(List<ProductCharacteristic> productCharacteristic) {
        this.productCharacteristic = productCharacteristic;
    }

    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public List<ProductRelationship> getProductRelationship() {
        return productRelationship;
    }

    public void setProductRelationship(List<ProductRelationship> productRelationship) {
        this.productRelationship = productRelationship;
    }

}
