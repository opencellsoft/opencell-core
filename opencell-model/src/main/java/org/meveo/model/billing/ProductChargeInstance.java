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
package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.catalog.ProductChargeTemplate;

@Entity
@Table(name = "billing_product_charge_inst")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_product_chrg_inst_seq"), })
public class ProductChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_chrg_tmpl_id")
	private ProductChargeTemplate productChargeTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_instance_id")
	private ProductInstance productInstance;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal quantity = BigDecimal.ONE;

	public ProductChargeInstance(ProductInstance productInstance, ProductChargeTemplate productChargeTemplate){
		this.code = productInstance.getCode();
		this.description = productInstance.getDescription();
		this.chargeDate = productInstance.getApplicationDate();
		this.userAccount=productInstance.getUserAccount();
		this.subscription=productInstance.getSubscription();
		this.setSeller(userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
		this.setCountry(userAccount.getBillingAccount().getTradingCountry());
		this.setCurrency(userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency());
		this.productInstance = productInstance;
		this.status = InstanceStatusEnum.ACTIVE;
		this.setQuantity(productInstance.getQuantity()==null?BigDecimal.ONE:productInstance.getQuantity());
		this.chargeTemplate=productChargeTemplate;
		this.productChargeTemplate=productChargeTemplate;		
	}

	public ProductChargeInstance() {

	}

	public ProductChargeTemplate getProductChargeTemplate() {
		return productChargeTemplate;
	}

	public void setProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
		this.productChargeTemplate = productChargeTemplate;
	}

	public ProductInstance getProductInstance() {
		return productInstance;
	}

	public void setProductInstance(ProductInstance productInstance) {
		this.productInstance = productInstance;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	
}
