package org.meveo.api.dto.invoice;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.billing.InvoiceConfiguration;

/**
 * @author Mohamed Hamidi
 **/
@XmlRootElement(name = "InvoiceConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceConfigurationDto implements Serializable {

	private static final long serialVersionUID = -3317673469040337077L;
	
	private Boolean displaySubscriptions ;
	private Boolean displayServices;
	private Boolean displayOffers;
	private Boolean displayEdrs;
	private Boolean displayProvider;
	private Boolean displayCfAsXML;
	private Boolean displayPricePlans;
	private Boolean displayDetail;
	private Boolean displayChargesPeriods;
	private Boolean displayBillingCycle;
	private Boolean displayFreeTransacInInvoice;
	
	public InvoiceConfigurationDto() {}
	
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
		displayBillingCycle=invoiceConfiguration.getDisplayBillingCycle();
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

	public Boolean getDisplayCfAsXML() {
		return displayCfAsXML;
	}

	public void setDisplayCfAsXML(Boolean displayCfAsXML) {
		this.displayCfAsXML = displayCfAsXML;
	}

	public Boolean getDisplayPricePlans() {
		return displayPricePlans;
	}

	public void setDisplayPricePlans(Boolean displayPricePlans) {
		this.displayPricePlans = displayPricePlans;
	}

	public Boolean getDisplayDetail() {
		return displayDetail;
	}

	public void setDisplayDetail(Boolean displayDetail) {
		this.displayDetail = displayDetail;
	}

	public Boolean getDisplayChargesPeriods() {
		return displayChargesPeriods;
	}

	public void setDisplayChargesPeriods(Boolean displayChargesPeriods) {
		this.displayChargesPeriods = displayChargesPeriods;
	}
	public Boolean getDisplayFreeTransacInInvoice() {
		return displayFreeTransacInInvoice;
	}

	public void setDisplayFreeTransacInInvoice(Boolean displayFreeTransacInInvoice) {
		this.displayFreeTransacInInvoice = displayFreeTransacInInvoice;
	}	

	public Boolean getDisplayBillingCycle() {
		return displayBillingCycle;
	}

	public void setDisplayBillingCycle(Boolean displayBillingCycle) {
		this.displayBillingCycle = displayBillingCycle;
	}

	@Override
	public String toString() {
		return "InvoiceConfigurationDto [displaySubscriptions=" + displaySubscriptions
				+ ", displayServices=" + displayServices + ", displayOffers="
				+ displayOffers + ", displayEdrs=" + displayEdrs+ ", displayPricePlans=" + displayPricePlans
				+ ", displayCfAsXML=" + displayCfAsXML
				+ ", displayProvider=" + displayProvider
				+ ", displayDetail=" + displayDetail 
				+ ", displayChargesPeriods=" + displayChargesPeriods+", displayFreeTransacInInvoice=" + displayFreeTransacInInvoice+", displayBillingCycle=" + displayBillingCycle;
	}
	
}
