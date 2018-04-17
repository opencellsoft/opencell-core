/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.catalog.ChargeTemplate.ChargeTypeEnum;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "OFFER")
@DiscriminatorValue("OFFER")
@NamedQueries({ @NamedQuery(name = "OfferTemplate.countActive", query = "SELECT COUNT(*) FROM OfferTemplate WHERE businessOfferModel is not null and lifeCycleStatus='ACTIVE'"),
        @NamedQuery(name = "OfferTemplate.countDisabled", query = "SELECT COUNT(*) FROM OfferTemplate WHERE businessOfferModel is not null and lifeCycleStatus<>'ACTIVE'"),
        @NamedQuery(name = "OfferTemplate.countExpiring", query = "SELECT COUNT(*) FROM OfferTemplate WHERE :nowMinusXDay<validity.to and validity.to<=NOW() and businessOfferModel is not null") })
public class OfferTemplate extends ProductOffering {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "business_offer_model_id")
    private BusinessOfferModel businessOfferModel;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<>();

    @OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<OfferProductTemplate> offerProductTemplates = new ArrayList<>();

    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;

    @Embedded
    private SubscriptionRenewal subscriptionRenewal = new SubscriptionRenewal();

    @Transient
    private String prefix;

    @Transient
    private Map<ChargeTypeEnum, List<ServiceTemplate>> serviceTemplatesByChargeType;

    @Transient
    private List<ProductTemplate> productTemplates;

    @Transient
    private String transientCode;

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

    @SuppressWarnings("rawtypes")
    public Map<ChargeTypeEnum, List<ServiceTemplate>> getServiceTemplatesByChargeType() {

        if (serviceTemplatesByChargeType != null) {
            return serviceTemplatesByChargeType;
        }

        serviceTemplatesByChargeType = new HashMap<>();

        for (OfferServiceTemplate service : offerServiceTemplates) {
            List charges = service.getServiceTemplate().getServiceRecurringCharges();
            if (charges != null && !charges.isEmpty()) {
                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.RECURRING)) {
                    serviceTemplatesByChargeType.put(ChargeTypeEnum.RECURRING, new ArrayList<ServiceTemplate>());
                }
                serviceTemplatesByChargeType.get(ChargeTypeEnum.RECURRING).add(service.getServiceTemplate());
            }

            charges = service.getServiceTemplate().getServiceUsageCharges();
            if (charges != null && !charges.isEmpty()) {
                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.USAGE)) {
                    serviceTemplatesByChargeType.put(ChargeTypeEnum.USAGE, new ArrayList<ServiceTemplate>());
                }
                serviceTemplatesByChargeType.get(ChargeTypeEnum.USAGE).add(service.getServiceTemplate());
            }

            charges = service.getServiceTemplate().getServiceSubscriptionCharges();
            if (charges != null && !charges.isEmpty()) {
                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.SUBSCRIPTION)) {
                    serviceTemplatesByChargeType.put(ChargeTypeEnum.SUBSCRIPTION, new ArrayList<ServiceTemplate>());
                }
                serviceTemplatesByChargeType.get(ChargeTypeEnum.SUBSCRIPTION).add(service.getServiceTemplate());
            }

            charges = service.getServiceTemplate().getServiceTerminationCharges();
            if (charges != null && !charges.isEmpty()) {
                if (!serviceTemplatesByChargeType.containsKey(ChargeTypeEnum.TERMINATION)) {
                    serviceTemplatesByChargeType.put(ChargeTypeEnum.TERMINATION, new ArrayList<ServiceTemplate>());
                }
                serviceTemplatesByChargeType.get(ChargeTypeEnum.TERMINATION).add(service.getServiceTemplate());
            }
        }

        return serviceTemplatesByChargeType;
    }

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
        code = transientCode;
    }

    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }
}