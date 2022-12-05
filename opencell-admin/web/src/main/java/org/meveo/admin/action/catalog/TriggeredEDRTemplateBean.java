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

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;

@Named
@ViewScoped
public class TriggeredEDRTemplateBean extends BaseBean<TriggeredEDRTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link PricePlanMatrix} service. Extends {@link PersistenceService}.
     */
    @Inject
    private TriggeredEDRTemplateService triggeredEdrService;

    protected MeveoInstance meveoInstance;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TriggeredEDRTemplateBean() {
        super(TriggeredEDRTemplate.class);
    }

    public MeveoInstance getMeveoInstance() {
        return meveoInstance;
    }

    public void setMeveoInstance(MeveoInstance meveoInstance) {
        this.meveoInstance = meveoInstance;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TriggeredEDRTemplate> getPersistenceService() {
        return triggeredEdrService;
    }

    @Override
    protected String getListViewName() {
        return "triggeredEdrTemplates";
    }

    @Override
    public String getEditViewName() {
        return "triggeredEdrTemplateDetail";
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String result = super.saveOrUpdate(killConversation);
        return result;
    }

    public void duplicate() {
        if (entity == null || entity.getId() == null) {
            return;
        }
        try {
            triggeredEdrService.duplicate(entity);
            messages.info(new BundleKey("messages", "duplicate.successfull"));
        } catch (BusinessException e) {
            log.error("Error encountered duplicating triggered EDR template entity: {}", entity.getCode(), e);
            messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
        }
    }

}
