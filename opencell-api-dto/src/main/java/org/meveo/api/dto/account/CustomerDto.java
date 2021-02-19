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

package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.GDPRInfoDto;
import org.meveo.api.dto.crm.AdditionalDetailsDto;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.crm.Customer;

/**
 * The Class CustomerDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
// @FilterResults(propertyToFilter = "customerAccounts.customerAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = CustomerAccount.class) })
public class CustomerDto extends AccountDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3243716253817571391L;

    /** The customer category. */
    @XmlElement(required = true)
    private String customerCategory;

    /** The customer brand. */
    @XmlElement()
    private String customerBrand;

    /** The seller. */
    @XmlElement(required = true)
    private String seller;

    /** The mandate identification. */
    private String mandateIdentification = "";
    
    /** The mandate date. */
    private Date mandateDate;

    /**
     * Use for GET / LIST only.
     */
    private CustomerAccountsDto customerAccounts = new CustomerAccountsDto();

    private AdditionalDetailsDto additionalDetails = new AdditionalDetailsDto();

    /**
     * Invoicing threshold - do not invoice for a lesser amount.
     */
    private BigDecimal invoicingThreshold;

    /**
     * The option on how to check the threshold.
     */
    private ThresholdOptionsEnum checkThreshold;

    /**
     * 
     * check the threshold per entity/invoice.
     */
    @XmlElement
    private Boolean thresholdPerEntity;
    

    /** information GDPR **/
    private List<GDPRInfoDto> infoGdpr;

    public Boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(Boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

    /**
     * Instantiates a new customer dto.
     */
    public CustomerDto() {
        super();
    }

    /**
     * Instantiates a new customer dto.
     * 
     * @param e Customer entity
     */
	public CustomerDto(Customer e) {
		super(e);

		setVatNo(e.getVatNo());
		setRegistrationNo(e.getRegistrationNo());

		if (e.getCustomerCategory() != null) {
			setCustomerCategory(e.getCustomerCategory().getCode());
		}

		if (e.getCustomerBrand() != null) {
			setCustomerBrand(e.getCustomerBrand().getCode());
		}

		if (e.getSeller() != null) {
			setSeller(e.getSeller().getCode());
		}

        if (e.getContactInformation() != null) {
            setContactInformation(new ContactInformationDto(e.getContactInformation()));
        }

        if (e.getAdditionalDetails() != null) {
            setAdditionalDetails(new AdditionalDetailsDto(e.getAdditionalDetails()));
        }

        if (e.getMinimumAmountEl() != null) {
            setMinimumAmountEl(e.getMinimumAmountEl());
        }
        if (e.getMinimumLabelEl() != null) {
            setMinimumLabelEl(e.getMinimumLabelEl());
        }
        if (e.getMinimumTargetAccount() != null) {
            setMinimumTargetAccount(e.getMinimumTargetAccount().getCode());
        }
        if (e.getInvoicingThreshold() != null) {
            setInvoicingThreshold(e.getInvoicingThreshold());
        }
        if (e.getCheckThreshold() != null) {
            setCheckThreshold(e.getCheckThreshold());
            setThresholdPerEntity(e.isThresholdPerEntity());
        }
    }
	
	public CustomerDto(Customer e, List<GDPRInfoDto> customField) {
		this(e);
		if(customField != null &&!customField.isEmpty()) {
			setInfoGdpr(customField);
		}
	}

    /**
     * Gets the customer category.
     *
     * @return the customer category
     */
    public String getCustomerCategory() {
        return customerCategory;
    }

    /**
     * Sets the customer category.
     *
     * @param customerCategory the new customer category
     */
    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    /**
     * Gets the seller.
     *
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller the new seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Gets the customer brand.
     *
     * @return the customer brand
     */
    public String getCustomerBrand() {
        return customerBrand;
    }

    /**
     * Sets the customer brand.
     *
     * @param customerBrand the new customer brand
     */
    public void setCustomerBrand(String customerBrand) {
        this.customerBrand = customerBrand;
    }

	/**
     * Gets the customer accounts.
     *
     * @return the customer accounts
     */
    public CustomerAccountsDto getCustomerAccounts() {
        return customerAccounts;
    }

    /**
     * Sets the customer accounts.
     *
     * @param customerAccounts the new customer accounts
     */
    public void setCustomerAccounts(CustomerAccountsDto customerAccounts) {
        this.customerAccounts = customerAccounts;
    }

    /**
     * Gets the mandate identification.
     *
     * @return the mandate identification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * Sets the mandate identification.
     *
     * @param mandateIdentification the new mandate identification
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * Gets the mandate date.
     *
     * @return the mandate date
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * Sets the mandate date.
     *
     * @param mandateDate the new mandate date
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }
   
    public AdditionalDetailsDto getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(AdditionalDetailsDto additionalDetails) {
		this.additionalDetails = additionalDetails;
	}


	/**
     * @return the invoicingThreshold
     */
    public BigDecimal getInvoicingThreshold() {
        return invoicingThreshold;
    }

    /**
     * @param invoicingThreshold the invoicingThreshold to set
     */
    public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
        this.invoicingThreshold = invoicingThreshold;
    }

    /**
     * Gets the threshold option.
     *
     * @return the threshold option
     */
    public ThresholdOptionsEnum getCheckThreshold() {
        return checkThreshold;
    }

    /**
     * Sets the threshold option.
     *
     * @param checkThreshold the threshold option
     */
    public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
        this.checkThreshold = checkThreshold;
    }

	/**
	 * @return the infoGdpr
	 */
	public List<GDPRInfoDto> getInfoGdpr() {
		return infoGdpr;
	}

	/**
	 * @param infoGdpr the infoGdpr to set
	 */
	public void setInfoGdpr(List<GDPRInfoDto> infoGdpr) {
		this.infoGdpr = infoGdpr;
	}

    @Override
    public String toString() {
        return "CustomerDto [customerCategory=" + customerCategory + ", customerBrand=" + customerBrand + ", seller=" + seller + ", mandateIdentification=" + mandateIdentification
                + ", mandateDate=" + mandateDate + ", contactInformation=" + getContactInformation() + ", customerAccounts=" + customerAccounts + "]";
    }
}