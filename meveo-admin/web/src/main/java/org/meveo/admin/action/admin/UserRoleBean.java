/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link Role} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Gediminas Ubartas
 * @created 2010.05.31
 */
@Named
@ConversationScoped
public class UserRoleBean extends BaseBean<Role> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link Role} service. Extends {@link PersistenceService}. */
	@Inject
	private RoleService userRoleService;

	@Inject
	private PermissionService permissionService;

	private DualListModel<Permission> perks;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public UserRoleBean() {
		super(Role.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public Role initEntity() {
		return super.initEntity();
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

	@Override
	public String getNewViewName() {
		return "userRoleDetail";
	}

	@Override
	protected String getListViewName() {
		return "userRoles";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Role> getPersistenceService() {
		return userRoleService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	 */
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("users");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("users");
	}
}
