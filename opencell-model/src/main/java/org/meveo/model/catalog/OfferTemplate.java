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
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.QueryHint;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.document.Document;

/**
 * @author Edward P. Legaspi, Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@WorkflowedEntity
@CustomFieldEntity(cftCodePrefix = "OfferTemplate")
@DiscriminatorValue("OFFER")
@NamedQueries({ @NamedQuery(name = "OfferTemplate.countActive", query = "SELECT COUNT(*) FROM OfferTemplate WHERE businessOfferModel is not null and lifeCycleStatus='ACTIVE'"),
        @NamedQuery(name = "OfferTemplate.countDisabled", query = "SELECT COUNT(*) FROM OfferTemplate WHERE businessOfferModel is not null and lifeCycleStatus<>'ACTIVE'"),
        @NamedQuery(name = "OfferTemplate.getMimimumRTUsed", query = "select ot.minimumAmountEl from OfferTemplate ot where ot.minimumAmountEl is not null"),
        @NamedQuery(name = "OfferTemplate.countExpiring", query = "SELECT COUNT(*) FROM OfferTemplate WHERE :nowMinusXDay<validity.to and validity.to<=NOW() and businessOfferModel is not null"),
        @NamedQuery(name = "OfferTemplate.findByServiceTemplate", query = "SELECT t FROM OfferTemplate t JOIN t.offerServiceTemplates ost WHERE ost.serviceTemplate = :serviceTemplate", hints = @QueryHint(name = "org.hibernate.flushMode", value = "COMMIT")),
        @NamedQuery(name = "OfferTemplate.findByTags", query = "select o from OfferTemplate o LEFT JOIN o.tags as tag WHERE tag.code IN (:tagCodes)")
})
public class OfferTemplate extends ProductOffering implements IWFEntity, ISearchable {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_offer_model_id")
    private BusinessOfferModel businessOfferModel;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<>();

    @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OfferProductTemplate> offerProductTemplates = new ArrayList<>();

    /**
     * offer component
     */
    @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferComponent> offerComponents = new ArrayList<>();


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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_offer_tmpl_discount_plan", joinColumns = @JoinColumn(name = "offer_tmpl_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "discount_plan_id", referencedColumnName = "id"))
    private List<DiscountPlan> allowedDiscountPlans = new ArrayList<>();

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

    @Embedded
    private SubscriptionRenewal subscriptionRenewal = new SubscriptionRenewal();

    @Transient
    private String prefix;
