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

package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;

/**
 * Invoicing configuration
 * 
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "provider" })
@Cacheable
@Table(name = "billing_invoice_configuration")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_configuration_seq"), })
public class InvoiceConfiguration extends BaseEntity implements Serializable, IEntity {

    private static final long serialVersionUID = -735961368678724497L;

    /**
     * Should subscriptions be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_subscriptions")
    private Boolean displaySubscriptions = false;

    /**
     * Should services be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_services")
    private Boolean displayServices = false;

    /**
     * Should offers be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_offers")
    private Boolean displayOffers = false;

    /**
     * Should price plans be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_priceplans")
    private Boolean displayPricePlans = false;

    /**
     * Should EDRs be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_edrs")
    private Boolean displayEdrs = false;

    /**
     * Should provider information be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_provider")
    private Boolean displayProvider = false;

    /**
     * Should subcategory aggregates be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_detail")
    private Boolean displayDetail = true;

    /**
     * Should custom field values be displayed in the XML invoice in XML or JSON format
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_cf_as_xml")
    private Boolean displayCfAsXML = false;


    /**
     * Should Billing cycle be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_billing_cycle")
    private Boolean displayBillingCycle = false;

    /**
     * Should orders be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_orders")
    private Boolean displayOrders = false;

    /**
     * Next to be assigned invoice number
     */
    @Column(name = "current_invoice_nb")
    private Long currentInvoiceNb = 0L;

    /**
     * Should wallet operations be displayed in the XML invoice
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display_wallet_operations")
    private Boolean displayWalletOperations = false;

    
    public Boolean getDisplayWalletOperations() {
		return displayWalletOperations;
	}

	public void setDisplayWalletOperations(Boolean displayWalletOperations) {
		this.displayWalletOperations = displayWalletOperations;
	}

	public Boolean getDisplaySubscriptions() {
        return displaySubscriptions;
    }

    public void setDisplaySubscriptions(Boolean displaySubscriptions) {
        this.displaySubscriptions = displaySubscriptions;
    }

    public Boolean getDisplayServices() {
        return displayServices;
    }

    public void setDisplayServices(Boolean displayServices) {
        this.displayServices = displayServices;
    }

    public Boolean getDisplayOffers() {
        return displayOffers;
    }

    public void setDisplayOffers(Boolean displayOffers) {
        this.displayOffers = displayOffers;
    }

    public Boolean getDisplayPricePlans() {
        return displayPricePlans;
    }

    public void setDisplayPricePlans(Boolean displayPricePlans) {
        this.displayPricePlans = displayPricePlans;
    }

    public Boolean getDisplayEdrs() {
        return displayEdrs;
    }

    public void setDisplayEdrs(Boolean displayEdrs) {
        this.displayEdrs = displayEdrs;
    }

    public Boolean getDisplayProvider() {
        return displayProvider;
    }

    public void setDisplayProvider(Boolean displayProvider) {
        this.displayProvider = displayProvider;
    }

    public Boolean getDisplayDetail() {
        return displayDetail;
    }

    public void setDisplayDetail(Boolean displayDetail) {
        this.displayDetail = displayDetail;
    }

    public Boolean getDisplayCfAsXML() {
        return displayCfAsXML;
    }

    public void setDisplayCfAsXML(Boolean displayCfAsXML) {
        this.displayCfAsXML = displayCfAsXML;
    }



    public Boolean getDisplayBillingCycle() {
        return displayBillingCycle;
    }

    public void setDisplayBillingCycle(Boolean displayBillingCycle) {
        this.displayBillingCycle = displayBillingCycle;
    }

    @Override
    public String toString() {
        return "InvoiceConfiguration [displaySubscriptions=" + displaySubscriptions + ", displayServices=" + displayServices + ", displayOffers=" + displayOffers + ", "
                + "displayPricePlans=" + displayPricePlans + ", displayEdrs=" + displayEdrs + ", displayProvider=" + displayProvider + ", " + "displayDetail=" + displayDetail
                + ", displayCfAsXML=" + displayCfAsXML + ", displayWalletOperations=" + displayWalletOperations + ", displayBillingCycle=" + displayBillingCycle + ",displayOrders="
                + displayOrders + "]";
    }

    /**
     * @return the displayOrders
     */
    public Boolean getDisplayOrders() {
        return displayOrders;
    }

    /**
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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof InvoiceConfiguration)) {
            return false;
        }

        InvoiceConfiguration other = (InvoiceConfiguration) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        // Always return true as there can be only one record of invoice configuration
        return true;
    }

}