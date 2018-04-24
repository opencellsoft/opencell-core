package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.NotificationHistoriesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class NotificationHistoriesResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "NotificationHistoriesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationHistoriesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 715247134470556196L;

    /** The notification histories. */
    private NotificationHistoriesDto notificationHistories = new NotificationHistoriesDto();

    /**
     * Instantiates a new notification histories response dto.
     */
    public NotificationHistoriesResponseDto() {
        super();
    }

    /**
     * Gets the notification histories.
     *
     * @return the notification histories
     */
    public NotificationHistoriesDto getNotificationHistories() {
        return notificationHistories;
    }

    /**
     * Sets the notification histories.
     *
     * @param notificationHistories the new notification histories
     */
    public void setNotificationHistories(NotificationHistoriesDto notificationHistories) {
        this.notificationHistories = notificationHistories;
    }

}
