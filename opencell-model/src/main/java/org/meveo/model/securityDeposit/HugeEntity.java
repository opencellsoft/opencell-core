package org.meveo.model.securityDeposit;

import java.util.List;

public class HugeEntity {
    private String entityClass;

    private List<String> hugeLists;

    private List<String> mandatoryFields;

    public String getEntityClass() {
        return entityClass;
    }

    public HugeEntity setEntityClass(String entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    public List<String> getHugeLists() {
        return hugeLists;
    }

    public HugeEntity setHugeLists(List<String> hugeLists) {
        this.hugeLists = hugeLists;
        return this;
    }

    public List<String> getMandatoryFields() {
        return mandatoryFields;
    }

    public HugeEntity setMandatoryFields(List<String> mandatoryFields) {
        this.mandatoryFields = mandatoryFields;
        return this;
    }
}
