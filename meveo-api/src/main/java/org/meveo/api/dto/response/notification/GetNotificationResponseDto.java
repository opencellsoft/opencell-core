package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetNotificationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetNotificationResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1644431947124241264L;
	
	private NotificationDto notificationDto;

	public NotificationDto getNotificationDto() {
		return notificationDto;
	}

	public void setNotificationDto(NotificationDto notificationDto) {
		this.notificationDto = notificationDto;
	}

}
