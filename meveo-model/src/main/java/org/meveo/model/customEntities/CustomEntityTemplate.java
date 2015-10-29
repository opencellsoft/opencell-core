package org.meveo.model.customEntities;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CUST_CET", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CUST_CET_SEQ")
public class CustomEntityTemplate extends BusinessEntity {

    private static final long serialVersionUID = 8281478284763353310L;

    public String getCFTPrefix() {
        return "CET_" + getCode();
    }
}