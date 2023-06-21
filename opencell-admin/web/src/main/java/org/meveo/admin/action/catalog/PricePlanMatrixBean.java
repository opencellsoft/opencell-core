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
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.omnifaces.cdi.Param;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.Visibility;

/**
 * Standard backing bean for {@link PricePlanMatrix} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @lastModifiedVersion 5.1
 */
@Named
@ViewScoped
public class PricePlanMatrixBean extends CustomFieldBean<PricePlanMatrix> {

    private static final long serialVersionUID = -7046887530976683885L;

    /**
     * Injected @{link PricePlanMatrix} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Inject
    @Param
    private Long chargeId;

    private String backPage;

    private long chargeTemplateId;

    private List<Boolean> columnVisibilitylist;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PricePlanMatrixBean() {
        super(PricePlanMatrix.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return price plan matrix.
     */

    public PricePlanMatrix initEntity() {
        PricePlanMatrix obj = super.initEntity();
        if (obj.isTransient()) {
            obj.setMinSubscriptionAgeInMonth(0L);
            obj.setMaxSubscriptionAgeInMonth(9999L);
        }
        if (chargeId != null) {
            
            ChargeTemplate chargeTemplate = chargeTemplateService.findById(chargeId);

            if (chargeTemplate != null) {
                if (chargeTemplate instanceof RecurringChargeTemplate) {
                    RecurringChargeTemplate recurring = (RecurringChargeTemplate) chargeTemplate;
                    if (getObjectId() == null) {
                        obj.setCode(getPricePlanCode(recurring));
                        obj.setDescription(recurring.getDescription());
                        obj.setSequence(getNextSequence(recurring));
                    }
                    backPage = "recurringChargeTemplateDetail";
                } else if (chargeTemplate instanceof OneShotChargeTemplate) {
                    OneShotChargeTemplate oneShot = (OneShotChargeTemplate) chargeTemplate;
                    if (getObjectId() == null) {
                        obj.setCode(getPricePlanCode(oneShot));
                        obj.setDescription(oneShot.getDescription());
                        obj.setSequence(getNextSequence(oneShot));
                    }
                    backPage = "oneShotChargeTemplateDetail";

                } else if (chargeTemplate instanceof UsageChargeTemplate) {
                    UsageChargeTemplate usageCharge = (UsageChargeTemplate) chargeTemplate;
                    if (getObjectId() == null) {
                        obj.setCode(getPricePlanCode(usageCharge));
                        obj.setDescription(usageCharge.getDescription());
                        obj.setSequence(getNextSequence(usageCharge));
                    }
                    backPage = "usageChargeTemplateDetail";
                } else if (chargeTemplate instanceof ProductChargeTemplate) {
                    ProductChargeTemplate productCharge = (ProductChargeTemplate) chargeTemplate;
                    if (getObjectId() == null) {
                        obj.setCode(getPricePlanCode(productCharge));
                        obj.setDescription(productCharge.getDescription());
                        obj.setSequence(getNextSequence(productCharge));
                    }
                    backPage = "productChargeTemplateDetail";
                }
            }
            chargeTemplateId = chargeId;
        }
        return obj;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PricePlanMatrix> getPersistenceService() {
        return pricePlanMatrixService;
    }

    public void onChargeSelect(SelectEvent event) {
        if (event.getObject() instanceof ChargeTemplate) {
            ChargeTemplate chargeTemplate = (ChargeTemplate) event.getObject();
            if (chargeTemplate != null) {
                if(entity.isTransient()) {
                    entity.setCode(getPricePlanCode(chargeTemplate));
                    entity.setDescription(chargeTemplate.getDescription());
                    entity.setSequence(getNextSequence(chargeTemplate));
                }
            }
        }
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    // show advanced button in search panel
    private boolean advanced = false;

    public boolean getAdvanced() {
        return this.advanced;
    }

    public void advancedAction(ActionEvent actionEvent) {
        this.advanced = !advanced;
        if (filters != null) {
            Iterator<String> iter = filters.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                if (!"eventCode".equals(key) && !"seller".equals(key) && !"code".equals(key) && !"offerTemplate".equals(key)) {
                    iter.remove();
                }
            }
        }
    }

    public LazyDataModel<PricePlanMatrix> getPricePlanMatrixList(ChargeTemplate chargeTemplate) {
        filters.put("eventCode", chargeTemplate.getCode());
        return getLazyDataModel();
    }

    public String getPricePlanCode(ChargeTemplate chargetemplate) {
        String pricePlanCode = null;
        try {
            if (chargetemplate != null) {
                pricePlanCode = "PP_" + chargetemplate.getCode() + "_" + getNextSequence(chargetemplate);
            }
        } catch (Exception e) {
            log.warn("error while getting pricePlan code", e);
            return null;
        }
        return pricePlanCode;
    }

    public Long getNextSequence(ChargeTemplate chargetemplate) {
        long result = 0;
        try {
            if (chargetemplate != null) {
                result = pricePlanMatrixService.getLastPricePlanSequenceByChargeCode(chargetemplate.getCode()) + 1;
            }
        } catch (Exception e) {
            log.warn("error while getting next sequence", e);
            return null;
        }
        return result;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        if (chargeTemplateId != 0) {
            super.saveOrUpdate(killConversation);
            return getBackCharge();
        } else {
            return super.saveOrUpdate(killConversation);
        }
    }

    @Override
    public String back() {
        if (chargeTemplateId != 0) {
            return getBackCharge();
        } else {
            return super.back();
        }
    }

    public String getBackCharge() {
        String chargeName = null;
        if (backPage.equals("recurringChargeTemplateDetail")) {
            chargeName = "recurringChargeTemplates";
        } else if (backPage.equals("oneShotChargeTemplateDetail")) {
            chargeName = "oneShotChargeTemplates";
        } else if (backPage.equals("productChargeTemplateDetail")) {
            chargeName = "productChargeTemplates";
        } else {
            chargeName = "usageChargeTemplates";
        }

        return "/pages/catalog/" + chargeName + "/" + backPage + ".xhtml?objectId=" + chargeTemplateId + "&edit=true&tab=1&faces-redirect=true";
    }

    @ActionMethod
    public void duplicate() {
        if (entity != null && entity.getId() != null) {
            try {
                pricePlanMatrixService.duplicate(entity);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicating price plan matrix entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    public long getChargeTemplateId() {
        return chargeTemplateId;
    }

    /**
     * initialize the list of table columns to be visible
     */
    @PostConstruct
    public void init() {
        columnVisibilitylist = Arrays.asList(true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false);
    }

    public List<Boolean> getColumnVisibilitylist() {
        return columnVisibilitylist;
    }

    public void onToggle(ToggleEvent e) {
        columnVisibilitylist.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

}
