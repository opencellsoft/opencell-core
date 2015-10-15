/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.admin;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.shared.Address;

@Entity
@ObservableEntity
@CustomFieldEntity(accountLevel = AccountLevelEnum.SELLER)
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

	@Column(name = "INVOICE_PREFIX", length = 50)
	private String invoicePrefix;

	@Column(name = "CURRENT_INVOICE_NB")
	private Long currentInvoiceNb;

	@Column(name = "INVOICE_ADJUSTMENT_PREFIX", length = 50)
	private String invoiceAdjustmentPrefix;

	@Column(name = "CURRENT_INVOICE_ADJUSTMENT_NB")
	private Long currentInvoiceAdjustmentNb;

	@Column(name = "INVOICE_ADJUSTMENT_SEQUENCE_SIZE")
	private Integer invoiceAdjustmentSequenceSize = 9;

	@Embedded
	private Address address = new Address();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_SELLER_ID")
	private Seller seller;

	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@MapKeyColumn(name = "code")
	private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

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

	public String getInvoicePrefix() {
		return invoicePrefix;
	}

	public void setInvoicePrefix(String invoicePrefix) {
		this.invoicePrefix = invoicePrefix;
	}

	public Long getCurrentInvoiceNb() {
		return currentInvoiceNb;
	}

	public void setCurrentInvoiceNb(Long currentInvoiceNb) {
		this.currentInvoiceNb = currentInvoiceNb;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Map<String, CustomFieldInstance> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
		this.customFields = customFields;
	}

	@Override
	public ICustomFieldEntity getParentCFEntity() {
		if (seller != null) {
			return seller;
		}
		return getProvider();
	}

	public String getInvoiceAdjustmentPrefix() {
		return invoiceAdjustmentPrefix;
	}

	public void setInvoiceAdjustmentPrefix(String invoiceAdjustmentPrefix) {
		this.invoiceAdjustmentPrefix = invoiceAdjustmentPrefix;
	}

	public Integer getInvoiceAdjustmentSequenceSize() {
		return invoiceAdjustmentSequenceSize;
	}

	public void setInvoiceAdjustmentSequenceSize(Integer invoiceAdjustmentSequenceSize) {
		this.invoiceAdjustmentSequenceSize = invoiceAdjustmentSequenceSize;
	}

	public Long getCurrentInvoiceAdjustmentNb() {
		return currentInvoiceAdjustmentNb;
	}

	public void setCurrentInvoiceAdjustmentNb(Long currentInvoiceAdjustmentNb) {
		this.currentInvoiceAdjustmentNb = currentInvoiceAdjustmentNb;
	}

}