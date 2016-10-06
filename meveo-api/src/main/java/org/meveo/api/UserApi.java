package org.meveo.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.SecuredEntityDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.LoginException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.security.SecuredBusinessEntityService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UserApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	@Inject
	private RoleService roleService;

	@Inject
	private UserService userService;

    @Inject
	private SecuredBusinessEntityService securedBusinessEntityService;
    
	@Inject
    private UserHierarchyLevelService userHierarchyLevelService;

	public void create(UserDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getUsername())) {
			missingParameters.add("username");
		}
		if (StringUtils.isBlank(postData.getEmail())) {
			missingParameters.add("email");
		}

		if ((postData.getRoles() == null || postData.getRoles().isEmpty()) && StringUtils.isBlank(postData.getRole())) {
			missingParameters.add("roles");
		}

		handleMissingParameters();

		if (!StringUtils.isBlank(postData.getRole())) {
			if (postData.getRoles() == null) {
				postData.setRoles(new ArrayList<String>());
			}
			postData.getRoles().add(postData.getRole());
		}

		// Find provider and check if user has access to manage that provider
		// data
		Provider provider = null;
		if (!StringUtils.isBlank(postData.getProvider())) {
			provider = providerService.findByCode(postData.getProvider());
			if (provider == null) {
				throw new EntityDoesNotExistsException(Provider.class, postData.getProvider());
			}
		} else {
			provider = currentUser.getProvider();
		}

		if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationManagement") && provider.equals(currentUser.getProvider())))) {
			throw new LoginException("User has no permission to manage users for provider " + provider.getCode());
		}

		// check if the user already exists
		if (userService.findByUsername(postData.getUsername()) != null) {
			throw new EntityAlreadyExistsException(User.class, postData.getUsername(), "username");
		}

		// find role
		Set<Role> roles = extractRoles(postData, provider);

		// parse secured entities
		List<SecuredEntity> securedEntities = extractSecuredEntities(postData, currentUser);

        UserHierarchyLevel userHierarchyLevel = null;
        if(!StringUtils.isBlank(postData.getUserLevel())){
            userHierarchyLevel = userHierarchyLevelService.findByCode(postData.getUserLevel(), provider);
            if (userHierarchyLevel == null) {
                throw new EntityDoesNotExistsException(UserHierarchyLevel.class, "userLevel");
            }
        }

		User user = new User();
		user.setUserName(postData.getUsername().toUpperCase());
		user.setEmail((postData.getEmail()));
		Name name = new Name();
		name.setLastName(postData.getLastName());
		name.setFirstName(postData.getFirstName());
		user.setName(name);
		user.setPassword(postData.getPassword());
		user.setLastPasswordModification(new Date());
		user.setProvider(provider);
		user.setRoles(roles);
		user.setSecuredEntities(securedEntities);
        user.setUserLevel(userHierarchyLevel);

		userService.create(user, currentUser);
	}

	public void update(UserDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getUsername())) {
			missingParameters.add("username");
		}
		handleMissingParameters();
		
		//we support old dto that containt only one role
		if (!StringUtils.isBlank(postData.getRole())) {
			if (postData.getRoles() == null) {
				postData.setRoles(new ArrayList<String>());
			}
			postData.getRoles().add(postData.getRole());
		}

		// find user
		User user = userService.findByUsername(postData.getUsername());

		if (user == null) {
			throw new EntityDoesNotExistsException(User.class, postData.getUsername(), "username");
		}

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationVisualization") && user.getProvider().equals(currentUser.getProvider())))) {
			throw new LoginException("User has no permission to manage users for provider " + user.getProvider().getCode());
		}

		// find roles
		Set<Role> roles = extractRoles(postData, user.getProvider());

		// parse secured entities
		List<SecuredEntity> securedEntities = extractSecuredEntities(postData, currentUser);

        UserHierarchyLevel userHierarchyLevel = null;
        if(!StringUtils.isBlank(postData.getUserLevel())){
            userHierarchyLevel = userHierarchyLevelService.findByCode(postData.getUserLevel(), user.getProvider());
            if (userHierarchyLevel == null) {
                throw new EntityDoesNotExistsException(UserHierarchyLevel.class, "userLevel");
            }
        }

		user.setUserName(postData.getUsername());
		if (!StringUtils.isBlank(postData.getEmail())) {
			user.setEmail(postData.getEmail());
		}
		if (!StringUtils.isBlank(postData.getPassword())) {
			user.setNewPassword(postData.getPassword());
		}
		Name name = new Name();
		if (!StringUtils.isBlank(postData.getLastName())) {
			name.setLastName(postData.getLastName());
			user.setName(name);
		}
		if (!StringUtils.isBlank(postData.getFirstName())) {
			name.setFirstName(postData.getFirstName());
			user.setName(name);
		}
		if (!roles.isEmpty()) {
			user.setRoles(roles);
		}
		user.setSecuredEntities(securedEntities);
        user.setUserLevel(userHierarchyLevel);

		userService.update(user, currentUser);
	}

	private Set<Role> extractRoles(UserDto postData, Provider provider) throws EntityDoesNotExistsException {
		Set<Role> roles = new HashSet<Role>();
		if(postData.getRoles() == null){
			return roles;
		}
		for (String rl : postData.getRoles()) {
			Role role = roleService.findByName(rl, provider);
			if (role == null) {
				throw new EntityDoesNotExistsException(Role.class, rl);
			}
			roles.add(role);
		}
		return roles;
	}

	private List<SecuredEntity> extractSecuredEntities(UserDto postData, User currentUser) throws EntityDoesNotExistsException {
		List<SecuredEntity> securedEntities = new ArrayList<>();
		if (postData.getSecuredEntities() != null) {
			SecuredEntity securedEntity = null;
			for (SecuredEntityDto securedEntityDto : postData.getSecuredEntities()) {
				securedEntity = new SecuredEntity();
				securedEntity.setCode(securedEntityDto.getCode());
				securedEntity.setEntityClass(securedEntityDto.getEntityClass());
				BusinessEntity businessEntity = securedBusinessEntityService.getEntityByCode(securedEntity.getEntityClass(), securedEntity.getCode(), currentUser);
				if (businessEntity == null) {
					throw new EntityDoesNotExistsException(securedEntity.getEntityClass(), securedEntity.getCode());
				}
				securedEntities.add(securedEntity);
			}
		}
		return securedEntities;
	}

	public void remove(String username, User currentUser) throws MeveoApiException, BusinessException {
		User user = userService.findByUsername(username);

		if (user == null) {
			throw new EntityDoesNotExistsException(User.class, username, "username");
		}

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationVisualization") && user.getProvider().equals(currentUser.getProvider())))) {
			throw new LoginException("User has no permission to manage users for provider " + user.getProvider().getCode());
		}

		userService.remove(user, currentUser);
	}

	public UserDto find(String username, User currentUser) throws MeveoApiException {

		if (StringUtils.isBlank(username)) {
			missingParameters.add("username");
		}

		handleMissingParameters();

        User user = userService.findByUsernameWithFetch(username, Arrays.asList("provider", "roles", "userLevel"));

		if (user == null) {
			throw new EntityDoesNotExistsException(User.class, username, "username");
		}

        if (!(currentUser.hasPermission("superAdmin", "superAdminManagement") || (currentUser.hasPermission("administration", "administrationVisualization") && user.getProvider().equals(currentUser.getProvider())))) {
			throw new LoginException("User has no permission to access users for provider " + user.getProvider().getCode());
		}

		UserDto result = new UserDto(user);

		return result;
	}

	public void createOrUpdate(UserDto postData, User currentUser) throws MeveoApiException, BusinessException {
		User user = userService.findByUsername(postData.getUsername());
		if (user == null) {
			create(postData, currentUser);
		} else {
			update(postData, currentUser);
		}
	}
}
