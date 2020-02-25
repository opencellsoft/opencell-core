package org.meveo.model.billing;

/**
 * An Enum to override prorata setting in the recurring charge when terminating a subscription
 * NO_OVERRIDE: which keeps the proata setting set on the recurring charge.
 * PRORATA :  applying the prorata
 * NO_PRORATA :  ignoring  the prorata
 *
 * @author Horri khalid
 */
public enum OverrideProrataEnum {
    NO_OVERRIDE(1, "overrideProrataEnum.noOverride"), PRORATA(2, "overrideProrataEnum.prorata"), NO_PRORATA(3, "overrideProrataEnum.noProrata");

    private String label;
    private Integer id;

    OverrideProrataEnum(Integer id, String label) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
