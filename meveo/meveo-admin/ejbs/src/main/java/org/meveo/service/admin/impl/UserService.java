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
package org.meveo.service.admin.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.ResourceBundle;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.NoRoleException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.admin.security.user.UserCreate;
import org.meveo.admin.security.user.UserDelete;
import org.meveo.admin.security.user.UserUpdate;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Role;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.local.UserServiceLocal;
import org.meveo.service.base.PersistenceService;

/**
 * User service implementation.
 * 
 * @author Gediminas Ubartas
 * @created 2010.05.31
 */

@Stateless
@Named
public class UserService extends PersistenceService<User> implements
		UserServiceLocal {

	static User systemUser = null;

	private static String SEQUENCE_VALUE_TEST = "";//TODO: ResourceBundle. ResourceBundle.instance()
			.getString("sequence.test");

	@Override
	@UserCreate
	public void create(User user) throws UsernameAlreadyExistsException {
		// TODO: Provider. Provider currentProvider = (Provider)
		// Component.getInstance("currentProvider");
		Provider currentProvider = null;
		if (isUsernameExists(user.getUserName()))
			throw new UsernameAlreadyExistsException(user.getUserName());

		user.setUserName(user.getUserName().toUpperCase());
		user.setPassword(Sha1Encrypt.encodePassword(user.getPassword()));
		user.setLastPasswordModification(new Date());
		List<Provider> providers = new ArrayList<Provider>();
		providers.add(currentProvider);
		user.setProviders(providers);
		super.create(user);
	}

	@Override
	@UserUpdate
	public void update(User user) throws UsernameAlreadyExistsException {
		if (isUsernameExists(user.getUserName(), user.getId())) {
			em.refresh(user);
			throw new UsernameAlreadyExistsException(user.getUserName());
		}

		user.setUserName(user.getUserName().toUpperCase());
		if (!StringUtils.isBlank(user.getNewPassword())) {
			String encryptedPassword = Sha1Encrypt.encodePassword(user
					.getPassword());
			user.setPassword(encryptedPassword);
		}

		super.update(user);
	}

	@Override
	@UserDelete
	public void remove(User user) {
		super.remove(user);
	}

	public User getSystemUser() {
		if (systemUser == null) {
			systemUser = findUsersByRoles("administrateur").get(0);
		}
		return systemUser;
	}

	@SuppressWarnings("unchecked")
	public List<User> findUsersByRoles(String... roles) {
		String queryString = "select distinct u from User u join u.roles as r where r.name in (:roles)";
		Query query = em.createQuery(queryString);
		query.setParameter("roles", Arrays.asList(roles));
		query.setHint("org.hibernate.flushMode", "NEVER");
		return query.getResultList();
	}

	public boolean isUsernameExists(String username, Long id) {
		String stringQuery = "select count(*) from User u where u.userName = :userName and u.id <> :id";
		Query query = em.createQuery(stringQuery);
		query.setParameter("userName", username.toUpperCase());
		query.setParameter("id", id);
		query.setHint("org.hibernate.flushMode", "NEVER");
		return ((Long) query.getSingleResult()).intValue() != 0;
	}

	public boolean isUsernameExists(String username) {
		String stringQuery = "select count(*) from User u where u.userName = :userName";
		Query query = em.createQuery(stringQuery);
		query.setParameter("userName", username.toUpperCase());
		query.setHint("org.hibernate.flushMode", "NEVER");
		return ((Long) query.getSingleResult()).intValue() != 0;
	}

	public User findByUsernameAndPassword(String username, String password) {
		try {
			password = Sha1Encrypt.encodePassword(password);
			return (User) em
					.createQuery(
							"from User where userName = :userName and password = :password")
					.setParameter("userName", username.toUpperCase())
					.setParameter("password", password).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByUsername(String username) {
		try {
			return (User) em
					.createQuery("from User where userName = :userName")
					.setParameter("userName", username.toUpperCase())
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByEmail(String email) {
		try {
			return (User) em.createQuery("from User where email = :email")
					.setParameter("email", email).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User changePassword(User user, String newPassword)
			throws BusinessException {
		em.refresh(user);
		user.setLastPasswordModification(new Date());
		user.setPassword(Sha1Encrypt.encodePassword(newPassword));
		super.update(user);
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllRolesExcept(String rolename1, String rolename2) {
		return em
				.createQuery(
						"from MeveoRole as r where r.name<>:name1 and r.name<>:name2")
				.setParameter("name1", rolename1)
				.setParameter("name2", rolename2).getResultList();
	}

	public Role getRoleByName(String name) {
		return (Role) em.createQuery("from MeveoRole as r where r.name=:name")
				.setParameter("name", name).getSingleResult();
	}

	public void login(User currentUser) throws LoginException {
		// Check if the user is active
		if (!currentUser.isActive()) {
			log.info("The user #" + currentUser.getId() + " is not active");
			throw new InactiveUserException("The user #" + currentUser.getId()
					+ " is not active");
		}

		// Check if the user password has expired
		String passwordExpiracy = ParamBean.getInstance("meveo.properties")
				.getProperty("password.Expiracy", "90");

		if (currentUser.isPasswordExpired(Integer.parseInt(passwordExpiracy))) {
			log.info("The password of user #" + currentUser.getId()
					+ " has expired.");
			throw new PasswordExpiredException("The password of user #"
					+ currentUser.getId() + " has expired.");
		}

		// Check the roles
		if (currentUser.getRoles() == null || currentUser.getRoles().isEmpty()) {
			log.info("The user #" + currentUser.getId() + " has no role!");
			throw new NoRoleException("The user #" + currentUser.getId()
					+ " has no role!");
		}
	}

	public User duplicate(User user) {
		log.debug("Start duplication of User entity ..");

		org.meveo.model.shared.Name otherName = user.getName();
		Title title = otherName.getTitle();
		String firstName = otherName.getFirstName();
		// is blank. TODO move to utils
		if (!(firstName == null || firstName.trim().length() == 0)) {
			firstName += "_new";
		}
		String lastName = otherName.getLastName() + "_new";

		User newUser = new User();

		newUser.setName(new org.meveo.model.shared.Name(title, firstName,
				lastName));

		newUser.setDisabled(newUser.isDisabled());
		newUser.setUserName(user.getUserName() + "_NEW");
		newUser.setRoles(new ArrayList<Role>(user.getRoles()));

		log.debug("End of duplication of User entity");

		return newUser;
	}

	public void saveActivity(User user, String objectId, String action,
			String uri) {
		// String sequenceValue = "USER_LOG_SEQ.nextval";
		if (!SEQUENCE_VALUE_TEST.equals("true")) {

			String stringQuery = "INSERT INTO ADM_USER_LOG (USER_NAME, USER_ID, DATE_EXECUTED, ACTION, URL, OBJECT_ID) VALUES ( ?, ?, ?, ?, ?, ?)";

			Query query = em.createNativeQuery(stringQuery);
			query.setParameter(1, user.getUserName());
			query.setParameter(2, user.getId());
			query.setParameter(3, new Date());
			query.setParameter(4, action);
			query.setParameter(5, uri);
			query.setParameter(6, objectId);
			query.executeUpdate();
		}
	}
}
