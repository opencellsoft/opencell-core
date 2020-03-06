/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.hierarchy;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.meveo.model.BusinessEntity;
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

    @Override
    public Class<? extends BusinessEntity> getParentEntityType() {
        return UserHierarchyLevel.class;
    }
}