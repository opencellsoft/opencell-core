package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;
import org.meveo.model.crm.Provider;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ExportIdentifier({ "provider" })
@Table(name = "BILLING_INVOICE_CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = { "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_CONFIGURATION_SEQ")
public class InvoiceConfiguration implements Serializable, IEntity {

    private static final long serialVersionUID = -735961368678724497L;
    
    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    @Column(name = "ID")
    @Access(AccessType.PROPERTY)
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Integer version;

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
    
    @Column(name = "DISPLAY_CHARGES_PERIODS")
    private Boolean displayChargesPeriods = false;
    
    @Column(name = "DISPLAY_BILLING_CYCLE")
    private Boolean displayBillingCycle = false;

    @OneToOne
    @JoinColumn(name="PROVIDER_ID")
    private Provider provider;

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

	public Boolean getDisplayChargesPeriods() {
		return displayChargesPeriods;
	}

	public void setDisplayChargesPeriods(Boolean displayChargesPeriods) {
		this.displayChargesPeriods = displayChargesPeriods;
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
				+ "displayPricePlans=" + displayPricePlans + ", displayEdrs=" + displayEdrs + ", displayProvider=" + displayProvider + ", "
				+ "displayDetail=" + displayDetail + ", displayCfAsXML=" + displayCfAsXML + ", displayChargesPeriods=" + displayChargesPeriods + "]"+ ", displayBillingCycle=" + displayBillingCycle + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public boolean isTransient() {
		return id==null;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
