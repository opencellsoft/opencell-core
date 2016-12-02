package org.meveo.model.customEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@CustomFieldEntity(cftCodePrefix = "CE", cftCodeFields = "cetCode")
@ExportIdentifier({ "code", "cetCode", "provider" })
@Table(name = "CUST_CEI", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "CET_CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CUST_CEI_SEQ")
public class CustomEntityInstance extends BusinessCFEntity {

    private static final long serialVersionUID = 8281478284763353310L;

    @Column(name = "CET_CODE", length = 50, nullable = false)
    @Size(max = 50)
    @NotNull
    public String cetCode;

    @Column(name = "PARENT_UUID", updatable = false, length = 60)
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

    @Override
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
        } else if (!code.equals(other.getCode())) {
            return false;
        } else if (cetCode == null && other.getCetCode() != null) {
            return false;
        } else if (!cetCode.equals(other.getCetCode())) {
            return false;
        }
        return true;
    }
}