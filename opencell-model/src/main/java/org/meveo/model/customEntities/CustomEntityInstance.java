package org.meveo.model.customEntities;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Custom entity instance
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "CE", cftCodeFields = "cetCode")
@ExportIdentifier({ "code", "cetCode" })
@Table(name = "cust_cei", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "cet_code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cust_cei_seq"), })
public class CustomEntityInstance extends EnableBusinessCFEntity {

    private static final long serialVersionUID = 8281478284763353310L;

    /**
     * Custom entity template code
     */
    @Column(name = "cet_code", length = 255, nullable = false)
    @Size(max = 255)
    @NotNull
    public String cetCode;

    /**
     * Parent entity unique identifier UUID. Used only as part of Custom field Embedded entity data type.
     */
    @Column(name = "parent_uuid", updatable = false, length = 60)
    @Size(max = 60)
    public String parentEntityUuid;

    public String getCetCode() {
        return cetCode;
    }

    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    public void setParentEntityUuid(String parentEntityUuid) {
        this.parentEntityUuid = parentEntityUuid;
    }

    public String getParentEntityUuid() {
        return parentEntityUuid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cetCode == null) ? 0 : cetCode.hashCode());
        result = prime * result + ((parentEntityUuid == null) ? 0 : parentEntityUuid.hashCode());
        return result;
    }

    /**
     * @see org.meveo.model.BusinessEntity#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomEntityInstance)) {
            return false;
        }

        CustomEntityInstance other = (CustomEntityInstance) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (code == null && other.getCode() != null) {
            return false;
        } else if (code != null && !code.equals(other.getCode())) {
            return false;
        } else if (cetCode == null && other.getCetCode() != null) {
            return false;
        } else if (cetCode != null && !cetCode.equals(other.getCetCode())) {
            return false;
        }
        return true;
    }

}