package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetNotificationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetNotificationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetNotificationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1644431947124241264L;

    /** The notification dto. */
    private NotificationDto notificationDto;

    /**
     * Gets the notification dto.
     *
     * @return the notification dto
     */
    public NotificationDto getNotificationDto() {
        return notificationDto;
    }

    /**
     * Sets the notification dto.
     *
     * @param notificationDto the new notification dto
     */
    public void setNotificationDto(NotificationDto notificationDto) {
        this.notificationDto = notificationDto;
    }

}
