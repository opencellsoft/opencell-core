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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerDto extends BusinessDto {

	private static final long serialVersionUID = 4763606402719751014L;

	private String currencyCode;
	private String countryCode;
	private String languageCode;
	private String parentSeller;

	private CustomersDto customers;
	
	private CustomFieldsDto customFields;
	
	private Map<String,SequenceDto> invoiceTypeSequences = new HashMap<String,SequenceDto>();

	@XmlElement(name = "businessAccountModel")
	private BusinessEntityDto businessAccountModel;
	
    private ContactInformationDto contactInformation;
    private AddressDto address;

	public SellerDto() {
	}

	/**
     * Create SellerDto from Seller and CustomFieldsDto
     * v5.0: Added ContactInformation and Address
     * 
     * @param seller seller
     * @param customFieldInstances customFieldsDto
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
	public SellerDto(Seller seller, CustomFieldsDto customFieldInstances) {
		super(seller);
		if(seller.getInvoiceTypeSequence() != null){
			for(InvoiceTypeSellerSequence seq : seller.getInvoiceTypeSequence() ){
				invoiceTypeSequences.put(seq.getInvoiceType().getCode(), new SequenceDto(seq.getSequence()));
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
		
		ContactInformation sellerContactInformation = seller.getContactInformation();
		if (sellerContactInformation != null) {
            if (getContactInformation() == null) {
                setContactInformation(new ContactInformationDto());
            }
            contactInformation.setEmail(sellerContactInformation.getEmail());
            contactInformation.setPhone(sellerContactInformation.getPhone());
            contactInformation.setMobile(sellerContactInformation.getMobile());
            contactInformation.setFax(sellerContactInformation.getFax());
        }
		
		Address sellerAddress = seller.getAddress();
        if (sellerAddress != null) {
            if (getAddress() == null) {
                setAddress(new AddressDto());
            }
            address.setAddress1(sellerAddress.getAddress1());
            address.setAddress2(sellerAddress.getAddress2());
            address.setAddress3(sellerAddress.getAddress3());
            address.setCity(sellerAddress.getCity());
            address.setCountry(sellerAddress.getCountry() == null ? null : sellerAddress.getCountry().getCountryCode());                        
            address.setState(sellerAddress.getState());
            address.setZipCode(sellerAddress.getZipCode());
        }
		
		customFields = customFieldInstances;
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
	
	public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @Override
	public String toString() {
		return "SellerDto [code=" + getCode() + ", description=" + getDescription() + ", currencyCode=" + currencyCode + ", countryCode=" + countryCode + ", languageCode=" + languageCode + ", parentSeller=" + parentSeller + ", customers=" + customers + ", customFields=" + customFields + ", invoiceTypeSequences=" + invoiceTypeSequences + ", businessAccountModel=" + businessAccountModel + ", contactInformation=" + contactInformation + ", address=" + address + "]";
	}	
}