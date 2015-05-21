package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.NotificationHistoriesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "NotificationHistoriesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationHistoriesResponseDto extends BaseResponse {

	private static final long serialVersionUID = 715247134470556196L;

	private NotificationHistoriesDto notificationHistories = new NotificationHistoriesDto();
	
	public NotificationHistoriesResponseDto() {
		super();
	}

	public NotificationHistoriesDto getNotificationHistories() {
		return notificationHistories;
	}

	public void setNotificationHistories(NotificationHistoriesDto notificationHistories) {
		this.notificationHistories = notificationHistories;
	}

}
