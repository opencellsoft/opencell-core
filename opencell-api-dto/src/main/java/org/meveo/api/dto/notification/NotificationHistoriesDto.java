package org.meveo.api.dto.notification;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "NotificationHistories")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationHistoriesDto extends BaseDto {

	private static final long serialVersionUID = 4179758713839676597L;

	private List<NotificationHistoryDto> notificationHistory;

	public List<NotificationHistoryDto> getNotificationHistory() {
		if (notificationHistory == null)
			notificationHistory = new ArrayList<NotificationHistoryDto>();
		return notificationHistory;
	}

	public void setNotificationHistory(List<NotificationHistoryDto> notificationHistory) {
		this.notificationHistory = notificationHistory;
	}

	@Override
	public String toString() {
		return "NotificationHistoriesDto [notificationHistory=" + notificationHistory + ", toString()=" + super.toString() + "]";
	}

}
