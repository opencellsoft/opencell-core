package org.meveo.model.catalog;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;

/**
 * Offer template to product, included in an offer, mapping
 * 
 * @author Edward P. Legaspi
 */
@Entity
@Cacheable
@ExportIdentifier({ "offerTemplate.code", "offerTemplate.validity.from", "offerTemplate.validity.to", "productTemplate.code", "productTemplate.validity.from",
        "productTemplate.validity.to" })
@Table(name = "cat_offer_product_template")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_offer_product_template_seq"), })
public class OfferProductTemplate implements IEntity, Serializable {

    private static final long serialVersionUID = -3681938016130405800L;

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
    private OfferTemplate offerTemplate;

    /**
     * Product template
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "product_template_id")
    private ProductTemplate productTemplate;

    /**
     * Is product purchase mandatory
     */
    @Type(type = "numeric_boolean")
    @Column(name = "mandatory")
    private boolean mandatory;

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.intValue();
        }
        int result = 961 + ((offerTemplate == null) ? 0 : offerTemplate.getId().hashCode());
        result = 31 * result + ((productTemplate == null) ? 0 : productTemplate.getId().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof OfferProductTemplate)) {
            return false;
        }

        OfferProductTemplate other = (OfferProductTemplate) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        ProductTemplate thatProductTemplate = other.getProductTemplate();
        if (productTemplate == null) {
            if (thatProductTemplate != null) {
                return false;
            }
        } else if (!productTemplate.equals(thatProductTemplate)) {
            return false;
        }

        OfferTemplate thatOfferTemplate = other.getOfferTemplate();
        if (offerTemplate == null) {
            if (thatOfferTemplate != null) {
                return false;
            }
        } else if (!offerTemplate.equals(thatOfferTemplate)) {
            return false;
        }

        return true;
    }

}
