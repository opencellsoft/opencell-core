/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
