package org.meveo.api.dto.notification;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.NotificationHistoryStatusEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "NotificationHistory")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationHistoryDto extends BaseDto {

	private static final long serialVersionUID = 200495187386477746L;

	private String notification;
	private String entityClassName;
	private String entityCode;
	private String serializedEntity;
	private int nbRetry;
	private String result;
	private Date date;

	/**
	 * Possible values: SENT, TO_RETRY, FAILED, CANCELED.
	 */
	private NotificationHistoryStatusEnum status;

	public NotificationHistoryDto() {

	}

	public NotificationHistoryDto(NotificationHistory e) {
		if (e.getNotification() != null) {
			notification = e.getNotification().getCode();
		}
		entityClassName = e.getEntityClassName();
		entityCode = e.getEntityCode();
		nbRetry = e.getNbRetry();
		result = e.getResult();
		if (e.getAuditable() != null) {
			if (e.getAuditable().getUpdated() != null) {
				date = e.getAuditable().getUpdated();
			} else {
				date = e.getAuditable().getCreated();
			}
		}
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
		this.result = result;
	}

	public NotificationHistoryStatusEnum getStatus() {
		return status;
	}

	public void setStatus(NotificationHistoryStatusEnum status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "NotificationHistoryDto [notification=" + notification + ", entityClassName=" + entityClassName + ", entityCode=" + entityCode + ", serializedEntity="
				+ serializedEntity + ", nbRetry=" + nbRetry + ", result=" + result + ", date=" + date + ", status=" + status + "]";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

}
