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
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.model.DualListModel;

/**
 * Standard backing bean for {@link User} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Gediminas Ubartas
 * @created 2010.05.31
 */
@Named
@ConversationScoped
public class UserBean extends BaseBean<User> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link User} service. Extends {@link PersistenceService}. */
	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;
	
	@Inject
	private ProviderService providerService;

	@Inject
	private Messages messages;

	private DualListModel<Role> perks;
	
	private DualListModel<Provider> providerPerks;

	/**
	 * Password set by user which is later encoded and set to user before saving
	 * to db.
	 */
	private String password;

	/**
	 * For showing change password panel
	 */
	private boolean show = false;

	/**
	 * Repeated password to check if it matches another entered password and
	 * user did not make a mistake.
	 */
	private String repeatedPassword;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public UserBean() {
		super(User.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public User initEntity() {
		return super.initEntity();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	public String saveOrUpdate() {
		if (password == null) {
			return super.saveOrUpdate();
		} else {
			boolean passwordsDoNotMatch = password != null && !password.equals(repeatedPassword);
			if (passwordsDoNotMatch) {
				messages.error(new BundleKey("messages", "save.passwordsDoNotMatch"));
				return null;
			} else {
				entity.setLastPasswordModification(new Date());
				entity.setNewPassword(password);
				entity.setPassword(password);
				return super.saveOrUpdate();
			}
		}
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<User> getPersistenceService() {
		return userService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	 */
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "roles", "providers");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("providers", "roles", "provider");
	}

	/**
	 * Standard method for custom component with listType="pickList".
	 */
	public DualListModel<Role> getDualListModel() {
		if (perks == null) {
			List<Role> perksSource = roleService.list();
			List<Role> perksTarget = new ArrayList<Role>();
			if (getEntity().getRoles() != null) {
				perksTarget.addAll(getEntity().getRoles());
			}
			perksSource.removeAll(perksTarget);
			perks = new DualListModel<Role>(perksSource, perksTarget);
		}
		return perks;
	}

	public void setDualListModel(DualListModel<Role> perks) {
		getEntity().setRoles(new HashSet<Role>((List<Role>) perks.getTarget()));
	}
	
	public DualListModel<Provider> getProvidersDualListModel() {
		if (providerPerks == null) {
			List<Provider> perksSource = providerService.list();
			List<Provider> perksTarget = new ArrayList<Provider>();
			if (getEntity().getProviders() != null) {
				perksTarget.addAll(getEntity().getProviders());
			}
			perksSource.removeAll(perksTarget);
			providerPerks = new DualListModel<Provider>(perksSource, perksTarget);
		}
		return providerPerks;
	}

	public void setProvidersDualListModel(DualListModel<Provider> providerPerks) {
		getEntity().setProviders(new HashSet<Provider>((List<Provider>) providerPerks.getTarget()));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public void change() {
		this.show = !this.show;
	}
}
