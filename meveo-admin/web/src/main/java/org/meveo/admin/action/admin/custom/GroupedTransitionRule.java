package org.meveo.admin.action.admin.custom;

import java.io.Serializable;

import org.meveo.model.wf.WFTransitionRule;

public class GroupedTransitionRule implements Serializable {

    private static final long serialVersionUID = 5027554537383208719L;

    private String name;

    private WFTransitionRule value;

    private String newValue;

    private String anotherValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WFTransitionRule getValue() {
        return value;
    }

    public void setValue(WFTransitionRule value) {
        this.value = value;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getAnotherValue() {
        return anotherValue;
    }

    public void setAnotherValue(String anotherValue) {
        this.anotherValue = anotherValue;
    }
}