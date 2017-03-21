package org.meveo.model.notification;

public enum StrategyImportTypeEnum {

UPDATED(1,"enum.strategyImportTypeEnum.UPDATED"),
REJECTE_IMPORT(2,"enum.strategyImportTypeEnum.REJECTE_IMPORT"),
REJECT_EXISTING_RECORDS(3,"enum.strategyImportTypeEnum.REJECT_EXISTING_RECORDS");

private Integer id;
private String label;

StrategyImportTypeEnum(Integer id, String label) {
    this.id = id;
    this.label = label;
}

public Integer getId() {
    return id;
}

public String getLabel() {
    return this.label;
}


public static StrategyImportTypeEnum getValue(Integer id) {
    if (id != null) {
        for (StrategyImportTypeEnum type : values()) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
    }
    return null;
}


}
