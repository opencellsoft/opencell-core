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
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Product charge template
 * 
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.1
 */
@Entity
@DiscriminatorValue("P")
@NamedQueries({
        @NamedQuery(name = "productChargeTemplate.getNbrProductWithNotPricePlan", query = "select count (*) from ProductChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "),
        @NamedQuery(name = "productChargeTemplate.getProductWithNotPricePlan", query = "from ProductChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "), })
public class ProductChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = 1L;

    /**
     * Product templates the charge applies to
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "productChargeTemplates")
    private List<ProductTemplate> productTemplates = new ArrayList<>();

    public List<ProductTemplate> getProductTemplates() {
        return productTemplates;
    }

    public void setProductTemplates(List<ProductTemplate> productTemplates) {
        this.productTemplates = productTemplates;
    }
    
    @Override
    public ChargeMainTypeEnum getChargeMainType() {
        return ChargeMainTypeEnum.PRODUCT;
    }

}