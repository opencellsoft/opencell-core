package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "offerTemplate.code", "serviceTemplate.code", "provider" })
@Table(name = "CAT_OFFER_SERV_TEMPLATES", uniqueConstraints = @UniqueConstraint(columnNames = { "OFFER_TEMPLATE_ID", "SERVICE_TEMPLATE_ID", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_SERV_TEMPLT_SEQ")
public class OfferServiceTemplate extends BaseEntity {

    private static final long serialVersionUID = -1872859127097329926L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OFFER_TEMPLATE_ID")
    @NotNull
    private OfferTemplate offerTemplate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SERVICE_TEMPLATE_ID")
    @NotNull
    private ServiceTemplate serviceTemplate;

    @Column(name = "MANDATORY")
    private boolean mandatory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "CAT_OFFER_SERV_INCOMP", joinColumns = @JoinColumn(name = "OFFER_SERVICE_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"))
    private List<ServiceTemplate> incompatibleServices = new ArrayList<>();
    
    @Column(name="VALID_FROM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;
	
	@Column(name="VALID_TO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public ServiceTemplate getServiceTemplate() {
        return serviceTemplate;
    }

    public void setServiceTemplate(ServiceTemplate serviceTemplate) {
        this.serviceTemplate = serviceTemplate;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public List<ServiceTemplate> getIncompatibleServices() {
        return incompatibleServices;
    }

    public void setIncompatibleServices(List<ServiceTemplate> incompatibleServices) {
        this.incompatibleServices = incompatibleServices;
    }

    public void addIncompatibleServiceTemplate(ServiceTemplate serviceTemplate) {
        if (getIncompatibleServices() == null) {
            incompatibleServices = new ArrayList<ServiceTemplate>();
        }
        incompatibleServices.add(serviceTemplate);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * 1; // super.hashCode();
        result = prime * result + ((incompatibleServices == null) ? 0 : incompatibleServices.hashCode());
        result = prime * result + ((offerTemplate == null) ? 0 : offerTemplate.hashCode());
        result = prime * result + ((serviceTemplate == null) ? 0 : serviceTemplate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof BusinessEntity)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        OfferServiceTemplate other = (OfferServiceTemplate) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            // return true;
        }

        if (offerTemplate != null) {
            if (!offerTemplate.equals(other.getOfferTemplate())) {
                return false;
            }
        } else if (other.getOfferTemplate() != null) {
            return false;
        }

        if (serviceTemplate != null) {
            if (!serviceTemplate.equals(other.getServiceTemplate())) {
                return false;
            }
        } else if (other.getServiceTemplate() != null) {
            return false;
        }
        return true;
    }

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
}
