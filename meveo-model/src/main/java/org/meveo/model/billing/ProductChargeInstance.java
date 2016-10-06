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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.ProductChargeTemplate;

@Entity
@Table(name = "BILLING_PRODUCT_CHARGE_INST")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_PRODUCT_CHRG_INST_SEQ")
public class ProductChargeInstance extends ChargeInstance {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_CHRG_TMPL_ID")
	private ProductChargeTemplate productChargeTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_INSTANCE_ID")
	private ProductInstance productInstance;

    @Column(name = "QUANTITY", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal quantity = BigDecimal.ONE;

	public ProductChargeInstance(ProductInstance productInstance, ProductChargeTemplate productChargeTemplate, User user){
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
		
        Auditable auditable = new Auditable();
        auditable.setCreated(new Date());
        auditable.setCreator(user);
		this.setAuditable(auditable);
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
