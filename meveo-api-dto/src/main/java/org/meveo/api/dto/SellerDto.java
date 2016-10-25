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
package org.meveo.api.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.account.CustomersDto;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.Sequence;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Seller")
@XmlType(name = "Seller")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerDto extends BaseDto {

	private static final long serialVersionUID = 4763606402719751014L;

	/**
	 * Code
	 */
	@XmlAttribute(required = true)
	private String code;

	/**
	 * Description
	 */
	@XmlAttribute()
	private String description;

	private String currencyCode;
	private String countryCode;
	private String languageCode;
	private String parentSeller;
	private String provider;

	private CustomersDto customers;
	
	private CustomFieldsDto customFields = new CustomFieldsDto();
	
	private Map<String,SequenceDto> invoiceTypeSequences = new HashMap<String,SequenceDto>();

	@XmlElement(name = "businessAccountModel")
	private BusinessEntityDto businessAccountModel;

	public SellerDto() {
	}

	public SellerDto(Seller seller, CustomFieldsDto customFieldInstances) {
		code = seller.getCode();
		description = seller.getDescription();
		if(seller.getInvoiceTypeSequence() != null){
			for(Entry<InvoiceType, Sequence> entry : seller.getInvoiceTypeSequence().entrySet() ){
				invoiceTypeSequences.put(entry.getKey().getCode(), new SequenceDto(entry.getValue()));
			}
		}

		if (seller.getTradingCountry() != null) {
			countryCode = seller.getTradingCountry().getCountryCode();
		}

		if (seller.getTradingCurrency() != null) {
			currencyCode = seller.getTradingCurrency().getCurrencyCode();
		}

		if (seller.getTradingLanguage() != null) {
			languageCode = seller.getTradingLanguage().getLanguageCode();
		}

		if (seller.getSeller() != null) {
			parentSeller = seller.getSeller().getCode();
		}

		if (seller.getProvider() != null) {
			provider = seller.getProvider().getCode();
		}
		
		customFields = customFieldInstances;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getParentSeller() {
		return parentSeller;
	}

	public void setParentSeller(String parentSeller) {
		this.parentSeller = parentSeller;
	}



	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public CustomersDto getCustomers() {
		return customers;
	}

	public void setCustomers(CustomersDto customers) {
		this.customers = customers;
	}

	public CustomFieldsDto getCustomFields() {
        return customFields;
    }
	
	public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

	/**
	 * @return the invoiceTypeSequences
	 */
	public Map<String, SequenceDto> getInvoiceTypeSequences() {
		return invoiceTypeSequences;
	}

	/**
	 * @param invoiceTypeSequences the invoiceTypeSequences to set
	 */
	public void setInvoiceTypeSequences(Map<String, SequenceDto> invoiceTypeSequences) {
		this.invoiceTypeSequences = invoiceTypeSequences;
	}

	public BusinessEntityDto getBusinessAccountModel() {
		return businessAccountModel;
	}

	public void setBusinessAccountModel(BusinessEntityDto businessAccountModel) {
		this.businessAccountModel = businessAccountModel;
	}

	@Override
	public String toString() {
		return "SellerDto [code=" + code + ", description=" + description + ", currencyCode=" + currencyCode + ", countryCode=" + countryCode + ", languageCode=" + languageCode + ", parentSeller=" + parentSeller + ", provider=" + provider + ", customers=" + customers + ", customFields=" + customFields + ", invoiceTypeSequences=" + invoiceTypeSequences + ", businessAccountModel=" + businessAccountModel + "]";
	}

	
	
	
}