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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage.Severity;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.User;
import org.meveo.service.admin.local.UserServiceLocal;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;

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
// TODO: @Scope(ScopeType.CONVERSATION)
public class UserBean extends BaseBean<User> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link User} service. Extends {@link PersistenceService}. */
	@Inject
	private UserServiceLocal userService;

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
	/*
	 * TODO: @Begin(nested = true)
	 * 
	 * @Factory("user")
	 */
	@Produces
	@Named("user")
	public User init() {
		return initEntity();
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	// TODO: @Out(value = "users", required = false)
	@Produces
	@Named("users")
	protected PaginationDataModel<User> getDataModel() {
		return entities;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	/*
	 * TODO: @Begin(join = true)
	 * 
	 * @Factory("users")
	 */
	@Produces
	@Named("users")
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	// @End(beforeRedirect = true, root=false)
	public String saveOrUpdate() {
		if (password == null) {
			return saveOrUpdate(entity);
		} else {
			boolean passwordsDoNotMatch = password != null && !password.equals(repeatedPassword);
			if (passwordsDoNotMatch) {
				// TODO: statusMessages.addFromResourceBundle(Severity.ERROR,
				// "save.passwordsDoNotMatch");
				return null;
			} else {
				entity.setLastPasswordModification(new Date());
				entity.setNewPassword(password);
				entity.setPassword(password);
				return saveOrUpdate(entity);
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
		return Arrays.asList("provider");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("providers");
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
