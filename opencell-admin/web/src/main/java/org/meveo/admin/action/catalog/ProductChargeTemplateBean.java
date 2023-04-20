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
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.primefaces.model.DualListModel;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class ProductChargeTemplateBean extends BaseBean<ProductChargeTemplate> {

    private static final long serialVersionUID = -1167691337353764450L;

    @Inject
    protected ProductChargeTemplateService productChargeTemplateService;

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    private DualListModel<TriggeredEDRTemplate> edrTemplatesDM;

    public ProductChargeTemplateBean() {
        super(ProductChargeTemplate.class);
        showDeprecatedWarning(DEPRECATED_ADMIN_MESSAGE);
    }

    @Override
    protected IPersistenceService<ProductChargeTemplate> getPersistenceService() {
        return productChargeTemplateService;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        // check for unicity
        if (oneShotChargeTemplateService.findByCode(entity.getCode()) != null || usageChargeTemplateService.findByCode(entity.getCode()) != null || recurringChargeTemplateService.findByCode(entity.getCode()) != null) {
            messages.error(new BundleKey("messages", "chargeTemplate.uniqueField.code"));
            return null;
        }

        getEntity().setEdrTemplates(triggeredEDRTemplateService.refreshOrRetrieve(edrTemplatesDM.getTarget()));

        boolean newEntity = (entity.getId() == null);

        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return newEntity ? getEditViewName() : outcome;
        }

        return null;
    }

    public DualListModel<TriggeredEDRTemplate> getEdrTemplatesDM() {
        if (edrTemplatesDM == null) {
            List<TriggeredEDRTemplate> source = triggeredEDRTemplateService.list();
            List<TriggeredEDRTemplate> target = new ArrayList<TriggeredEDRTemplate>();
            if (getEntity().getEdrTemplates() != null) {
                target.addAll(getEntity().getEdrTemplates());
            }

            source.removeAll(target);
            edrTemplatesDM = new DualListModel<TriggeredEDRTemplate>(source, target);
        }
        return edrTemplatesDM;
    }

    public void setEdrTemplatesDM(DualListModel<TriggeredEDRTemplate> edrTemplatesDM) {
        this.edrTemplatesDM = edrTemplatesDM;
    }

}
