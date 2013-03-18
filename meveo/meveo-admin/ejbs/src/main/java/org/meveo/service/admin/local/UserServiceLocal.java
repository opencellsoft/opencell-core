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
package org.meveo.service.admin.local;

import java.util.List;

import javax.ejb.Local;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.LoginException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.service.base.local.IPersistenceService;

/**
 * User service local interface.
 * 
 * @author Gediminas Ubartas
 * @created 2010.05.31
 */
@Local
public interface UserServiceLocal extends IPersistenceService<User> {
    /**
     * Returns system user with administrators user role
     */
    public User getSystemUser();

    /**
     * Returns system user with administrators user role
     * 
     * @param roles
     *            All roles
     * @return List of users with roles
     */
    public List<User> findUsersByRoles(String... roles);

    /**
     * Checks if user with username and id exists
     * 
     * @param username
     *            Users username
     * @param id
     * @return value if user exists
     * 
     */
    public boolean isUsernameExists(String username, Long id);

    /**
     * Finds user with current username and password
     * 
     * @param username
     *            User name
     * @param password
     *            user password
     * @return Existing user with current username and password
     */
    public User findByUsernameAndPassword(String username, String password);

    /**
     * Finds user with current username
     * 
     * @param username
     *            User name
     * 
     * @return Existing user with current username
     */
    public User findByUsername(String username);

    /**
     * Returns user roles without some roles
     * 
     * @param rolename1
     *            Role to exclude
     * @param rolename2
     *            Role to exclude
     * @return List of roles
     */
    public List<Role> getAllRolesExcept(String rolename1, String rolename2);

    /**
     * Returns user role
     * 
     * @param name
     *            Role name
     * @return Role with current name
     */
    public Role getRoleByName(String name);

    public void login(User currentUser) throws LoginException;

    /**
     * Creates new user
     * 
     * @param user
     *            User object
     */
    public void create(User user) throws UsernameAlreadyExistsException;

    /**
     * Updates user
     * 
     * @param user
     *            User object
     */
    public void update(User user) throws UsernameAlreadyExistsException;

    /**
     * Removes user
     * 
     * @param user
     *            User object
     */
    public void remove(User user);

    /**
     * Duplicates User
     * 
     * @param user
     *            User object
     */
    public User duplicate(User user);

    /**
     * Finds user by email
     * 
     * @param email
     *            Users email
     */
    public User findByEmail(String email);

    /**
     * Changes user Password
     * 
     * @param user
     *            User object
     * @param newPassword
     *            New password
     * @return User with changed password
     */
    public User changePassword(User user, String newPassword) throws BusinessException;

    /**
     * Saves user actions
     * 
     * @param user
     *            User object
     * @param objectId
     *            Used object id
     * @param action
     *            Action done with object
     * @param uri
     *            URL of action
     */
    public void saveActivity(User user, String objectId, String action, String uri);
}