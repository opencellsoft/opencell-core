package org.meveo.model.notification;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

/**
 * Notification execution history
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "adm_notif_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_notif_history_seq"), })
@NamedQueries({ @NamedQuery(name = "NotificationHistory.countHistoryToPurgeByDate", query = "select count(*) FROM NotificationHistory hist WHERE hist.auditable.created<=:date"),
        @NamedQuery(name = "NotificationHistory.purgeHistoryByDate", query = "delete NotificationHistory hist WHERE hist.auditable.created<=:date"),
        @NamedQuery(name = "NotificationHistory.countHistoryToPurgeByDateAndNotification", query = "select count(*) FROM NotificationHistory hist WHERE hist.auditable.created<=:date and hist.notification=:notification"),
        @NamedQuery(name = "NotificationHistory.purgeHistoryByDateAndNotification", query = "delete NotificationHistory hist WHERE hist.auditable.created<=:date and hist.notification=:notification"),
        @NamedQuery(name = "NotificationHistory.deleteHistoryByNotification", query = "delete NotificationHistory hist WHERE hist.notification=:notification") })
public class NotificationHistory extends AuditableEntity {

    private static final long serialVersionUID = -6882236977852466160L;

    /**
     * Inbound request that triggered notification
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_request_id")
    private InboundRequest inboundRequest;

    /**
     * Notification that was executed
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "notification_id")
    private Notification notification;

    /**
     * Classname of an entity upon which notification was fired
     */
    @Column(name = "entity_classname", length = 255, nullable = false)
    @Size(max = 255)
    @NotNull
    private String entityClassName;

    /**
     * Code of an entity upon which notification was fired
     */
    @Column(name = "entity_code", length = 35)
    @Size(max = 35)
    private String entityCode;

    /**
     * Entity's ID or a serialized entity (toString()) if ID is not available yet
     */
    @Column(name = "serialized_entity", columnDefinition = "TEXT")
    private String serializedEntity;

    /**
     * Number of times re-tried
     */
    @Column(name = "nb_retry")
    @Max(10)
    private int nbRetry;

    /**
     * Execution result
     */
    @Column(name = "result", length = 1000)
    @Size(max = 1000)
    private String result;

    /**
     * Execution status
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NotificationHistoryStatusEnum status;

    public InboundRequest getInboundRequest() {
        return inboundRequest;
    }

    public void setInboundRequest(InboundRequest inboundRequest) {
        this.inboundRequest = inboundRequest;
        if (!inboundRequest.getNotificationHistories().contains(this)) {
            inboundRequest.getNotificationHistories().add(this);
        }
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getSerializedEntity() {
        return serializedEntity;
    }

    public void setSerializedEntity(String serializedEntity) {
        this.serializedEntity = serializedEntity;
    }

    public int getNbRetry() {
        return nbRetry;
    }

    public void setNbRetry(int nbRetry) {
        this.nbRetry = nbRetry;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if (result != null && result.length() > 1000) {
            this.result = result.substring(0, 997) + "...";
        } else {
            this.result = result;
        }
    }

    public NotificationHistoryStatusEnum getStatus() {
        return status;
    }

    public void setStatus(NotificationHistoryStatusEnum status) {
        this.status = status;
    }

    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof NotificationHistory)) {
            return false;
        }

        NotificationHistory other = (NotificationHistory) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        boolean inReq = this.inboundRequest != null && other.getInboundRequest() != null && this.inboundRequest.getCode().equals(other.getInboundRequest().getCode());
        boolean notif = this.notification != null && other.getNotification() != null && this.notification.getCode().equals(other.getNotification().getCode());
        return inReq && notif;
    }

}
