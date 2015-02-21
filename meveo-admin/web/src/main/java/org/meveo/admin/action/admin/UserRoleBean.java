/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.omnifaces.cdi.ViewScoped;
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

    @Inject
    private PermissionService permissionService;

    private DualListModel<Permission> perks;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserRoleBean() {
        super(Role.class);
    }

    /**
     * Standard method for custom component with listType="pickList".
     */
    public DualListModel<Permission> getDualListModel() {
        if (perks == null) {
            List<Permission> perksSource = permissionService.list();
            List<Permission> perksTarget = new ArrayList<Permission>();
            if (getEntity().getPermissions() != null) {
                perksTarget.addAll(getEntity().getPermissions());
            }
            perksSource.removeAll(perksTarget);
            perks = new DualListModel<Permission>(perksSource, perksTarget);
        }
        return perks;
    }

    public void setDualListModel(DualListModel<Permission> perks) {
        getEntity().setPermissions(perks.getTarget());
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Role> getPersistenceService() {
        return userRoleService;
    }

    /**
     * Add additional criteria for searching by provider
     */
    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        // Do not user a check against user.provider as it contains only one value, while user can be linked to various providers
        searchCriteria.put(PersistenceService.SEARCH_SKIP_PROVIDER_CONSTRAINT, true);

        return searchCriteria;
    }
}
