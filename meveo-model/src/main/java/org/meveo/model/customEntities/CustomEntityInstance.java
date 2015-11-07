package org.meveo.model.customEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;

@Entity
@CustomFieldEntity(cftCodePrefix = "CE", cftCodeFields = "cetCode")
@ExportIdentifier({ "code", "cetCode", "provider" })
@Table(name = "CUST_CEI", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "CET_CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CUST_CEI_SEQ")
public class CustomEntityInstance extends BusinessCFEntity {

    private static final long serialVersionUID = 8281478284763353310L;

    @Column(name = "CET_CODE", length = 100, nullable = false)
    public String cetCode;

    @Override
    public ICustomFieldEntity getParentCFEntity() {
        return null;
    }

    public String getCetCode() {
        return cetCode;
    }

    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof CustomEntityInstance)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CustomEntityInstance other = (CustomEntityInstance) obj;

        if (getId() != null && other.getId() != null && getId() == other.getId()) {
            // return true;
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