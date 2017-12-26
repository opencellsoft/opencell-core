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
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.scripts.CustomScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.GenericScriptService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link ScriptInstance} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
@ViewBean
public class ScriptInstanceBean extends BaseBean<ScriptInstance> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link ScriptInstance} service. Extends {@link PersistenceService}.
     */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private RoleService roleService;

    @Inject
    private GenericScriptService genericScriptService;

    private DualListModel<Role> execRolesDM;
    private DualListModel<Role> sourcRolesDM;
    
	public void initCompilationErrors() {
        if (FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest()) {
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

    public DualListModel<Role> getExecRolesDM() {

        if (execRolesDM == null) {
            List<Role> perksSource = roleService.getAllRoles();
            List<Role> perksTarget = new ArrayList<Role>();
            if (getEntity().getExecutionRoles() != null) {
                perksTarget.addAll(getEntity().getExecutionRoles());
            }
            perksSource.removeAll(perksTarget);
            execRolesDM = new DualListModel<Role>(perksSource, perksTarget);
        }
        return execRolesDM;
    }

    public DualListModel<Role> getSourcRolesDM() {

        if (sourcRolesDM == null) {
            List<Role> perksSource = roleService.getAllRoles();
            List<Role> perksTarget = new ArrayList<Role>();
            if (getEntity().getSourcingRoles() != null) {
                perksTarget.addAll(getEntity().getSourcingRoles());
            }
            perksSource.removeAll(perksTarget);
            sourcRolesDM = new DualListModel<Role>(perksSource, perksTarget);
        }
        return sourcRolesDM;
    }

    public void setExecRolesDM(DualListModel<Role> perks) {
        this.execRolesDM = perks;
    }

    public void setSourcRolesDM(DualListModel<Role> perks) {
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

    @Override
    protected String getListViewName() {
        return "scriptInstances";
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

        String code = CustomScriptService.getFullClassname(entity.getScript());

        // check script existed full class name in class path
        if (CustomScriptService.isOverwritesJavaClass(code)) {
            messages.error(new BundleKey("messages", "message.scriptInstance.classInvalid"), code);
            return null;
        }

        // check duplicate script
        CustomScript scriptDuplicate = genericScriptService.findByCode(code);
        if (scriptDuplicate != null && !scriptDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "scriptInstance.scriptAlreadyExists"), code);
            return null;
        }

        // Update roles
        getEntity().getExecutionRoles().clear();
        if (execRolesDM != null) {
            getEntity().getExecutionRoles().addAll(roleService.refreshOrRetrieve(execRolesDM.getTarget()));
        }

        // Update roles
        getEntity().getSourcingRoles().clear();
        if (sourcRolesDM != null) {
            getEntity().getSourcingRoles().addAll(roleService.refreshOrRetrieve(sourcRolesDM.getTarget()));
        }

        String result = super.saveOrUpdate(killConversation);

        if (entity.isError().booleanValue()) {
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

    public String execute() {
        scriptInstanceService.test(entity.getCode(), null);
        return null;
    }

    public List<String> getLogs() {
        return scriptInstanceService.getLogs(entity.getCode());
    }

    public boolean isUserHasSourcingRole(ScriptInstance scriptInstance) {
        return scriptInstanceService.isUserHasSourcingRole(scriptInstance);
    }

    public void testCompilation() {

        // check script existed full class name in class path
        String code = CustomScriptService.getFullClassname(entity.getScript());
        if (CustomScriptService.isOverwritesJavaClass(code)) {
            messages.error(new BundleKey("messages", "message.scriptInstance.classInvalid"), code);
            return;
        }

        // check duplicate script
        CustomScript scriptDuplicate = genericScriptService.findByCode(code);
        if (scriptDuplicate != null && !scriptDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "scriptInstance.scriptAlreadyExists"), code);
            return;
        }

        scriptInstanceService.compileScript(entity, true);
        if (!entity.isError()) {
            messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
        }
    }

}