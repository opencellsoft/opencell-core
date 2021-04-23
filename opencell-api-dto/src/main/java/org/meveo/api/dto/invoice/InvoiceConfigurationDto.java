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

package org.meveo.api.dto.invoice;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceConfiguration;

/**
 * The Class InvoiceConfigurationDto.
 *
 * @author Mohamed Hamidi
 * @lastModifiedVersion 5.1
 */
@XmlRootElement(name = "InvoiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceConfigurationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3317673469040337077L;

    /** The display subscriptions. */
    private Boolean displaySubscriptions;
    
    /** The display services. */
    private Boolean displayServices;
    
    /** The display offers. */
    private Boolean displayOffers;
    
    /** The display edrs. */
    private Boolean displayEdrs;
    
    /** The display provider. */
    private Boolean displayProvider;
    
    /** The display cf as XML. */
    private Boolean displayCfAsXML;
    
    /** The display price plans. */
    private Boolean displayPricePlans;
    
    /** The display detail. */
    private Boolean displayDetail;
    
    /** The display charges periods. */
    private Boolean displayChargesPeriods;
    
    /** The display billing cycle. */
    private Boolean displayBillingCycle;
    
    /** The display free transac in invoice. */
    private Boolean displayFreeTransacInInvoice;
    
    /** The display orders. */
    private Boolean displayOrders;
    
    private Long currentInvoiceNb;
    
    /** display wallet operations. */
    private Boolean displayWalletOperations;
    
    /** Default invoice sub-category */
    private String defaultInvoiceSubcategoryCode;
    
    /** Default generic accounting article */
    private String defaultGenericArticleCode;
    
    /** Default discount accounting article */
    private String defaultDiscountArticleCode;
    
    /** Default advanced payment accounting article */
    private String defaultAdvancedPaymentArticleCode;
    
    /** Default invoice minimum accounting article */
    private String defaultInvoiceMinimumArticleCode;
    
    

	/**
     * Instantiates a new invoice configuration dto.
     */
    public InvoiceConfigurationDto() {
    }

    /**
     * Instantiates a new invoice configuration dto.
     *
     * @param invoiceConfiguration the invoice configuration
     */
    public InvoiceConfigurationDto(InvoiceConfiguration invoiceConfiguration) {
        displaySubscriptions = invoiceConfiguration.isDisplaySubscriptions();
        displayServices = invoiceConfiguration.isDisplayServices();
        displayOffers = invoiceConfiguration.isDisplayOffers();
        displayEdrs = invoiceConfiguration.isDisplayEdrs();
        displayProvider = invoiceConfiguration.isDisplayProvider();
        displayCfAsXML = invoiceConfiguration.isDisplayCfAsXML();
        displayPricePlans = invoiceConfiguration.isDisplayPricePlans();
        displayDetail = invoiceConfiguration.isDisplayDetail();
        displayBillingCycle = invoiceConfiguration.isDisplayBillingCycle();
        displayOrders = invoiceConfiguration.isDisplayOrders();
        currentInvoiceNb = invoiceConfiguration.getCurrentInvoiceNb();
        displayWalletOperations=invoiceConfiguration.isDisplayWalletOperations();
        defaultInvoiceSubcategoryCode = invoiceConfiguration.getDefaultInvoiceSubCategory().getCode();
        defaultGenericArticleCode = invoiceConfiguration.getDefaultGenericAccountingArticle().getCode();
        defaultDiscountArticleCode = invoiceConfiguration.getDefaultDiscountAccountingArticle().getCode();
        defaultAdvancedPaymentArticleCode = invoiceConfiguration.getDefaultAdvancedPaymentAccountingArticle().getCode();
        defaultInvoiceMinimumArticleCode = invoiceConfiguration.getDefaultInvoiceMinimumAccountingArticle().getCode();
    }

    /**
     * Gets the display subscriptions.
     *
     * @return the display subscriptions
     */
    public Boolean getDisplaySubscriptions() {
        return displaySubscriptions;
    }

    /**
     * Sets the display subscriptions.
     *
     * @param displaySubscriptions the new display subscriptions
     */
    public void setDisplaySubscriptions(Boolean displaySubscriptions) {
        this.displaySubscriptions = displaySubscriptions;
    }

    /**
     * Gets the display services.
     *
     * @return the display services
     */
    public Boolean getDisplayServices() {
        return displayServices;
    }

    /**
     * Sets the display services.
     *
     * @param displayServices the new display services
     */
    public void setDisplayServices(Boolean displayServices) {
        this.displayServices = displayServices;
    }

    /**
     * Gets the display offers.
     *
     * @return the display offers
     */
    public Boolean getDisplayOffers() {
        return displayOffers;
    }

    /**
     * Sets the display offers.
     *
     * @param displayOffers the new display offers
     */
    public void setDisplayOffers(Boolean displayOffers) {
        this.displayOffers = displayOffers;
    }

    /**
     * Gets the display edrs.
     *
     * @return the display edrs
     */
    public Boolean getDisplayEdrs() {
        return displayEdrs;
    }

    /**
     * Sets the display edrs.
     *
     * @param displayEdrs the new display edrs
     */
    public void setDisplayEdrs(Boolean displayEdrs) {
        this.displayEdrs = displayEdrs;
    }

    /**
     * Gets the display provider.
     *
     * @return the display provider
     */
    public Boolean getDisplayProvider() {
        return displayProvider;
    }

    /**
     * Sets the display provider.
     *
     * @param displayProvider the new display provider
     */
    public void setDisplayProvider(Boolean displayProvider) {
        this.displayProvider = displayProvider;
    }

    /**
     * Gets the display cf as XML.
     *
     * @return the display cf as XML
     */
    public Boolean getDisplayCfAsXML() {
        return displayCfAsXML;
    }

    /**
     * Sets the display cf as XML.
     *
     * @param displayCfAsXML the new display cf as XML
     */
    public void setDisplayCfAsXML(Boolean displayCfAsXML) {
        this.displayCfAsXML = displayCfAsXML;
    }

    /**
     * Gets the display price plans.
     *
     * @return the display price plans
     */
    public Boolean getDisplayPricePlans() {
        return displayPricePlans;
    }

    /**
     * Sets the display price plans.
     *
     * @param displayPricePlans the new display price plans
     */
    public void setDisplayPricePlans(Boolean displayPricePlans) {
        this.displayPricePlans = displayPricePlans;
    }

    /**
     * Gets the display detail.
     *
     * @return the display detail
     */
    public Boolean getDisplayDetail() {
        return displayDetail;
    }

    /**
     * Sets the display detail.
     *
     * @param displayDetail the new display detail
     */
    public void setDisplayDetail(Boolean displayDetail) {
        this.displayDetail = displayDetail;
    }

    /**
     * Gets the display charges periods.
     *
     * @return the display charges periods
     */
    public Boolean getDisplayChargesPeriods() {
        return displayChargesPeriods;
    }

    /**
     * Sets the display charges periods.
     *
     * @param displayChargesPeriods the new display charges periods
     */
    public void setDisplayChargesPeriods(Boolean displayChargesPeriods) {
        this.displayChargesPeriods = displayChargesPeriods;
    }

    /**
     * Gets the display free transac in invoice.
     *
     * @return the display free transac in invoice
     */
    public Boolean getDisplayFreeTransacInInvoice() {
        return displayFreeTransacInInvoice;
    }

    /**
     * Sets the display free transac in invoice.
     *
     * @param displayFreeTransacInInvoice the new display free transac in invoice
     */
    public void setDisplayFreeTransacInInvoice(Boolean displayFreeTransacInInvoice) {
        this.displayFreeTransacInInvoice = displayFreeTransacInInvoice;
    }

    /**
     * Gets the display billing cycle.
     *
     * @return the display billing cycle
     */
    public Boolean getDisplayBillingCycle() {
        return displayBillingCycle;
    }

    /**
     * Sets the display billing cycle.
     *
     * @param displayBillingCycle the new display billing cycle
     */
    public void setDisplayBillingCycle(Boolean displayBillingCycle) {
        this.displayBillingCycle = displayBillingCycle;
    }

    /**
     * Gets the display orders.
     *
     * @return the displayOrders
     */
    public Boolean getDisplayOrders() {
        return displayOrders;
    }

    /**
     * Sets the display orders.
     *
     * @param displayOrders the displayOrders to set
     */
    public void setDisplayOrders(Boolean displayOrders) {
        this.displayOrders = displayOrders;
    }
    
    

    /**
     * @return the currentInvoiceNb
     */
    public Long getCurrentInvoiceNb() {
        return currentInvoiceNb;
    }

    /**
     * @param currentInvoiceNb the currentInvoiceNb to set
     */
    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
        this.currentInvoiceNb = currentInvoiceNb;
    }
    
    public Boolean getDisplayWalletOperations() {
		return displayWalletOperations;
	}

	public void setDisplayWalletOperations(Boolean displayWalletOperations) {
		this.displayWalletOperations = displayWalletOperations;
	}

    @Override
    public String toString() {
        return "InvoiceConfigurationDto [displaySubscriptions=" + displaySubscriptions + ", displayServices=" + displayServices + ", displayOffers=" + displayOffers
                + ", displayEdrs=" + displayEdrs + ", displayPricePlans=" + displayPricePlans + ", displayCfAsXML=" + displayCfAsXML + ", displayProvider=" + displayProvider
                + ", displayDetail=" + displayDetail + ", displayChargesPeriods=" + displayChargesPeriods + ", displayFreeTransacInInvoice=" + displayFreeTransacInInvoice
                + ", displayBillingCycle=" + displayBillingCycle + ",displayOrders=" + displayOrders + ",currentInvoiceNb="+currentInvoiceNb+ ",displayWalletOperations="+displayWalletOperations+"]";
    }
    
    /**
     * 
     * @return default invoice sub-category
     */
	public String getDefaultInvoiceSubcategoryCode() {
		return defaultInvoiceSubcategoryCode;
	} 
	
	/**
	 * set default invoice sub-category
	 * @param defaultInvoiceSubcategoryCode
	 */
	public void setDefaultInvoiceSubcategoryCode(String defaultInvoiceSubcategoryCode) {
		this.defaultInvoiceSubcategoryCode = defaultInvoiceSubcategoryCode;
	}
	
	/**
	 * 
	 * @return default generic accounting article
	 */
	public String getDefaultGenericArticleCode() {
		return defaultGenericArticleCode;
	}
	
	/**
	 * set default generic accounting article
	 * @param defaultGenericArticleCode
	 */
	public void setDefaultGenericArticleCode(String defaultGenericArticleCode) {
		this.defaultGenericArticleCode = defaultGenericArticleCode;
	}
	
	/**
	 * 
	 * @return default discount accounting article
	 */
	public String getDefaultDiscountArticleCode() {
		return defaultDiscountArticleCode;
	}
	
	/**
	 * set default discount accounting article
	 * @param defaultDiscountArticleCode
	 */
	public void setDefaultDiscountArticleCode(String defaultDiscountArticleCode) {
		this.defaultDiscountArticleCode = defaultDiscountArticleCode;
	}
	
	/**
	 * 
	 * @return default advanced payment accounting article
	 */
	public String getDefaultAdvancedPaymentArticleCode() {
		return defaultAdvancedPaymentArticleCode;
	}
	
	/**
	 * set default advanced payment accounting article
	 * @param defaultAdvancedPaymentArticleCode
	 */
	public void setDefaultAdvancedPaymentArticleCode(String defaultAdvancedPaymentArticleCode) {
		this.defaultAdvancedPaymentArticleCode = defaultAdvancedPaymentArticleCode;
	}
	
	/**
	 * 
	 * @return default invoice minimum accounting article
	 */
	public String getDefaultInvoiceMinimumArticleCode() {
		return defaultInvoiceMinimumArticleCode;
	}
	
	/**
	 * set default invoice minimum accounting article
	 * @param defaultInvoiceMinimumArticleCode
	 */
	public void setDefaultInvoiceMinimumArticleCode(String defaultInvoiceMinimumArticleCode) {
		this.defaultInvoiceMinimumArticleCode = defaultInvoiceMinimumArticleCode;
	}

}