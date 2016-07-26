package org.meveo.model.catalog;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.crm.BusinessAccountModel;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_OFFER_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_TEMPLATE_SEQ")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class ProductOffering extends BusinessCFEntity {

    private static final long serialVersionUID = 6877386866687396135L;

    @Column(name = "NAME", length = 100)
    @Size(max = 100)
    private String name;

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_TMPL_CAT", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_TEMPLATE_CAT_ID"))
    @OrderColumn(name = "INDX")
    private List<OfferTemplateCategory> offerTemplateCategories;

    @Column(name = "VALID_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name = "VALID_TO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;

    @Column(name = "image")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private Blob image;

    @Column(name = "IMAGE_CONTENT_TYPE", length = 50)
    @Size(max = 50)
    private String imageContentType;

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_DIGITAL_RES", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "DIGITAL_RESOURCE_ID"))
    @OrderColumn(name = "INDX")
    private List<DigitalResource> attachments;

    @Enumerated(EnumType.STRING)
    @Column(name = "LIFE_CYCLE_STATUS")
    private LifeCycleStatusEnum lifeCycleStatus;

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_BAM", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "BAM_ID"))
    @OrderColumn(name = "INDX")
    private List<BusinessAccountModel> businessAccountModels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public byte[] getImageAsByteArr() {
        if (image != null) {
            int blobLength;
            try {
                blobLength = (int) image.length();
                byte[] blobAsBytes = image.getBytes(1, blobLength);

                return blobAsBytes;
            } catch (SQLException e) {
                return null;
            }
        }

        return null;
    }

    public LifeCycleStatusEnum getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(LifeCycleStatusEnum lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public List<DigitalResource> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DigitalResource> attachments) {
        this.attachments = attachments;
    }

    public List<OfferTemplateCategory> getOfferTemplateCategories() {
        return offerTemplateCategories;
    }

    public void setOfferTemplateCategories(List<OfferTemplateCategory> offerTemplateCategories) {
        this.offerTemplateCategories = offerTemplateCategories;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getNameOrCode() {
        if (!StringUtils.isBlank(name)) {
            return name;
        } else {
            return code;
        }
    }

    public List<BusinessAccountModel> getBusinessAccountModels() {
        return businessAccountModels;
    }

    public void setBusinessAccountModels(List<BusinessAccountModel> businessAccountModels) {
        this.businessAccountModels = businessAccountModels;
    }
}