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
import org.meveo.model.annotation.ImageType;
import org.meveo.model.crm.BusinessAccountModel;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ModuleItem
@ObservableEntity
@MultilanguageEntity(key = "menu.catalog.offersAndProducts", group = "ProductOffering")
@ExportIdentifier({ "code", "validity.startDate", "validity.endDate" })
@Table(name = "CAT_OFFER_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "VALID_FROM", "VALID_TO" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "CAT_OFFER_TEMPLATE_SEQ"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({ @NamedQuery(name = "ProductOffering.findLatestVersion", query = "select e from ProductOffering e where e.code = :code order by e.validity.from desc")})
public abstract class ProductOffering extends BusinessCFEntity implements IImageUpload {

    private static final long serialVersionUID = 6877386866687396135L;

    @Column(name = "NAME", length = 100)
    @Size(max = 100)
    private String name;

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_TMPL_CAT", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_TEMPLATE_CAT_ID"))
    @OrderColumn(name = "INDX")
    private List<OfferTemplateCategory> offerTemplateCategories = new ArrayList<>();

    @AttributeOverrides({ @AttributeOverride(name = "from", column = @Column(name = "VALID_FROM")),
            @AttributeOverride(name = "to", column = @Column(name = "VALID_TO")) })
    private DatePeriod validity = new DatePeriod();

    @ImageType
    @Column(name = "IMAGE_PATH", length = 100)
    @Size(max = 100)
    private String imagePath;

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_DIGITAL_RES", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "DIGITAL_RESOURCE_ID"))
    @OrderColumn(name = "INDX")
    private List<DigitalResource> attachments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "LIFE_CYCLE_STATUS")
    private LifeCycleStatusEnum lifeCycleStatus;

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_BAM", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "BAM_ID"))
    @OrderColumn(name = "INDX")
    private List<BusinessAccountModel> businessAccountModels = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "CAT_PRODUCT_OFFER_CHANNELS", joinColumns = @JoinColumn(name = "PRODUCT_ID"), inverseJoinColumns = @JoinColumn(name = "CHANNEL_ID"))
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

    public DatePeriod getValidity() {
        if (validity == null) {
            validity = new DatePeriod();
        }
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
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
}