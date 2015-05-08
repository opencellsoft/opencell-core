package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "BILLING_INVOICE_CONFIGURATION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_CONFIGURATION_SEQ")
public class InvoiceConfiguration extends BusinessEntity {

	private static final long serialVersionUID = -735961368678724497L;

	@Column(name = "DISPLAY_SUBSCRIPTIONS")
	private Boolean displaySubscriptions = false;

	@Column(name = "DISPLAY_SERVICES")
	private Boolean displayServices = false;

	@Column(name = "DISPLAY_OFFERS")
	private Boolean displayOffers = false;

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

}
