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
    
    private Long currentInvoiceNb = 0L;

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
        displaySubscriptions = invoiceConfiguration.getDisplaySubscriptions();
        displayServices = invoiceConfiguration.getDisplayServices();
        displayOffers = invoiceConfiguration.getDisplayOffers();
        displayEdrs = invoiceConfiguration.getDisplayEdrs();
        displayProvider = invoiceConfiguration.getDisplayProvider();
        displayCfAsXML = invoiceConfiguration.getDisplayCfAsXML();
        displayPricePlans = invoiceConfiguration.getDisplayPricePlans();
        displayDetail = invoiceConfiguration.getDisplayDetail();
        displayChargesPeriods = invoiceConfiguration.getDisplayChargesPeriods();
        displayBillingCycle = invoiceConfiguration.getDisplayBillingCycle();
        displayOrders = invoiceConfiguration.getDisplayOrders();
        currentInvoiceNb = invoiceConfiguration.getCurrentInvoiceNb();
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

    @Override
    public String toString() {
        return "InvoiceConfigurationDto [displaySubscriptions=" + displaySubscriptions + ", displayServices=" + displayServices + ", displayOffers=" + displayOffers
                + ", displayEdrs=" + displayEdrs + ", displayPricePlans=" + displayPricePlans + ", displayCfAsXML=" + displayCfAsXML + ", displayProvider=" + displayProvider
                + ", displayDetail=" + displayDetail + ", displayChargesPeriods=" + displayChargesPeriods + ", displayFreeTransacInInvoice=" + displayFreeTransacInInvoice
                + ", displayBillingCycle=" + displayBillingCycle + ",displayOrders=" + displayOrders + ",currentInvoiceNb="+currentInvoiceNb+"]";
    }
}