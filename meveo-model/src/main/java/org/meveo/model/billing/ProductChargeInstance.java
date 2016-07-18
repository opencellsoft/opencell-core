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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.catalog.OfferTemplate;
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


	public ProductChargeInstance(String code, String description, Date chargeDate,
			UserAccount userAccount,OfferTemplate offerTemplate, 
			BigDecimal amountWithoutTax, BigDecimal amount2,
			ProductInstance productInstance,ProductChargeTemplate productChargeTemplate) {
		this.code = code;
		this.description = description;
		setChargeDate(chargeDate);
		setAmountWithoutTax(amountWithoutTax);
		setAmountWithTax(amount2);
		this.userAccount=userAccount;
		this.offerTemplate = offerTemplate;
		this.setSeller(userAccount.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
		this.setCountry(userAccount.getBillingAccount().getTradingCountry());
		this.setCurrency(userAccount.getBillingAccount().getCustomerAccount().getTradingCurrency());
		this.productInstance = productInstance;
		this.productChargeTemplate=productChargeTemplate;
		this.status = InstanceStatusEnum.ACTIVE;
	}

	public ProductChargeInstance() {

	}
}
