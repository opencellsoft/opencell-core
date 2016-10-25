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
package org.meveo.model.admin;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.Sequence;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.shared.Address;

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "SELLER")
@ExportIdentifier({ "code", "provider" })
@Table(name = "CRM_SELLER", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_SELLER_SEQ")
public class Seller extends BusinessCFEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_CURRENCY_ID")
	private TradingCurrency tradingCurrency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_COUNTRY_ID")
	private TradingCountry tradingCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_LANGUAGE_ID")
	private TradingLanguage tradingLanguage;

	
	@Embedded
	private Address address = new Address();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_SELLER_ID")
	private Seller seller;
	
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "BILLING_SEQ_INVTYP_SELL") 
	@MapKeyJoinColumn(name="INVOICETYPE_ID")
	Map<InvoiceType,Sequence> invoiceTypeSequence = new HashMap<InvoiceType,Sequence>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BAM_ID")
	private BusinessAccountModel businessAccountModel;
	
	
	public Seller() {
		super();
	}

	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public TradingCountry getTradingCountry() {
		return tradingCountry;
	}

	public void setTradingCountry(TradingCountry tradingCountry) {
		this.tradingCountry = tradingCountry;
	}

	public TradingLanguage getTradingLanguage() {
		return tradingLanguage;
	}

	public void setTradingLanguage(TradingLanguage tradingLanguage) {
		this.tradingLanguage = tradingLanguage;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}


	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	@Override
	public ICustomFieldEntity[] getParentCFEntities() {
		if (seller != null) {
			return new ICustomFieldEntity[]{seller};
		}
		return new ICustomFieldEntity[]{getProvider()};
	}

	/**
	 * @return the invoiceTypeSequence
	 */
	public Map<InvoiceType, Sequence> getInvoiceTypeSequence() {
		return invoiceTypeSequence;
	}

	/**
	 * @param invoiceTypeSequence the invoiceTypeSequence to set
	 */
	public void setInvoiceTypeSequence(Map<InvoiceType, Sequence> invoiceTypeSequence) {
		this.invoiceTypeSequence = invoiceTypeSequence;
	}

	public BusinessAccountModel getBusinessAccountModel() {
		return businessAccountModel;
	}

	public void setBusinessAccountModel(BusinessAccountModel businessAccountModel) {
		this.businessAccountModel = businessAccountModel;
	}

	@Override
	public BusinessEntity getParentEntity() {
		return seller;
	}
	
	@Override
	public Class<? extends BusinessEntity> getParentEntityType() {
		return Seller.class;
	}

}
