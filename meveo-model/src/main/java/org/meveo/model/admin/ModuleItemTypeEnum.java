package org.meveo.model.admin;

public enum ModuleItemTypeEnum {
    CET("meveoModuleItemType.CET"), CFT("meveoModuleItemType.CFT"), FILTER("meveoModuleItemType.filter"), SCRIPT("meveoModuleItemType.script"), JOBINSTANCE(
            "meveoModuleItemType.jobInstance"), NOTIFICATION("meveoModuleItemType.notification"), SUBMODULE("meveoModuleItemType.subModule"), MEASURABLEQUANTITIES(
            "meveoModuleItemType.measurableQuantities"), CHART("meveoModuleItemType.chart");

    private String label;

    ModuleItemTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}