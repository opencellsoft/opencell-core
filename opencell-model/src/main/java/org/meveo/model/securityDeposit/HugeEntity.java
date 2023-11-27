package org.meveo.model.securityDeposit;

import java.util.List;

public class HugeEntity {
    private String entityClass;

    private List<String> hugeLists;

    private List<String> mandatoryFilterFields;

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

    public List<String> getMandatoryFilterFields() {
        return mandatoryFilterFields;
    }

    public HugeEntity setMandatoryFilterFields(List<String> mandatoryFilterFields) {
        this.mandatoryFilterFields = mandatoryFilterFields;
        return this;
    }
}
