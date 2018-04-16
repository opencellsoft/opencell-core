package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.security.Permission;

/**
 * The Class PermissionDto.
 */
@XmlRootElement(name = "Permission")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The permission. */
    @XmlAttribute(required = true)
    private String permission;

    /** The name. */
    @XmlAttribute(required = true)
    private String name;

    /**
     * Instantiates a new permission dto.
     */
    public PermissionDto() {

    }

    /**
     * Instantiates a new permission dto.
     *
     * @param permission the permission
     */
    public PermissionDto(Permission permission) {
        if (permission != null) {
            this.name = permission.getName();
            this.permission = permission.getPermission();
        }
    }

    /**
     * Gets the permission.
     *
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission.
     *
     * @param permission the new permission
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PermissionDto [permission=" + permission + ", name=" + name + "]";
    }
}
