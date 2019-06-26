package org.meveo.model.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;

/**
 * Offer template to service template, included in the offer, mapping
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@ExportIdentifier({ "offerTemplate.code", "offerTemplate.validity.from", "offerTemplate.validity.to", "serviceTemplate.code" })
@Table(name = "cat_offer_serv_templates")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_offer_serv_templt_seq"), })
public class OfferServiceTemplate implements IEntity, Serializable {

    private static final long serialVersionUID = 3932032300922665293L;

    /**
     * Identifier
     */
    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    /**
     * Offer template
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_template_id")
    @NotNull
    private OfferTemplate offerTemplate;

    /**
     * Service template
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    @JoinColumn(name = "service_template_id")
    @NotNull
    private ServiceTemplate serviceTemplate;

    /**
     * Is service mandatory
     */
    @Type(type = "numeric_boolean")
    @Column(name = "mandatory")
    private boolean mandatory;

    /**
     * A list of incompatible services with a given service
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_offer_serv_incomp", joinColumns = @JoinColumn(name = "offer_service_template_id"), inverseJoinColumns = @JoinColumn(name = "service_template_id"))
    private List<ServiceTemplate> incompatibleServices = new ArrayList<>();

    /**
     * Service inclusion validity period
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity = new DatePeriod();

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

        int result = 961 + ((incompatibleServices == null) ? 0 : incompatibleServices.hashCode());
        result = 31 * result + ((offerTemplate == null) ? 0 : offerTemplate.hashCode());
        result = 31 * result + ((serviceTemplate == null) ? 0 : serviceTemplate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OfferServiceTemplate)) {
            return false;
        }

        OfferServiceTemplate other = (OfferServiceTemplate) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
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

    public DatePeriod getValidity() {
        if (validity == null) {
            validity = new DatePeriod();
        }
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    /**
     * Update OfferServiceTemplate properties with properties of another OfferServiceTemplate
     * 
     * @param otherOst Other OfferServiceTemplate, to copy properties from
     */
    public void update(OfferServiceTemplate otherOst) {

        setMandatory(otherOst.isMandatory());
        setValidity(otherOst.getValidity());
        setIncompatibleServices(otherOst.getIncompatibleServices());
    }

    public OfferServiceTemplate duplicate(OfferTemplate newOfferTemplate) {
        OfferServiceTemplate newOst = new OfferServiceTemplate();
        newOst.setIncompatibleServices(incompatibleServices);
        newOst.setMandatory(mandatory);
        newOst.setServiceTemplate(serviceTemplate);
        newOst.setValidity(validity);
        if (newOfferTemplate != null) {
            newOst.setOfferTemplate(newOfferTemplate);
        }
        return newOst;
    }

    @Override
    public String toString() {
        return "OfferServiceTemplate [id=" + id + ", offerTemplate=" + offerTemplate + ", serviceTemplate=" + serviceTemplate + ", mandatory=" + mandatory
                + ", incompatibleServices=" + incompatibleServices + ", validity=" + validity + "]";
    }
}
