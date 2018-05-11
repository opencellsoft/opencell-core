/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Entity
@Table(name = "cat_product_charge_templ")
@NamedQueries({
        @NamedQuery(name = "productChargeTemplate.getNbrProductWithNotPricePlan", query = "select count (*) from ProductChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "),
        @NamedQuery(name = "productChargeTemplate.getProductWithNotPricePlan", query = "from ProductChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "), })
public class ProductChargeTemplate extends ChargeTemplate {

    @Transient
    public static final String CHARGE_TYPE = "PRODUCT";

    private static final long serialVersionUID = 1L;
    
    @Column(name = "filter_expression", length = 2000)
    @Size(max = 2000)
    private String filterExpression = null;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "productChargeTemplates")
    private List<ProductTemplate> productTemplates = new ArrayList<>();

    public List<ProductTemplate> getProductTemplates() {
        return productTemplates;
    }

    public void setProductTemplates(List<ProductTemplate> productTemplates) {
        this.productTemplates = productTemplates;
    }

    public String getChargeType() {
        return CHARGE_TYPE;
    }
    
    public String getFilterExpression() {
        return filterExpression;
    }

    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

}
