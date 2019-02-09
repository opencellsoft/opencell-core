package org.meveo.model.customEntities;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

/**
 * Custom entity template
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cust_cet", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cust_cet_seq"), })
@NamedQueries({ @NamedQuery(name = "CustomEntityTemplate.getCETForCache", query = "SELECT cet from CustomEntityTemplate cet where cet.disabled=false order by cet.name ") })
public class CustomEntityTemplate extends EnableBusinessEntity implements Comparable<CustomEntityTemplate> {

    private static final long serialVersionUID = 8281478284763353310L;

    public static String CFT_PREFIX = "CE";

    /**
     * Template name
     */
    @Column(name = "name", length = 100, nullable = false)
    @Size(max = 100)
    @NotNull
    private String name;

    /**
     * Should data be stored in a separate table
     */
    @Type(type = "numeric_boolean")
    @Column(name = "store_as_table", nullable = false)
    @NotNull
    private boolean storeAsTable;

    /**
     * A database table name derived from a code value
     */
    @Transient
    private String dbTablename;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppliesTo() {
        return CFT_PREFIX + "_" + getCode();
    }

    public static String getAppliesTo(String code) {
        return CFT_PREFIX + "_" + code;
    }

    public String getReadPermission() {
        return CustomEntityTemplate.getReadPermission(code);
    }

    public String getModifyPermission() {
        return CustomEntityTemplate.getModifyPermission(code);
    }

    @Override
    public int compareTo(CustomEntityTemplate cet1) {
        return StringUtils.compare(name, cet1.getName());
    }

    public static String getReadPermission(String code) {
        return "CE_" + code + "-read";
    }

    public static String getModifyPermission(String code) {
        return "CE_" + code + "-modify";
    }

    public boolean isStoreAsTable() {
        return storeAsTable;
    }

    public void setStoreAsTable(boolean storeAsTable) {
        this.storeAsTable = storeAsTable;
    }

    /**
     * Get a database table name derived from a code value. Lowercase and spaces replaced by "_".
     * 
     * @return Database field name
     */
    public String getDbTablename() {
        if (dbTablename == null && code != null) {
            dbTablename = BaseEntity.cleanUpAndLowercaseCodeOrId(code);
        }
        return dbTablename;
    }
}