package org.meveo.model.catalog;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.IEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Cacheable
@ExportIdentifier({ "bundleTemplate.code", "bundleTemplate.validity.from", "bundleTemplate.validity.to", "productTemplate.code", "productTemplate.validity.from",
        "productTemplate.validity.to" })
@Table(name = "cat_bundle_product_template", uniqueConstraints = @UniqueConstraint(columnNames = { "product_template_id", "bundle_template_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_bundle_product_template_seq"), })
public class BundleProductTemplate implements IEntity {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY)
    protected Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_template_id")
    @NotNull
    private ProductTemplate productTemplate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bundle_template_id")
    @NotNull
    private BundleTemplate bundleTemplate;

    @Column(name = "quantity")
    private int quantity;

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

    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
    }

    public BundleTemplate getBundleTemplate() {
        return bundleTemplate;
    }

    public void setBundleTemplate(BundleTemplate bundleTemplate) {
        this.bundleTemplate = bundleTemplate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        if (id != null)
            return id.intValue();

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bundleTemplate == null) ? 0 : bundleTemplate.getId().hashCode());
        result = prime * result + ((productTemplate == null) ? 0 : productTemplate.getId().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof BundleProductTemplate)) {
            return false;
        }

        BundleProductTemplate that = (BundleProductTemplate) obj;

        ProductTemplate thatProductTemplate = that.getProductTemplate();
        if (productTemplate == null && thatProductTemplate != null) {
            return false;
        } else if (!productTemplate.equals(thatProductTemplate)) {
            return false;
        }

        BundleTemplate thatBundleTemplate = that.getBundleTemplate();
        if (bundleTemplate == null && thatBundleTemplate != null) {
            return false;
        } else if (!bundleTemplate.equals(thatBundleTemplate)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "[BundleTemplate = " + this.bundleTemplate + ", ProductTemplate = " + this.productTemplate + "]";
    }

}
