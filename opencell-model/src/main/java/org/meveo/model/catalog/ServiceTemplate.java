/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.catalog;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.hibernate.type.SqlTypes;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.annotation.ImageType;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.tags.Tag;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 * This represents a service that is part of an offer. It contains charges of different types.
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ModuleItem
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "ServiceTemplate")
@ExportIdentifier({ "code" })
@Table(name = "cat_service_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_service_template_seq"), })
@NamedQueries({
        @NamedQuery(name = "serviceTemplate.getNbServiceWithNotOffer", query = "select count(*) from ServiceTemplate s where s.id not in (select serv.serviceTemplate.id from OfferTemplate o join o.offerServiceTemplates serv)"),
        @NamedQuery(name = "serviceTemplate.getServicesWithNotOffer", query = "from ServiceTemplate s where s.id not in (select serv.serviceTemplate.id from OfferTemplate o join o.offerServiceTemplates serv)"),
        @NamedQuery(name = "serviceTemplate.getServicesWithRecurringsByChargeTemplate", query = "from ServiceTemplate s left join s.serviceRecurringCharges c where c.chargeTemplate=:chargeTemplate"),
        @NamedQuery(name = "ServiceTemplate.getMimimumRTUsed", query = "select s.minimumAmountEl from ServiceTemplate s where s.minimumAmountEl is not null"),
        @NamedQuery(name = "ServiceTemplate.findByTags", query = "select s from ServiceTemplate s LEFT JOIN s.tags as tag WHERE tag.code IN (:tagCodes)")
        // @NamedQuery(name = "serviceTemplate.getServicesWithSubscriptionsByChargeTemplate",
        // query = "from ServiceTemplate s left join s.serviceSubscriptionCharges c where c.chargeTemplate=:chargeTemplate"),
        // @NamedQuery(name = "serviceTemplate.getServicesWithTerminationsByChargeTemplate",
        // query = "from ServiceTemplate s left join s.serviceTerminationCharges c where c.chargeTemplate=:chargeTemplate"),
        // @NamedQuery(name = "serviceTemplate.getServicesWithUsagesByChargeTemplate",
        // query = "from ServiceTemplate s left join s.serviceUsageCharges c where c.chargeTemplate=:chargeTemplate")
})
public class ServiceTemplate extends ServiceCharge implements IImageUpload {

    private static final long serialVersionUID = 1L;

    /**
     * Calendar to use when creating Wallet operations. Service subscription start date is taken as calendar's initiation date. Invoicing calendar to calculate if operation should
     * be invoiced on an future date.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoicing_calendar_id")
    private Calendar invoicingCalendar;

    /**
     * Business service model that created this service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_service_model_id")
    private BusinessServiceModel businessServiceModel;

    /**
     * Long description
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "long_description")
    private String longDescription;

    /**
     * Path to an image
     */
    @ImageType
    @Column(name = "image_path", length = 100)
    @Size(max = 100)
    private String imagePath;

    /**
     * Expression to determine minimum amount value
     */
    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;


    /**
     * Corresponding invoice subcategory
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_invoice_sub_category_id")
    private InvoiceSubCategory minimumInvoiceSubCategory;

    /**
     * Corresponding to minimum one shot charge template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_charge_template_id")
    private OneShotChargeTemplate minimumChargeTemplate;

    /**
     * Service renewal configuration
     */
    @Embedded
    private SubscriptionRenewal serviceRenewal = new SubscriptionRenewal();

    /**
     * Selected from a list in GUI
     */
    @Transient
    private boolean selected;

    /**
     * If true, end of agreement date will be extended automatically till subscribedTillDate field
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "auto_end_of_engagement")
    private Boolean autoEndOfEngagement = Boolean.FALSE;

    /**
     * If service is from BSM, it allows us to have a duplicate service template when instantiating BOM.
     */
    @Transient
    private boolean instantiatedFromBSM;

    /**
     * Description to override
     */
    @Transient
    private String descriptionOverride;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;
	

    
    
    /**
     * list of tag attached
     */   
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_service_template_tags", joinColumns = @JoinColumn(name = "service_template_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<Tag>();
    
	/**
	 * list of attributes attached to this product
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
				name = "cpq_service_template_attributes",
				joinColumns = @JoinColumn(name = "service_template_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id")				
			)
    private List<Attribute> attributes = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof ServiceTemplate)) {
            return false;
        }

        ServiceTemplate other = (ServiceTemplate) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (code == null) {
            if (other.getCode() != null)
                return false;
        } else if (!code.equals(other.getCode()))
            return false;
        return true;
    }

    public Calendar getInvoicingCalendar() {
        return invoicingCalendar;
    }

    public void setInvoicingCalendar(Calendar invoicingCalendar) {
        this.invoicingCalendar = invoicingCalendar;
    }

    public BusinessServiceModel getBusinessServiceModel() {
        return businessServiceModel;
    }

    public void setBusinessServiceModel(BusinessServiceModel businessServiceModel) {
        this.businessServiceModel = businessServiceModel;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isInstantiatedFromBSM() {
        return instantiatedFromBSM;
    }

    public void setInstantiatedFromBSM(boolean instantiatedFromBSM) {
        this.instantiatedFromBSM = instantiatedFromBSM;
    }

    public String getDescriptionOverride() {
        return descriptionOverride;
    }

    public void setDescriptionOverride(String descriptionOverride) {
        this.descriptionOverride = descriptionOverride;
    }

    /**
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }
    
    /**
     * @return the minimumInvoiceSubCategory
     */
    public InvoiceSubCategory getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * @param minimumInvoiceSubCategory the minimumInvoiceSubCategory to set
     */
    public void setMinimumInvoiceSubCategory(InvoiceSubCategory minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }

    /**
     * Gets the charge template used in minimum amount.
     *
     * @return a one Shot Charge template
     */
    public OneShotChargeTemplate getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    /**
     * Sets the minimum amount charge template.
     *
     * @param minimumChargeTemplate a one Shot Charge template
     */
    public void setMinimumChargeTemplate(OneShotChargeTemplate minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }


    public SubscriptionRenewal getServiceRenewal() {
        return serviceRenewal;
    }

    public void setServiceRenewal(SubscriptionRenewal serviceRenewal) {
        this.serviceRenewal = serviceRenewal;
    }

    /**
     * @return the autoEndOfEngagement
     */
    public Boolean getAutoEndOfEngagement() {
        return autoEndOfEngagement;
    }

    /**
     * @param autoEndOfEngagement the autoEndOfEngagement to set
     */
    public void setAutoEndOfEngagement(Boolean autoEndOfEngagement) {
        this.autoEndOfEngagement = autoEndOfEngagement;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }


    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.description);
        } else {
            return this.description;
        }
    }


	
	/**
	 * @return the tags
	 */
	public Set<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	/**
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}


	
	
    
    
}

