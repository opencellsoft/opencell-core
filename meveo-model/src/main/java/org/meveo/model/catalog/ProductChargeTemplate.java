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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.meveo.model.MultilanguageEntity;

@Entity
@MultilanguageEntity(key = "menu.charges", group = "ChargeTemplate")
@Table(name = "CAT_PRODUCT_CHARGE_TEMPL")
@NamedQueries({
		@NamedQuery(name = "productChargeTemplate.getNbrProductWithNotPricePlan", query = "select count (*) from ProductChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) and o.provider=:provider"),
		@NamedQuery(name = "productChargeTemplate.getProductWithNotPricePlan", query = "from ProductChargeTemplate o where o.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) and o.provider=:provider"), })
public class ProductChargeTemplate extends ChargeTemplate {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_TMPL_ID")
	private ProductTemplate productTemplate;

	public ProductTemplate getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplate productTemplate) {
		this.productTemplate = productTemplate;
	}

}
