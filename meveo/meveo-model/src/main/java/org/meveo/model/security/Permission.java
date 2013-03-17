package org.meveo.model.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADM_PERMISSION")
public class Permission {

    @Id
    private Long id;

    @Column(name = "RESOURCE", nullable = false)
    private String resource;

    @Column(name = "PERMISSION", nullable = false)
    private String permission;

    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Permission [name=" + name + ", resource=" + resource + ", permission=" + permission + "]";
    }
}