//
//    @Transient
//    private Map<ChargeTypeEnum, List<ServiceTemplate>> serviceTemplatesByChargeType;

    @Transient
    private List<ProductTemplate> productTemplates = new ArrayList<>();

    @Transient
    private String transientCode;

    @Type(type = "numeric_boolean")
    @Column(name = "auto_end_of_engagement")
    private Boolean autoEndOfEngagement = Boolean.FALSE;


    /**
     * list of tag attached
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_offer_template_tags", joinColumns = @JoinColumn(name = "offer_template_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags = new ArrayList<>();




	/**
     * list of Media
     */   
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_offer_template_media", joinColumns = @JoinColumn(name = "offer_template_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
    private List<Media> medias = new ArrayList<Media>();

    @OneToMany(mappedBy = "targetOfferTemplate", fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("id")
    private List<CommercialRuleHeader> commercialRules = new ArrayList<>();

    /**
     * date of status : it set automatically when ever the status of offerTemplate is changed
     */
    @Column(name = "status_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;


    @Type(type = "numeric_boolean")
    @Column(name = "is_offer_change_restricted")
    private Boolean isOfferChangeRestricted = Boolean.FALSE;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="offer_template_id")
    private List<OfferTemplate> allowedOffersChange = new ArrayList<>();


    /**
     * Corresponding to minimum invoice AccountingArticle
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_article_id")
    private AccountingArticle minimumArticle;
    
    
    @Type(type = "numeric_boolean")
    @Column(name = "is_model")
    private Boolean isModel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_model_id")
    private OfferTemplate offerModel;
    

    @Type(type = "numeric_boolean")
    @Column(name = "generate_quote_edr_per_product")
    private boolean generateQuoteEdrPerProduct;
    
    /**
    * Display
    */
    @Type(type = "numeric_boolean")
    @Column(name = "display")
    @NotNull
    private boolean display;
    
    /**
     * sequence for Offer Template and attribute
     */
    @Column(name = "sequence")
    protected Integer sequence = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;
    
    /**
     * @return the display
     */
    public boolean isDisplay() {
        return display;
    }
    
    /**
     * @param display the display to set
     */
    public void setDisplay(boolean display) {
        this.display = display;
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "offerTemplate", orphanRemoval = true)
    private List<OfferTemplateAttribute> offerAttributes = new ArrayList<>();

    public List<OfferServiceTemplate> getOfferServiceTemplates() {
        return offerServiceTemplates;
    }

    public void setOfferServiceTemplates(List<OfferServiceTemplate> offerServiceTemplates) {
        this.offerServiceTemplates = offerServiceTemplates;
    }

    public void addOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
        if (getOfferServiceTemplates() == null) {
            offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
        }
        offerServiceTemplate.setOfferTemplate(this);
        offerServiceTemplates.add(offerServiceTemplate);
    }

    public void updateOfferServiceTemplate(OfferServiceTemplate offerServiceTemplate) {
        int index = offerServiceTemplates.indexOf(offerServiceTemplate);
        if (index >= 0) {
            offerServiceTemplates.set(index, offerServiceTemplate);
        }
    }

    public BusinessOfferModel getBusinessOfferModel() {
        return businessOfferModel;
    }

    public void setBusinessOfferModel(BusinessOfferModel businessOfferModel) {
        this.businessOfferModel = businessOfferModel;
    }

    /**
     * Check if offer contains a given service template
     *
     * @param serviceTemplate Service template to match
     * @return True if offer contains a given service template
     */
    public boolean containsServiceTemplate(ServiceTemplate serviceTemplate) {

        for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
            if (offerServiceTemplate.getServiceTemplate().equals(serviceTemplate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if offer contains a given product template
     *
     * @param productTemplate Product template to match
     * @return True if offer contains a given product template
     */
    public boolean containsProductTemplate(ProductTemplate productTemplate) {

        for (OfferProductTemplate offerProductTemplate : offerProductTemplates) {
            if (offerProductTemplate.getProductTemplate().equals(productTemplate)) {
                return true;
            }
        }
        return false;
    }

    public List<OfferProductTemplate> getOfferProductTemplates() {
        return offerProductTemplates;
    }

    public void setOfferProductTemplates(List<OfferProductTemplate> offerProductTemplates) {
        this.offerProductTemplates = offerProductTemplates;
    }

    public void addOfferProductTemplate(OfferProductTemplate offerProductTemplate) {
        if (getOfferProductTemplates() == null) {
            offerProductTemplates = new ArrayList<OfferProductTemplate>();
        }
        offerProductTemplate.setOfferTemplate(this);
        offerProductTemplates.add(offerProductTemplate);
    }

    public void updateOfferProductTemplate(OfferProductTemplate offerProductTemplate) {

        int index = offerProductTemplates.indexOf(offerProductTemplate);
        if (index >= 0) {
            offerProductTemplates.set(index, offerProductTemplate);
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public SubscriptionRenewal getSubscriptionRenewal() {
        return subscriptionRenewal;
    }

    public void setSubscriptionRenewal(SubscriptionRenewal subscriptionRenewal) {
        this.subscriptionRenewal = subscriptionRenewal;
    }

//    @SuppressWarnings("rawtypes")
//    public Map<ChargeTypeEnum, List<ServiceTemplate>> getServiceTemplatesByChargeType() {
//
//        if (serviceTemplatesByChargeType != null) {
//            return serviceTemplatesByChargeType;
//        }
//
//        serviceTemplatesByChargeType = new HashMap<>();
//
//        for (OfferServiceTemplate service : offerServiceTemplates) {
//            List charges = service.getServiceTemplate().getServiceRecurringCharges();
//            if (charges != null && !charges.isEmpty()) {
//                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.RECURRING)) {
//                    serviceTemplatesByChargeType.put(ChargeTypeEnum.RECURRING, new ArrayList<ServiceTemplate>());
//                }
//                serviceTemplatesByChargeType.get(ChargeTypeEnum.RECURRING).add(service.getServiceTemplate());
//            }
//
//            charges = service.getServiceTemplate().getServiceUsageCharges();
//            if (charges != null && !charges.isEmpty()) {
//                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.USAGE)) {
//                    serviceTemplatesByChargeType.put(ChargeTypeEnum.USAGE, new ArrayList<ServiceTemplate>());
//                }
//                serviceTemplatesByChargeType.get(ChargeTypeEnum.USAGE).add(service.getServiceTemplate());
//            }
//
//            charges = service.getServiceTemplate().getServiceSubscriptionCharges();
//            if (charges != null && !charges.isEmpty()) {
//                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.SUBSCRIPTION)) {
//                    serviceTemplatesByChargeType.put(ChargeTypeEnum.SUBSCRIPTION, new ArrayList<ServiceTemplate>());
//                }
//                serviceTemplatesByChargeType.get(ChargeTypeEnum.SUBSCRIPTION).add(service.getServiceTemplate());
//            }
//
//            charges = service.getServiceTemplate().getServiceTerminationCharges();
//            if (charges != null && !charges.isEmpty()) {
//                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.TERMINATION)) {
//                    serviceTemplatesByChargeType.put(ChargeTypeEnum.TERMINATION, new ArrayList<ServiceTemplate>());
//                }
//                serviceTemplatesByChargeType.get(ChargeTypeEnum.TERMINATION).add(service.getServiceTemplate());
//            }
//        }
//
//        return serviceTemplatesByChargeType;
//    }

    public List<ProductTemplate> getProductTemplates() {
        if (productTemplates != null) {
            return productTemplates;
        }

        productTemplates = new ArrayList<>();

        for (OfferProductTemplate prodTemplate : offerProductTemplates) {
            prodTemplate.getProductTemplate().getProductChargeTemplates();
            productTemplates.add(prodTemplate.getProductTemplate());
        }

        return productTemplates;
    }

    public String getTransientCode() {
        return null;
    }

    public void setTransientCode(String transientCode) {
        setCode(transientCode);
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

    public List<DiscountPlan> getAllowedDiscountPlans() {
        return allowedDiscountPlans;
    }

    public void setAllowedDiscountPlans(List<DiscountPlan> allowedDiscountPlans) {
        this.allowedDiscountPlans = allowedDiscountPlans;
    }

    public void addAnAllowedDiscountPlan(DiscountPlan allowedDiscountPlan) {
        if (getAllowedDiscountPlans() == null) {
            this.allowedDiscountPlans = new ArrayList<DiscountPlan>();
        }
        this.allowedDiscountPlans.add(allowedDiscountPlan);
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



	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void setProductTemplates(List<ProductTemplate> productTemplates) {
		this.productTemplates = productTemplates;
	}

	/**
	 * @return the offerComponents
	 */
	public List<OfferComponent> getOfferComponents() {
		return offerComponents;
	}

	/**
	 * @param offerComponents the offerComponents to set
	 */
	public void setOfferComponents(List<OfferComponent> offerComponents) {
		this.offerComponents = offerComponents;
	}

	/**
	 * @return the medias
	 */
	public List<Media> getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(List<Media> medias) {
		this.medias = medias;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the commercialRules
	 */
	public List<CommercialRuleHeader> getCommercialRules() {
		return commercialRules;
	}

	/**
	 * @param commercialRules the commercialRules to set
	 */
	public void setCommercialRules(List<CommercialRuleHeader> commercialRules) {
		this.commercialRules = commercialRules;
	}


    public boolean haveProduct(String productCode) {
	    return offerComponents.stream()
                .filter(off -> off.getProduct() != null)
                .anyMatch(off -> off.getProduct().getCode().equals(productCode));
    }

    /**
     * @return the isOfferChangeRestricted
     */
    public Boolean getIsOfferChangeRestricted() {
        return isOfferChangeRestricted;
    }

    /**
     * @param isOfferChangeRestricted the isOfferChangeRestricted to set
     */
    public void setIsOfferChangeRestricted(Boolean isOfferChangeRestricted) {
        this.isOfferChangeRestricted = isOfferChangeRestricted;
    }

    public List<OfferTemplate> getAllowedOffersChange() {
        return allowedOffersChange;
    }

    public void setAllowedOffersChange(List<OfferTemplate> allowedOffersChange) {
        this.allowedOffersChange = allowedOffersChange;
    }

    public AccountingArticle getMinimumArticle() {
        return minimumArticle;
    }

    public void setMinimumArticle(AccountingArticle minimumArticle) {
        this.minimumArticle = minimumArticle;
    }

	public Boolean getIsModel() {
		return isModel;
	}

	public void setIsModel(Boolean isModel) {
		this.isModel = isModel;
	}

	/**
	 * @return the offerModel
	 */
	public OfferTemplate getOfferModel() {
		return offerModel;
	}

	/**
	 * @param offerModel the offerModel to set
	 */
	public void setOfferModel(OfferTemplate offerModel) {
		this.offerModel = offerModel;
	}

	/**
	 * @return the offerAttributes
	 */
	public List<OfferTemplateAttribute> getOfferAttributes() {
		return offerAttributes;
	}

	/**
	 * @param offerAttributes the offerAttributes to set
	 */
	public void setOfferAttributes(List<OfferTemplateAttribute> offerAttributes) {
		this.offerAttributes = offerAttributes;
	}

    public boolean isGenerateQuoteEdrPerProduct() {
        return generateQuoteEdrPerProduct;
    }

    public void setGenerateQuoteEdrPerProduct(boolean generateQuoteEdrPerProduct) {
        this.generateQuoteEdrPerProduct = generateQuoteEdrPerProduct;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
    
}