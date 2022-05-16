package org.meveo.security;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.GroupRepresentation;

/**
 * User group representation in Keycloak
 */
public class UserGroup implements Comparable<UserGroup> {

    /**
     * Parent group path
     */
    String parentGroup;

    /**
     * Group name
     */
    String name;

    /*
     * Children of a group
     */
    List<UserGroup> childGroups;

    /**
     * Constructor
     * 
     * @param userGroup User group representation in Keycloak
     */
    public UserGroup(GroupRepresentation userGroup) {
        this.parentGroup = userGroup.getPath().substring(0, userGroup.getPath().lastIndexOf('/'));
        this.name = userGroup.getName();
        if (userGroup.getSubGroups() != null) {
            childGroups = userGroup.getSubGroups().stream().map(g -> new UserGroup(g)).collect(Collectors.toList());
        }
    }

    /**
     * @return Parent group path
     */
    public String getParentGroup() {
        return parentGroup;
    }

    public String getName() {
        return name;
    }

    public List<UserGroup> getChildGroups() {
        return childGroups;
    }

    @Override
    public int compareTo(UserGroup o) {
        return name.compareTo(o.getName());
    }
}