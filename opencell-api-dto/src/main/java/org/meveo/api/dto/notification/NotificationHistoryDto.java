package org.meveo.api.dto.notification;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;

/**
 * The Class NotificationHistoryDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "NotificationHistory")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationHistoryDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 200495187386477746L;

    /** The notification. */
    private String notification;
    
    /** The entity class name. */
    private String entityClassName;
    
    /** The entity code. */
    private String entityCode;
    
    /** The serialized entity. */
    private String serializedEntity;
    
    /** The nb retry. */
    private int nbRetry;
    
    /** The result. */
    private String result;
    
    /** The date. */
    private Date date;

    /**
     * Possible values: SENT, TO_RETRY, FAILED, CANCELED.
     */
    private NotificationHistoryStatusEnum status;

    /**
     * Instantiates a new notification history dto.
     */
    public NotificationHistoryDto() {

    }

    /**
     * Instantiates a new notification history dto.
     *
     * @param notificationHistory the NotificationHistory entity
     */
    public NotificationHistoryDto(NotificationHistory notificationHistory) {
        if (notificationHistory.getNotification() != null) {
            notification = notificationHistory.getNotification().getCode();
        }
        entityClassName = notificationHistory.getEntityClassName();
        entityCode = notificationHistory.getEntityCode();
        nbRetry = notificationHistory.getNbRetry();
        result = notificationHistory.getResult();
        if (notificationHistory.getAuditable() != null) {
            if (notificationHistory.getAuditable().getUpdated() != null) {
                date = notificationHistory.getAuditable().getUpdated();
            } else {
                date = notificationHistory.getAuditable().getCreated();
            }
        }
    }

    /**
     * Gets the entity class name.
     *
     * @return the entity class name
     */
    public String getEntityClassName() {
        return entityClassName;
    }

    /**
     * Sets the entity class name.
     *
     * @param entityClassName the new entity class name
     */
    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    /**
     * Gets the entity code.
     *
     * @return the entity code
     */
    public String getEntityCode() {
        return entityCode;
    }

    /**
     * Sets the entity code.
     *
     * @param entityCode the new entity code
     */
    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    /**
     * Gets the serialized entity.
     *
     * @return the serialized entity
     */
    public String getSerializedEntity() {
        return serializedEntity;
    }

    /**
     * Sets the serialized entity.
     *
     * @param serializedEntity the new serialized entity
     */
    public void setSerializedEntity(String serializedEntity) {
        this.serializedEntity = serializedEntity;
    }

    /**
     * Gets the nb retry.
     *
     * @return the nb retry
     */
    public int getNbRetry() {
        return nbRetry;
    }

    /**
     * Sets the nb retry.
     *
     * @param nbRetry the new nb retry
     */
    public void setNbRetry(int nbRetry) {
        this.nbRetry = nbRetry;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result the new result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public NotificationHistoryStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(NotificationHistoryStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the notification.
     *
     * @return the notification
     */
    public String getNotification() {
        return notification;
    }

    /**
     * Sets the notification.
     *
     * @param notification the new notification
     */
    public void setNotification(String notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "NotificationHistoryDto [notification=" + notification + ", entityClassName=" + entityClassName + ", entityCode=" + entityCode + ", serializedEntity="
                + serializedEntity + ", nbRetry=" + nbRetry + ", result=" + result + ", date=" + date + ", status=" + status + "]";
    }
}