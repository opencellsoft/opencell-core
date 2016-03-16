package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "provider" })
@Table(name = "BILLING_INVOICE_CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = { "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_CONFIGURATION_SEQ")
public class InvoiceConfiguration extends BaseEntity {

    private static final long serialVersionUID = -735961368678724497L;

    @Column(name = "DISPLAY_SUBSCRIPTIONS")
    private Boolean displaySubscriptions = false;

    @Column(name = "DISPLAY_SERVICES")
    private Boolean displayServices = false;

    @Column(name = "DISPLAY_OFFERS")
    private Boolean displayOffers = false;
    
    @Column(name = "DISPLAY_PRICEPLANS")
    private Boolean displayPricePlans = false;

    @Column(name = "DISPLAY_EDRS")
    private Boolean displayEdrs = false;

    @Column(name = "DISPLAY_PROVIDER")
    private Boolean displayProvider = false;

    @Column(name = "DISPLAY_DETAIL")
    private Boolean displayDetail = true;

    @Column(name = "DISPLAY_CF_AS_XML")
    private Boolean displayCfAsXML = false;

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

}
