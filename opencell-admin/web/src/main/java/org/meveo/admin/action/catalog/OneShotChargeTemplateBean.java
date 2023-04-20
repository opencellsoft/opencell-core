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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link OneShotChargeTemplate} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class OneShotChargeTemplateBean extends CustomFieldBean<OneShotChargeTemplate> {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OneShotChargeTemplate} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    private ProductChargeTemplateService productChargeTemplateService;

    private DualListModel<TriggeredEDRTemplate> edrTemplates;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OneShotChargeTemplateBean() {
        super(OneShotChargeTemplate.class);
        showDeprecatedWarning(DEPRECATED_ADMIN_MESSAGE);
    }

    @Override
    public void search() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.search();
    }

    // /**
    // * Data model of entities for data table in GUI. Filters charges of Usage
    // * type.
    // *
    // * @return filtered entities.
    // */
    // // @Out(value = "oneShotChargeTemplatesForUsageType", required = false)
    // protected PaginationDataModel<OneShotChargeTemplate>
    // getDataModelForUsageType() {
    // return entities;
    // }

    /**
     * Factory method, that is invoked if data model is empty. Invokes BaseBean.list() method that handles all data model loading. Overriding is needed only to put factory name on
     * it. Filters charges of Usage type.
     *
     * @return outcome view
     * @throws BusinessException business exception
     *
     */
    // @Produces
    // @Named("oneShotChargeTemplatesForUsageType")
    // public PaginationDataModel<OneShotChargeTemplate> listForUsageType() {
    // getFilters();
    // if (!filters.containsKey("disabled")) {
    // filters.put("disabled", false);
    // }
    // filters.put("oneShotChargeTemplateType",
    // OneShotChargeTemplateTypeEnum.USAGE);
    // return super.list();
    // }

    /*
     * (non-Javadoc)
     *
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        // check for unicity
        if (recurringChargeTemplateService.findByCode(entity.getCode()) != null || usageChargeTemplateService.findByCode(entity.getCode()) != null || productChargeTemplateService.findByCode(entity.getCode()) != null) {
            messages.error(new BundleKey("messages", "chargeTemplate.uniqueField.code"));
            return null;
        }

        getEntity().setEdrTemplates(triggeredEDRTemplateService.refreshOrRetrieve(edrTemplates.getTarget()));

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getEditViewName();
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<OneShotChargeTemplate> getPersistenceService() {
        return oneShotChargeTemplateService;
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

    @ActionMethod
    public void duplicate() {

        if (entity != null && entity.getId() != null) {

            try {
                oneShotChargeTemplateService.duplicate(entity);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicating one shot charge template entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    public boolean isUsedInSubscription() {
        return (getEntity() != null && !getEntity().isTransient() && (oneShotChargeTemplateService.findByCode(getEntity().getCode()) != null)) ? true : false;
    }

    public LazyDataModel<OneShotChargeTemplate> getOtherTypeCharges() {
        if (filters == null) {
            filters = new HashMap<>();
        }
        filters.put("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.OTHER);
        return getLazyDataModel();
    }

	public List<OneShotChargeTemplate> getOtherOneShotCharges() {

		List<OneShotChargeTemplate> oneShotChargeTemplates = super.listAll();
		if (oneShotChargeTemplates == null || oneShotChargeTemplates.isEmpty()) {
			return Collections.emptyList();
		}
		return oneShotChargeTemplates.stream().filter(os -> OneShotChargeTemplateTypeEnum.OTHER.equals(os.getOneShotChargeTemplateType())).collect(Collectors.toList());
	}

}
