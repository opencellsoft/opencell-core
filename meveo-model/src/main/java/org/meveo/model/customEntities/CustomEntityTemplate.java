package org.meveo.model.customEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.security.Permission;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CUST_CET", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CUST_CET_SEQ")
@NamedQueries({ @NamedQuery(name = "CustomEntityTemplate.getCETForCache", query = "SELECT cet from CustomEntityTemplate cet left join fetch cet.permission permis where cet.disabled=false  ") })
public class CustomEntityTemplate extends BusinessEntity implements Comparable<CustomEntityTemplate> {

    private static final long serialVersionUID = 8281478284763353310L;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "PERMISSION_ID")
    private Permission permission;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getPermissionResource() {
        if (permission != null) {
            return permission.getResource();
        }
        return null;
    }

    public String getPermissionAction() {
        if (permission != null) {
            return permission.getPermission();
        }
        return null;
    }

    public String getCftPrefix() {
        return "CE_" + getCode();
    }

    @Override
    public int compareTo(CustomEntityTemplate cet1) {
        return StringUtils.compare(name, cet1.getName());
    }
}