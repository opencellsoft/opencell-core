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

package org.meveo.admin.action.admin.custom;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.action.script.ScriptInstanceBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.custom.EntityCustomActionService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.1
 */
@Named
@ViewScoped
public class EntityCustomActionBean extends BaseBean<EntityCustomAction> {

    private static final long serialVersionUID = 5401687428382698718L;

    @Inject
    private EntityCustomActionService entityActionScriptService;

    @Inject
    @ViewBean
    protected ScriptInstanceBean scriptInstanceBean;

    public EntityCustomActionBean() {
        super(EntityCustomAction.class);
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        EntityCustomAction actionDuplicate = entityActionScriptService.findByCodeAndAppliesTo(entity.getCode(), entity.getAppliesTo());
        if (actionDuplicate != null && !actionDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "customizedEntities.actionAlreadyExists"));
            return null;
        }

        String result = super.saveOrUpdate(killConversation);
        return result;

    }

    @Override
    protected IPersistenceService<EntityCustomAction> getPersistenceService() {
        return entityActionScriptService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public void refreshScript() {
        entity.setScript(scriptInstanceBean.getEntity());
    }

    /**
     * Prepare to show a popup to view or edit script
     */
    public void viewEditScript() {
        if (entity.getScript() != null) {
            scriptInstanceBean.initEntity(entity.getScript().getId());
        } else {
            scriptInstanceBean.newEntity();
        }
        scriptInstanceBean.setBackViewSave(this.getEditViewName());
    }

    /**
     * Prepare to show a popup to enter new script
     */
    public void newScript() {
        scriptInstanceBean.newEntity();
        scriptInstanceBean.setBackViewSave(this.getEditViewName());
    }
}