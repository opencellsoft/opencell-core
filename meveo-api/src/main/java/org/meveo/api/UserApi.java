package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.util.security.Sha1Encrypt;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class UserApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	@Inject
	private RoleService roleService;

	@Inject
	private UserService userService;

	public void create(UserDto postData, User currentUser)
			throws MissingParameterException, EntityDoesNotExistsException,
			EntityAlreadyExistsException {
		if (!StringUtils.isBlank(postData.getUsername())
				&& !StringUtils.isBlank(postData.getProvider())
				&& !StringUtils.isBlank(postData.getRole())
				&& !StringUtils.isBlank(postData.getLastName())) {
			// find provider
			Provider provider = providerService.findByCode(postData
					.getProvider());
			if (provider == null) {
				throw new EntityDoesNotExistsException(Provider.class,
						postData.getProvider());
			}

			// check if the user already exists
			if (userService.findByUsername(postData.getUsername()) != null) {
				throw new EntityAlreadyExistsException(User.class,
						postData.getUsername(), "username");
			}

			// find role
			Role role = roleService.findByName(postData.getRole());
			if (role == null) {
				throw new EntityDoesNotExistsException(Role.class,
						postData.getRole());
			}

			User user = new User();
			user.setUserName(postData.getUsername());
			Name name = new Name();
			name.setLastName(postData.getLastName());
			name.setFirstName(postData.getFirstName());
			user.setName(name);
			user.setProvider(provider);
			user.setPassword(Sha1Encrypt.encodePassword(postData.getPassword()));
			Set<Role> roles = new HashSet<Role>();
			roles.add(role);
			user.setRoles(roles);

			userService.create(user, currentUser, provider);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getUsername())) {
				missingFields.add("username");
			}
			if (StringUtils.isBlank(postData.getProvider())) {
				missingFields.add("provider");
			}
			if (StringUtils.isBlank(postData.getRole())) {
				missingFields.add("role");
			}
			if (StringUtils.isBlank(postData.getLastName())) {
				missingFields.add("lastName");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void update(UserDto postData, User currentUser)
			throws EntityDoesNotExistsException, MissingParameterException {
		if (!StringUtils.isBlank(postData.getUsername())
				&& !StringUtils.isBlank(postData.getProvider())
				&& !StringUtils.isBlank(postData.getRole())
				&& !StringUtils.isBlank(postData.getLastName())) {
			// find user
			User user = userService.findByUsername(postData.getUsername());

			if (user == null) {
				throw new EntityDoesNotExistsException(User.class,
						postData.getUsername(), "username");
			}

			// find provider
			Provider provider = providerService.findByCode(postData
					.getProvider());
			if (provider == null) {
				throw new EntityDoesNotExistsException(Provider.class,
						postData.getProvider());
			}

			// find role
			Role role = roleService.findByName(postData.getRole());
			if (role == null) {
				throw new EntityDoesNotExistsException(Role.class,
						postData.getRole());
			}

			user.setUserName(postData.getUsername());
			Name name = new Name();
			name.setLastName(postData.getLastName());
			name.setFirstName(postData.getFirstName());
			user.setName(name);
			user.setProvider(provider);
			Set<Role> roles = new HashSet<Role>();
			roles.add(role);
			user.setRoles(roles);

			userService.create(user, currentUser, provider);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getUsername())) {
				missingFields.add("username");
			}
			if (StringUtils.isBlank(postData.getProvider())) {
				missingFields.add("provider");
			}
			if (StringUtils.isBlank(postData.getRole())) {
				missingFields.add("role");
			}
			if (StringUtils.isBlank(postData.getLastName())) {
				missingFields.add("lastName");
			}

			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void remove(String username) throws EntityDoesNotExistsException {
		User user = userService.findByUsername(username);

		if (user == null) {
			throw new EntityDoesNotExistsException(User.class, username,
					"username");
		}

		userService.remove(user);
	}

	public UserDto find(String username) throws EntityDoesNotExistsException {
		User user = userService.findByUsernameWithFetch(username,
				Arrays.asList("provider", "roles"));

		if (user == null) {
			throw new EntityDoesNotExistsException(User.class, username,
					"username");
		}

		UserDto result = new UserDto(user);

		return result;
	}

}
