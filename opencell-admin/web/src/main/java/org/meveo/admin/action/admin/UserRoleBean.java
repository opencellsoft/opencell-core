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
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link Role} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class UserRoleBean extends BaseBean<Role> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Role} service. Extends {@link PersistenceService}. */
    @Inject
    private RoleService userRoleService;

    private DualListModel<Permission> permissionsDM;

    private DualListModel<Role> rolesDM;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserRoleBean() {
        super(Role.class);
    }

    public DualListModel<Permission> getPermissionListModel() {
        if (permissionsDM == null) {
            List<Permission> perksSource = permissionService.list();
            List<Permission> perksTarget = new ArrayList<Permission>();
            if (getEntity().getPermissions() != null) {
                perksTarget.addAll(getEntity().getPermissions());
            }
            perksSource.removeAll(perksTarget);
            permissionsDM = new DualListModel<Permission>(perksSource, perksTarget);
        }
        return permissionsDM;
    }

    public void setPermissionListModel(DualListModel<Permission> perks) {
        this.permissionsDM = perks;
    }

    public DualListModel<Role> getRoleListModel() {
        if (rolesDM == null) {
            List<Role> perksSource = userRoleService.listActive();
            perksSource.remove(getEntity());
            List<Role> perksTarget = new ArrayList<Role>();
            if (getEntity().getRoles() != null) {
                perksTarget.addAll(getEntity().getRoles());
            }
            perksSource.removeAll(perksTarget);
            rolesDM = new DualListModel<Role>(perksSource, perksTarget);
        }
        return rolesDM;
    }

    public void setRoleListModel(DualListModel<Role> perks) {
        this.rolesDM = perks;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        // Update permissions
        getEntity().getPermissions().clear();
        getEntity().getPermissions().addAll(permissionService.refreshOrRetrieve(permissionsDM.getTarget()));

        // Update roles
        getEntity().getRoles().clear();
        getEntity().getRoles().addAll(userRoleService.refreshOrRetrieve(rolesDM.getTarget()));

        return super.saveOrUpdate(killConversation);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Role> getPersistenceService() {
        return userRoleService;
    }
}
