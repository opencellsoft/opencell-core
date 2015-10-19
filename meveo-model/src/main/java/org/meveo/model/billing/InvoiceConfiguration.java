package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "BILLING_INVOICE_CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_CONFIGURATION_SEQ")
public class InvoiceConfiguration extends BusinessEntity {

    private static final long serialVersionUID = -735961368678724497L;

    @Column(name = "DISPLAY_SUBSCRIPTIONS")
    private Boolean displaySubscriptions = false;

    @Column(name = "DISPLAY_SERVICES")
    private Boolean displayServices = false;

    @Column(name = "DISPLAY_OFFERS")
    private Boolean displayOffers = false;
    
    @Column(name = "DISPLAY_EDRS")
    private Boolean displayEdrs = false;
    
    @Column(name = "DISPLAY_PROVIDER")
    private Boolean displayProvider = false;

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
	
	
    
    

}
