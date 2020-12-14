package org.meveo.model.generic.wf;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "wf_generic_action", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "wf_generic_action_seq") })

public class Action extends BaseEntity implements Comparable<Action> {

    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    @Column(name = "priority")
    private int priority;

    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    private String conditionEl;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "log_level")
    private String logLevel;

    @Column(name = "field_to_update")
    private String fieldToUpdate;

    @Column(name = "is_asynchronous")
    @Type(type = "numeric_boolean")

    private boolean asynchronous;

    @Column(name = "value_el", length = 2000)
    @Size(max = 2000)
    private String valueEL;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "action_script_id")
    private ScriptInstance actionScript;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @ManyToOne
    @JoinColumn(name = "transition_id")
    private GWFTransition transition;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditionEl() {
        return conditionEl;
    }

    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public ScriptInstance getActionScript() {
        return actionScript;
    }

    public void setActionScript(ScriptInstance actionScript) {
        this.actionScript = actionScript;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public GWFTransition getTransition() {
        return transition;
    }

    public void setTransition(GWFTransition transition) {
        this.transition = transition;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getFieldToUpdate() {
        return fieldToUpdate;
    }

    public void setFieldToUpdate(String fieldToUpdate) {
        this.fieldToUpdate = fieldToUpdate;
    }

    public String getValueEL() {
        return valueEL;
    }

    public void setValueEL(String valueEL) {
        this.valueEL = valueEL;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int compareTo(Action action) {
        return this.priority - action.priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Action)) {
            return false;
        }
        Action other = (Action) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (uuid == null) {
            if (other.getUuid() != null) {
                return false;
            }
        } else if (!uuid.equals(other.getUuid())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uuid, priority, conditionEl, type, description,
                logLevel, fieldToUpdate, asynchronous, valueEL, actionScript, notification, transition);
    }
}