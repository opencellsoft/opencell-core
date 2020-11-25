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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.annotation.ImageType;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.cpq.AccountingArticle;
import org.meveo.model.cpq.GroupedService;
import org.meveo.model.cpq.ServiceType;
import org.meveo.model.cpq.tags.Tag;

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
public class ServiceTemplate extends EnableBusinessCFEntity implements IImageUpload {

    private static final long serialVersionUID = 1L;

    /**
     * Mapping between service and recurring charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ServiceChargeTemplateRecurring> serviceRecurringCharges = new ArrayList<>();

    /**
     * Mapping between service and subscription charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges = new ArrayList<>();

    /**
     * Mapping between service and termination charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ServiceChargeTemplateTermination> serviceTerminationCharges = new ArrayList<>();

    /**
     * Mapping between service and usage charges
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ServiceChargeTemplateUsage> serviceUsageCharges = new ArrayList<>();

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
    @Size(max = 2000)
    @Column(name = "long_description", columnDefinition = "TEXT")
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
     * Expression to determine minimum amount value - for Spark
     */
    @Column(name = "minimum_amount_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    @Column(name = "minimum_label_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumLabelElSpark;

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
    @Type(type = "numeric_boolean")
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
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;
	
	
	/**
	 * the grouped service
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grouped_service_id", referencedColumnName = "id")
	private GroupedService groupedService;
	
	 
	
	
	/**
	 * service type
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name =  "service_type_id", referencedColumnName = "id")
	@NotNull
	private ServiceType serviceType;
	
	
	  /**
     * Mandatory
     */
    @Type(type = "numeric_boolean")
    @Column(name = "mandatory")
    @NotNull
    protected Boolean mandatory=Boolean.FALSE;
    
    
    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority", columnDefinition = "int DEFAULT 0")
    private Integer priority = 0;
     
    
    @Column(name = "param", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param;
    
    
    /**
     * list of tag attached
     */   
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_service_template_tags", joinColumns = @JoinColumn(name = "service_template_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<Tag>();
    
    
    
	  /**
     *Indicates whether the service is a material (physical) component of the product or not
     */
    @Type(type = "numeric_boolean")
    @Column(name = "material")
    @NotNull
    protected Boolean material=false;
    
    
    /**
     * Accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_article_id",referencedColumnName = "id")
    private AccountingArticle accountingArticle;
    

    @Column(name = "sequence")
    protected Integer sequence;

	  /**
     * Display
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display")
    @NotNull
    protected Boolean display;
    
    
    public ServiceChargeTemplateRecurring getServiceRecurringChargeByChargeCode(String chargeCode) {
        ServiceChargeTemplateRecurring result = null;
        for (ServiceChargeTemplateRecurring sctr : serviceRecurringCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateRecurring> getServiceRecurringCharges() {
        return serviceRecurringCharges;
    }

    public void setServiceRecurringCharges(List<ServiceChargeTemplateRecurring> serviceRecurringCharges) {
        this.serviceRecurringCharges = serviceRecurringCharges;
    }

    public ServiceChargeTemplateSubscription getServiceChargeTemplateSubscriptionByChargeCode(String chargeCode) {
        ServiceChargeTemplateSubscription result = null;
        for (ServiceChargeTemplateSubscription sctr : serviceSubscriptionCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateSubscription> getServiceSubscriptionCharges() {
        return serviceSubscriptionCharges;
    }

    public void setServiceSubscriptionCharges(List<ServiceChargeTemplateSubscription> serviceSubscriptionCharges) {
        this.serviceSubscriptionCharges = serviceSubscriptionCharges;
    }

    public ServiceChargeTemplateTermination getServiceChargeTemplateTerminationByChargeCode(String chargeCode) {
        ServiceChargeTemplateTermination result = null;
        for (ServiceChargeTemplateTermination sctr : serviceTerminationCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateTermination> getServiceTerminationCharges() {
        return serviceTerminationCharges;
    }

    public void setServiceTerminationCharges(List<ServiceChargeTemplateTermination> serviceTerminationCharges) {
        this.serviceTerminationCharges = serviceTerminationCharges;
    }

    public ServiceChargeTemplateUsage getServiceChargeTemplateUsageByChargeCode(String chargeCode) {
        ServiceChargeTemplateUsage result = null;
        for (ServiceChargeTemplateUsage sctr : serviceUsageCharges) {
            if (sctr.getChargeTemplate().getCode().equals(chargeCode)) {
                result = sctr;
                break;
            }
        }
        return result;
    }

    public List<ServiceChargeTemplateUsage> getServiceUsageCharges() {
        return serviceUsageCharges;
    }

    public void setServiceUsageCharges(List<ServiceChargeTemplateUsage> serviceUsageCharges) {
        this.serviceUsageCharges = serviceUsageCharges;
    }

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
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    /**
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
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
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
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
	 * @return the serviceType
	 */
	public ServiceType getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	/**
	 * @return the mandatory
	 */
	public Boolean getMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
 

	/**
	 * @return the param
	 */
	public String getParam() {
		return param;
	}

	/**
	 * @param param the param to set
	 */
	public void setParam(String param) {
		this.param = param;
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
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	/**
	 * @return the material
	 */
	public Boolean getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(Boolean material) {
		this.material = material;
	}



	/**
	 * @return the accountingArticle
	 */
	public AccountingArticle getAccountingArticle() {
		return accountingArticle;
	}

	/**
	 * @param accountingArticle the accountingArticle to set
	 */
	public void setAccountingArticle(AccountingArticle accountingArticle) {
		this.accountingArticle = accountingArticle;
	}

	/**
	 * @return the groupedService
	 */
	public GroupedService getGroupedService() {
		return groupedService;
	}

	/**
	 * @param groupedService the groupedService to set
	 */
	public void setGroupedService(GroupedService groupedService) {
		this.groupedService = groupedService;
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the display
	 */
	public Boolean getDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(Boolean display) {
		this.display = display;
	}
    
    
    
    
    
}

