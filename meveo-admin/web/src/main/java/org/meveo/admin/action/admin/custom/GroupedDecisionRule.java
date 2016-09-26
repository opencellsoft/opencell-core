package org.meveo.admin.action.admin.custom;

import java.io.Serializable;
import java.util.Date;

import org.meveo.model.wf.WFDecisionRule;

public class GroupedDecisionRule implements Serializable {

    private static final long serialVersionUID = 5027554537383208719L;

    private String name;

    private WFDecisionRule value;

    private String newValue;

    private Date newDate;

    private String anotherValue;

    private Date anotherDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WFDecisionRule getValue() {
        return value;
    }

    public void setValue(WFDecisionRule value) {
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

    public Date getNewDate() {
        return newDate;
    }

    public void setNewDate(Date newDate) {
        this.newDate = newDate;
    }

    public Date getAnotherDate() {
        return anotherDate;
    }

    public void setAnotherDate(Date anotherDate) {
        this.anotherDate = anotherDate;
    }
}