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
package org.meveo.service.admin.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InactiveUserException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.NoRoleException;
import org.meveo.admin.exception.PasswordExpiredException;
import org.meveo.admin.exception.UnknownUserException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Title;
import org.meveo.security.authorization.UserCreate;
import org.meveo.security.authorization.UserDelete;
import org.meveo.security.authorization.UserUpdate;
import org.meveo.service.base.PersistenceService;

/**
 * User service implementation.
 */
@Stateless
public class UserService extends PersistenceService<User> {

	static User systemUser = null;

	private ParamBean paramBean = ParamBean.getInstance();

	@Override
	@UserCreate
	public void create(User user) throws UsernameAlreadyExistsException, BusinessException {

		if (isUsernameExists(user.getUserName())) {
			throw new UsernameAlreadyExistsException(user.getUserName());
		}

		user.setUserName(user.getUserName().toUpperCase());
		user.setPassword(Sha1Encrypt.encodePassword(user.getPassword()));
		user.setLastPasswordModification(new Date());
		// Set provider to the first provider from a user related provider list
		user.setProvider(user.getProviders().iterator().next());

		super.create(user);
	}

	@Override
	@UserUpdate
	public User update(User user) throws UsernameAlreadyExistsException {
		if (isUsernameExists(user.getUserName(), user.getId())) {
			getEntityManager().refresh(user);
			throw new UsernameAlreadyExistsException(user.getUserName());
		}

		user.setUserName(user.getUserName().toUpperCase());
		if (!StringUtils.isBlank(user.getNewPassword())) {
			String encryptedPassword = Sha1Encrypt.encodePassword(user.getPassword());
			user.setPassword(encryptedPassword);
		}

		return super.update(user);
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
		String queryString = "select distinct u from User u join u.roles as r where r.name in (:roles) and u.provider=:provider";
		Query query = getEntityManager().createQuery(queryString);
		query.setParameter("roles", Arrays.asList(roles));
		query.setParameter("provider", getCurrentProvider());
		query.setHint("org.hibernate.flushMode", "NEVER");
		return query.getResultList();
	}

	public boolean isUsernameExists(String username, Long id) {
		String stringQuery = "select count(*) from User u where u.userName = :userName and u.id <> :id";
		Query query = getEntityManager().createQuery(stringQuery);
		query.setParameter("userName", username.toUpperCase());
		query.setParameter("id", id);
		query.setHint("org.hibernate.flushMode", "NEVER");
		return ((Long) query.getSingleResult()).intValue() != 0;
	}

	public boolean isUsernameExists(String username) {
		String stringQuery = "select count(*) from User u where u.userName = :userName";
		Query query = getEntityManager().createQuery(stringQuery);
		query.setParameter("userName", username.toUpperCase());
		query.setHint("org.hibernate.flushMode", "NEVER");
		return ((Long) query.getSingleResult()).intValue() != 0;
	}

	public User findByUsernameAndPassword(String username, String password) {
		return findByUsernameAndPassword(getEntityManager(), username, password);
	}

	public User findByUsernameAndPassword(EntityManager em, String username, String password) {
		return findByUsernameAndPassword(em, username, password, Arrays.asList("roles", "provider", "providers"));
	}

