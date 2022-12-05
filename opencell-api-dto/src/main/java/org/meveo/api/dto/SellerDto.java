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
package org.meveo.api.dto;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.account.CustomersDto;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;

/**
 * The Class SellerDto.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Khalid HORRI
 * @lastModifiedVersion 5.3
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4763606402719751014L;

    /** The currency code. */
    private String currencyCode;

    /** The country code. */
    private String countryCode;

    /** The language code. */
    private String languageCode;

    /** The parent seller. */
    private String parentSeller;

    /** The customers. */
    private CustomersDto customers;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The invoice type sequences. */
    private Map<String, SequenceDto> invoiceTypeSequences = new HashMap<>();

    /** The business account model. */
    @XmlElement(name = "businessAccountModel")
    private BusinessEntityDto businessAccountModel;

    /** The contact information. */
    private ContactInformationDto contactInformation;

    /** The address. */
    private AddressDto address;

    /**
     * The seller VAT No
     */
    private String vatNo;

    /**
     * The seller registration No
     */
    private String registrationNo;

    /**
     * A legal text for the seller
     */
    private String legalText;

    /**
     * The legal type of the seller
     */
    private String legalType;

    /**
     * Instantiates a new seller dto.
     */
    public SellerDto() {
    }

    /**
     * Create SellerDto from Seller and CustomFieldsDto v5.0: Added ContactInformation and Address
     *
     * @author akadid abdelmounaim
     * @param seller seller
     * @param customFieldInstances customFieldsDto
     * @lastModifiedVersion 5.0
     */
    public SellerDto(Seller seller, CustomFieldsDto customFieldInstances) {
        super(seller);
        if (seller.getInvoiceTypeSequence() != null) {
            for (InvoiceTypeSellerSequence seq : seller.getInvoiceTypeSequence()) {
                invoiceTypeSequences.put(seq.getInvoiceType().getCode(), new SequenceDto(seq.getInvoiceSequence(), seq.getPrefixEL()));
            }
        }

        id = seller.getId();

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

    /**
     * Gets the currency code.
     *
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode the new currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * Gets the country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the new country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the language code.
     *
     * @return the language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the new language code
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    /**
     * Gets the parent seller.
     *
     * @return the parent seller
     */
    public String getParentSeller() {
        return parentSeller;
    }

    /**
     * Sets the parent seller.
     *
     * @param parentSeller the new parent seller
     */
    public void setParentSeller(String parentSeller) {
        this.parentSeller = parentSeller;
    }

    /**
     * Gets the customers.
     *
     * @return the customers
     */
    public CustomersDto getCustomers() {
        return customers;
    }

    /**
     * Sets the customers.
     *
     * @param customers the new customers
     */
    public void setCustomers(CustomersDto customers) {
        this.customers = customers;
    }

    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the invoice type sequences.
     *
     * @return the invoiceTypeSequences
     */
    public Map<String, SequenceDto> getInvoiceTypeSequences() {
        return invoiceTypeSequences;
    }

    /**
     * Sets the invoice type sequences.
     *
     * @param invoiceTypeSequences the invoiceTypeSequences to set
     */
    public void setInvoiceTypeSequences(Map<String, SequenceDto> invoiceTypeSequences) {
        this.invoiceTypeSequences = invoiceTypeSequences;
    }

    /**
     * Gets the business account model.
     *
     * @return the business account model
     */
    public BusinessEntityDto getBusinessAccountModel() {
        return businessAccountModel;
    }

    /**
     * Sets the business account model.
     *
     * @param businessAccountModel the new business account model
     */
    public void setBusinessAccountModel(BusinessEntityDto businessAccountModel) {
        this.businessAccountModel = businessAccountModel;
    }

    /**
     * Gets the contact information.
     *
     * @return the contact information
     */
    public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    /**
     * Sets the contact information.
     *
     * @param contactInformation the new contact information
     */
    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public AddressDto getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the new address
     */
    public void setAddress(AddressDto address) {
        this.address = address;
    }

    /**
     * Gets the seller's VAT No
     * @return a VAT No
     *
     */
    public String getVatNo() {
        return vatNo;
    }

    /**
     * Sets the seller's VAT No
     * @param vatNo  new VAT No
     */
    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    /**
     * Gets the seller's registration No
     * @return a registration No
     *
     */
    public String getRegistrationNo() {
        return registrationNo;
    }

    /**
     * Sets the seller's registration No
     * @param registrationNo  new registration No
     */
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    /**
     * Gets the seller's legal text
     * @return a legal text
     */
    public String getLegalText() {
        return legalText;
    }

    /**
     * Sets the seller's legal text
     * @param legalText new legal text
     */
    public void setLegalText(String legalText) {
        this.legalText = legalText;
    }

    /**
     * Gets the seller's legal type
     * @return a legal type
     */
    public String getLegalType() {
        return legalType;
    }

    /**
     * Sets the seller's legal type
     * @param legalType new legal type
     */
    public void setLegalType(String legalType) {
        this.legalType = legalType;
    }

    @Override
    public String toString() {
        return "SellerDto [code=" + getCode() + ", description=" + getDescription() + ", currencyCode=" + currencyCode + ", countryCode=" + countryCode + ", languageCode="
                + languageCode + ", parentSeller=" + parentSeller + ", customers=" + customers + ", customFields=" + customFields + ", invoiceTypeSequences=" + invoiceTypeSequences
                + ", businessAccountModel=" + businessAccountModel + ", contactInformation=" + contactInformation + ", address=" + address + "]";
    }
}