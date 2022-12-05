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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class UsageChargeTemplateBean extends CustomFieldBean<UsageChargeTemplate> {
    private static final long serialVersionUID = 1L;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    protected ProductChargeTemplateService productChargeTemplateService;

    private DualListModel<TriggeredEDRTemplate> edrTemplates;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UsageChargeTemplateBean() {
        super(UsageChargeTemplate.class);
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("edrTemplates");
    }

    @Override
    public void search() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.search();
    }

    /**
     * Conversation is ended and user is redirected from edit to his previous window.
     * 
     * @throws BusinessException General business exception
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        // check for unicity
        if (oneShotChargeTemplateService.findByCode(entity.getCode()) != null || recurringChargeTemplateService.findByCode(entity.getCode()) != null || productChargeTemplateService.findByCode(entity.getCode()) != null) {
            messages.error(new BundleKey("messages", "chargeTemplate.uniqueField.code"));
            return null;
        }

        getEntity().setEdrTemplates(edrTemplateService.refreshOrRetrieve(edrTemplates.getTarget()));

        String outcome = null;
        try {
        	outcome = super.saveOrUpdate(killConversation);
		} catch (BusinessEntityException e) {
			messages.error(new BundleKey("messages", e.getMessage()));
			throw e;
		}
        if (outcome != null) {
            return getEditViewName();
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<UsageChargeTemplate> getPersistenceService() {
        return usageChargeTemplateService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public DualListModel<TriggeredEDRTemplate> getEdrTemplatesModel() {
        if (edrTemplates == null) {
            List<TriggeredEDRTemplate> source = triggeredEDRTemplateService.list();
            List<TriggeredEDRTemplate> target = new ArrayList<TriggeredEDRTemplate>();
            if (getEntity().getEdrTemplates() != null) {
                target.addAll(getEntity().getEdrTemplates());
            }
            source.removeAll(target);
            edrTemplates = new DualListModel<TriggeredEDRTemplate>(source, target);
        }
        return edrTemplates;
    }

    public void setEdrTemplatesModel(DualListModel<TriggeredEDRTemplate> edrTemplates) {
        this.edrTemplates = edrTemplates;
    }

    @Inject
    private TriggeredEDRTemplateService edrTemplateService;

    @ActionMethod
    public void duplicate() {

        if (entity != null && entity.getId() != null) {
            try {
                usageChargeTemplateService.duplicate(entity);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicaing usage charge template entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    public boolean isUsedInSubscription() {
        return (getEntity() != null && !getEntity().isTransient() && (usageChargeTemplateService.findByCode(getEntity().getCode()) != null)) ? true : false;
    }

}
