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
package org.meveo.model.hierarchy;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.meveo.model.admin.User;

@Entity
@Cacheable
@DiscriminatorValue(value = "USER_TYPE")
public class UserHierarchyLevel extends HierarchyLevel<User> {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "userLevel", fetch = FetchType.LAZY)
    private Set<User> users;

    public UserHierarchyLevel() {
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    /**
     * Check that user belongs to a current or any of child levels
     * 
     * @param userToCheck User to verify
     * @return True if user belongs to a current or any of child levels
     */
    @SuppressWarnings("rawtypes")
    public boolean isUserBelongsHereOrBellow(User userToCheck) {
        if (users.contains(userToCheck)) {
            return true;
        }

        for (HierarchyLevel childLevel : getChildLevels()) {
            if (((UserHierarchyLevel) childLevel).isUserBelongsHereOrBellow(userToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that user belongs to a current or any of parent levels
     * 
     * @param userToCheck User to verify
     * @return True if user belongs to a current or any of child levels
     */
    public boolean isUserBelongsHereOrHigher(User userToCheck) {
           
        if (getUsers().contains(userToCheck)) {
            return true;
        }

        // TODO fix me - throws class cast exception
        // if (getParentLevel() != null) {
        // return ((UserHierarchyLevel) getParentLevel()).isUserBelongsHereOrHigher(userToCheck);
        // }
        return false;
    }
}