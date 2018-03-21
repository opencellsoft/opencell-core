package org.meveo.model.crm.custom;

public enum CustomFieldInheritanceEnum {
    INHERIT_NONE, INHERIT_NO_MERGE, INHERIT_MERGED;

    public static CustomFieldInheritanceEnum getInheritCF(boolean inherit, boolean merge) {
        if (inherit && merge) {
            return INHERIT_MERGED;
        } else if (inherit) {
            return INHERIT_NO_MERGE;
        } else {
            return INHERIT_NONE;
        }
    }
}
