package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.VersionedEntity;
import org.meveo.model.annotation.ImageType;
import org.meveo.model.crm.BusinessAccountModel;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ModuleItem
@ObservableEntity
@VersionedEntity
@MultilanguageEntity(key = "menu.catalog.offersAndProducts", group = "ProductOffering")
@ExportIdentifier({ "code", "validity.from", "validity.to" })
@Table(name = "cat_offer_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "valid_from", "valid_to" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_offer_template_seq"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({
        @NamedQuery(name = "ProductOffering.findLatestVersion", query = "select e from ProductOffering e where type(e)= :clazz and e.code = :code order by e.validity.from desc, e.validity.to desc"),
        @NamedQuery(name = "ProductOffering.findMatchingVersions", query = "select e from ProductOffering e where type(e)= :clazz and e.code = :code and e.id !=:id order by id"),
        @NamedQuery(name = "ProductOffering.findActiveByDate", query = "select e from ProductOffering e where type(e)= :clazz and ((e.validity.from IS NULL and e.validity.to IS NULL) or (e.validity.from<=:date and :date<e.validity.to) or (e.validity.from<=:date and e.validity.to IS NULL) or (e.validity.from IS NULL and :date<e.validity.to))") })
public abstract class ProductOffering extends BusinessCFEntity implements IImageUpload {

    private static final long serialVersionUID = 6877386866687396135L;

    @Column(name = "name", length = 100)
    @Size(max = 100)
    private String name;

    @ManyToMany
    @JoinTable(name = "cat_product_offer_tmpl_cat", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "offer_template_cat_id"))
    @OrderColumn(name = "INDX")
    private List<OfferTemplateCategory> offerTemplateCategories = new ArrayList<>();

    @AttributeOverrides({ @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity = new DatePeriod();

    @ImageType
    @Column(name = "image_path", length = 100)
    @Size(max = 100)
    private String imagePath;

    @ManyToMany
    @JoinTable(name = "cat_product_offer_digital_res", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "digital_resource_id"))
    @OrderColumn(name = "INDX")
    private List<DigitalResource> attachments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "life_cycle_status")
    private LifeCycleStatusEnum lifeCycleStatus;

    @ManyToMany
    @JoinTable(name = "cat_product_offer_bam", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "bam_id"))
    @OrderColumn(name = "INDX")
    private List<BusinessAccountModel> businessAccountModels = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "cat_product_offer_channels", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "channel_id"))
    @OrderColumn(name = "INDX")
    private List<Channel> channels = new ArrayList<Channel>();;

    public void addOfferTemplateCategory(OfferTemplateCategory offerTemplateCategory) {
        if (getOfferTemplateCategories() == null) {
            offerTemplateCategories = new ArrayList<>();
        }
        if (!offerTemplateCategories.contains(offerTemplateCategory)) {
            offerTemplateCategories.add(offerTemplateCategory);
        }
    }

    public void addAttachment(DigitalResource attachment) {
        if (getAttachments() == null) {
            attachments = new ArrayList<>();
        }
        if (!attachments.contains(attachment)) {
            attachments.add(attachment);
        }
    }

    public void addBusinessAccountModel(BusinessAccountModel businessAccountModel) {
        if (getBusinessAccountModels() == null) {
            businessAccountModels = new ArrayList<>();
        }
        if (!businessAccountModels.contains(businessAccountModel)) {
            businessAccountModels.add(businessAccountModel);
        }
    }

    public void addChannel(Channel channel) {
        if (getChannels() == null) {
            channels = new ArrayList<>();
        }
        if (!channels.contains(channel)) {
            channels.add(channel);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public DatePeriod getValidityRaw() {
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    /**
     * If validity is null (both dates are empty) then instantiate one. Note: Use it with care as it results in update calls to DB if validity was null before. Preferably use
     * getValidityRaw() and check for null.
     * 
     * @return Existing or instantiated new validity
     */
    public DatePeriod getValidity() {

        if (validity == null) {
            validity = new DatePeriod();
        }
        return validity;
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

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ProductOffering)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        ProductOffering other = (ProductOffering) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
             return true;
        }
        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }

        if (validity != null && !validity.equals(other.getValidityRaw())) {
            return false;
        } else if (validity == null && (other.getValidityRaw() != null && !other.getValidityRaw().isEmpty())) {
            return false;
        }

        return true;
    }
}