	public User findByUsernameAndPassword(EntityManager em, String username, String password, List<String> fetchFields) {

		password = Sha1Encrypt.encodePassword(password);

		QueryBuilder qb = new QueryBuilder(User.class, "u", fetchFields, null);

		qb.addCriterion("userName", "=", username.toUpperCase(), true);
		qb.addCriterion("password", "=", password, true);

		try {
			return (User) qb.getQuery(em).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByUsername(String username) {
		QueryBuilder qb = new QueryBuilder(User.class, "u");

		qb.addCriterion("userName", "=", username, true);

		try {
			return (User) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByEmail(String email) {
		try {
			return (User) getEntityManager().createQuery("from User where email = :email").setParameter("email", email)
					.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User changePassword(User user, String newPassword) throws BusinessException {
		getEntityManager().refresh(user);
		user.setLastPasswordModification(new Date());
		user.setPassword(Sha1Encrypt.encodePassword(newPassword));
		super.update(user);
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllRolesExcept(String rolename1, String rolename2) {
		return getEntityManager().createQuery("from MeveoRole as r where r.name<>:name1 and r.name<>:name2")
				.setParameter("name1", rolename1).setParameter("name2", rolename2).getResultList();
	}

	public Role getRoleByName(String name) {
		return (Role) getEntityManager().createQuery("from MeveoRole as r where r.name=:name")
				.setParameter("name", name).getSingleResult();
	}

	public User loginChecks(String username, String password) throws LoginException {
		return loginChecks(username, password, false);
	}

	public User loginChecks(EntityManager em, String username, String password, boolean skipPasswordExpiracy)
			throws UnknownUserException, PasswordExpiredException, LoginException {
		User user = findByUsernameAndPassword(em, username, password);

		// check if the user exists
		if (user == null) {
			throw new UnknownUserException(username);
		}

		return loginChecks(user, skipPasswordExpiracy);
	}

	public User loginChecks(String username, String password, boolean skipPasswordExpiracy) throws LoginException {
		User user = findByUsernameAndPassword(username, password);
		if (skipPasswordExpiracy) {
			// log.debug("[UserService] Skipping expiry check asked");
		} else {
			// log.debug("[UserService] Checking expiry asked");
		}

		// check if the user exists
		if (user == null) {
			throw new UnknownUserException(username);
		}

		return loginChecks(user, skipPasswordExpiracy);
	}

	public User loginChecks(User user, boolean skipPasswordExpiracy) throws LoginException {

		// Check if the user is active
		if (!user.isActive()) {
			log.info("The user " + user.getId() + " is not active.");
			throw new InactiveUserException("The user " + user.getId() + " is not active.");
		}

		// Check if the user password has expired
		String passwordExpiracy = paramBean.getProperty("password.Expiracy", "90");

		if (!skipPasswordExpiracy && user.isPasswordExpired(Integer.parseInt(passwordExpiracy))) {
			log.info("The password of user with id=" + user.getId() + " has expired.");
			throw new PasswordExpiredException("The password of user with id=" + user.getId() + " has expired.");
		}

		// Check the roles
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			log.info("The user with id=" + user.getId() + " has no role!");
			throw new NoRoleException("The user with id=" + user.getId() + " has no role!");
		}

		// TODO needed to overcome lazy loading issue. Remove once solved
		for (Role role : user.getRoles()) {
			for (org.meveo.model.security.Permission permission : role.getPermissions()) {
				permission.getName();
			}
		}

		for (Provider provider : user.getProviders()) {
			provider.getCode();
			if (provider.getLanguage() != null) {
				provider.getLanguage().getLanguageCode();
				for (TradingLanguage language : provider.getTradingLanguages()) {
					language.getLanguageCode();
				}
			}
			for (WalletTemplate template : provider.getPrepaidWalletTemplates()) {
				template.getCode();
			}
		}

		// End lazy loading issue

		return user;
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

		newUser.setName(new org.meveo.model.shared.Name(title, firstName, lastName));

		newUser.setDisabled(newUser.isDisabled());
		newUser.setUserName(user.getUserName() + "_NEW");
		newUser.setRoles(new HashSet<Role>(user.getRoles()));

		log.debug("End of duplication of User entity");

		return newUser;
	}

	public void saveActivity(User user, String objectId, String action, String uri) {
		// String sequenceValue = "ADM_USER_LOG_SEQ.nextval";
		String sequenceValueTest = paramBean.getProperty("sequence.test", "false");
		if (!sequenceValueTest.equals("true")) {

			String stringQuery = "INSERT INTO ADM_USER_LOG (USER_NAME, USER_ID, DATE_EXECUTED, ACTION, URL, OBJECT_ID) VALUES ( ?, ?, ?, ?, ?, ?)";

			Query query = getEntityManager().createNativeQuery(stringQuery);
			query.setParameter(1, user.getUserName());
			query.setParameter(2, user.getId());
			query.setParameter(3, new Date());
			query.setParameter(4, action);
			query.setParameter(5, uri);
			query.setParameter(6, objectId);
			query.executeUpdate();
		}
	}

	public User findByUsernameWithFetch(String username, List<String> fetchFields) {
		QueryBuilder qb = new QueryBuilder(User.class, "u", fetchFields, null);

		qb.addCriterion("userName", "=", username, true);

		try {
			return (User) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Check entity provider. If current provider is not same as entity provider
	 * exception is thrown since different provider should not be allowed to
	 * modify (update or delete) entity. Only in User management case, provider
	 * attribute should be ignored as user can be tied to several providers.
	 */
	@Override
	protected void checkProvider(User entity) {
		// Super administrator - don't care
		if (identity.hasPermission("superAdmin", "superAdminManagement")) {
			return;
			// Other users - a regular check
		} else {
			super.checkProvider(entity);
		}
	}

	public User findByIdLoadProvider(Long id) {
		QueryBuilder qb = new QueryBuilder(User.class, "u", Arrays.asList("provider"), null);
		qb.addCriterion("u.id", "=", id, true);

		try {
			return (User) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}