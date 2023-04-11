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
package org.meveo.admin.action.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceCategory;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.ScriptInstanceCategoryService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptUtils;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link ScriptInstance} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit, view, delete operations). It
 * works with Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 */
@Named

@ViewBean
public class ScriptInstanceBean extends BaseBean<ScriptInstance> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link ScriptInstance} service. Extends {@link PersistenceService}.
     */
    
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ScriptInstanceCategoryService scriptInstanceCategoryService;

    @Inject
    private RoleService roleService;

    private DualListModel<String> execRolesDM;
    private DualListModel<String> sourcRolesDM;

    private String logMessages;

    public void initCompilationErrors() {
        if (facesContext.getPartialViewContext().isAjaxRequest()) {
            return;
        }
        if (getObjectId() == null) {
            return;
        }

        if (entity == null) {
            initEntity();
        }

        if (entity.isError()) {
            scriptInstanceService.compileScript(entity, true);
        }
    }

    public DualListModel<String> getExecRolesDM() {

        if (execRolesDM == null) {
            List<String> perksSource = roleService.listRoleNames(null);
            List<String> perksTarget = new ArrayList<String>();
            if (getEntity().getExecutionRoles() != null) {
                perksTarget.addAll(getEntity().getExecutionRoles());
            }
            perksSource.removeAll(perksTarget);
            execRolesDM = new DualListModel<String>(perksSource, perksTarget);
        }
        return execRolesDM;
    }

    public DualListModel<String> getSourcRolesDM() {

        if (sourcRolesDM == null) {
            List<String> perksSource = roleService.listRoleNames(null);
            List<String> perksTarget = new ArrayList<String>();
            if (getEntity().getSourcingRoles() != null) {
                perksTarget.addAll(getEntity().getSourcingRoles());
            }
            perksSource.removeAll(perksTarget);
            sourcRolesDM = new DualListModel<String>(perksSource, perksTarget);
        }
        return sourcRolesDM;
    }

    public void setExecRolesDM(DualListModel<String> perks) {
        this.execRolesDM = perks;
    }

    public void setSourcRolesDM(DualListModel<String> perks) {
        this.sourcRolesDM = perks;
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ScriptInstanceBean() {
        super(ScriptInstance.class);

    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<ScriptInstance> getPersistenceService() {
        return scriptInstanceService;
    }

    /**
     * Fetch customer field so no LazyInitialize exception is thrown when we access it from account edit view.
     * 
     */
    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("executionRoles", "sourcingRoles");
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (entity.getSourceTypeEnum() == ScriptSourceTypeEnum.JAVA) {

            String code = ScriptUtils.getFullClassname(entity.getScript());

            // check script existed full class name in class path
            if (ScriptUtils.isOverwritesJavaClass(code)) {
                messages.error(new BundleKey("messages", "message.scriptInstance.classInvalid"), code);
                return null;
            }
            entity.setCode(code);
        }

        // check duplicate script
        ScriptInstance scriptDuplicate = scriptInstanceService.findByCode(entity.getCode());
        if (scriptDuplicate != null && !scriptDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "scriptInstance.scriptAlreadyExists"), entity.getCode());
            return null;
        }

        // Update roles
        getEntity().getExecutionRoles().clear();
        if (execRolesDM != null) {
            getEntity().getExecutionRoles().addAll(execRolesDM.getTarget());
        }

        // Update roles
        getEntity().getSourcingRoles().clear();
        if (sourcRolesDM != null) {
            getEntity().getSourcingRoles().addAll(sourcRolesDM.getTarget());
        }

        String result = super.saveOrUpdate(killConversation);

        if (entity.isError()) {
            // if (entity.isError()) {
            // messages.error(new BundleKey("messages", "scriptInstance.compilationFailed"));
            // }
            result = null;
        }
        if (killConversation) {
            endConversation();
        }

        return result;
    }

    @ActionMethod
    public String execute() {
        logMessages = scriptInstanceService.test(entity, null);
        messages.info(new BundleKey("messages", "message.scriptInstance.executed"));
        return null;
    }

    public String getLogs() {
        return logMessages;
    }

    public boolean isUserHasSourcingRole(ScriptInstance scriptInstance) {
        return scriptInstanceService.isUserHasSourcingRole(scriptInstance);
    }

    public void testCompilation() {

        // check script existed full class name in class path
        String code = ScriptUtils.getFullClassname(entity.getScript());
        if (ScriptUtils.isOverwritesJavaClass(code)) {
            messages.error(new BundleKey("messages", "message.scriptInstance.classInvalid"), code);
            return;
        }

        // check duplicate script
        ScriptInstance scriptDuplicate = scriptInstanceService.findByCode(code);
        if (scriptDuplicate != null && !scriptDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "scriptInstance.scriptAlreadyExists"), code);
            return;
        }

        scriptInstanceService.compileScript(entity, true);
        if (!entity.isError()) {
            messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
        }
    }

    public LazyDataModel<ScriptInstance> getScriptInstanceByCategory(String catCode) {
        ScriptInstanceCategory category = scriptInstanceCategoryService.findByCode(catCode);
        if (category != null) {
            filters.put("scriptInstanceCategory", category);
            return getLazyDataModel();
        }

        return null;
    }
}