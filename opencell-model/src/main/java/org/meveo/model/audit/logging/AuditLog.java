package org.meveo.model.audit.logging;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * Audit log of user performed actions
 * 
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "audit_log")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "audit_log_seq"), })
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = -8920671560100707762L;

    /**
     * Action timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    /**
     * User that performed the action
     */
    @Column(name = "actor", length = 200)
    private String actor;

    /**
     * Source of action
     */
    @Column(name = "origin", length = 200)
    private String origin;

    /**
     * Action performed
     */
    @Column(name = "action")
    private String action;

    /**
     * Entity that action was performed on
     */
    @Column(name = "entity")
    private String entity;

    /**
     * Action parameters
     */
    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

}
