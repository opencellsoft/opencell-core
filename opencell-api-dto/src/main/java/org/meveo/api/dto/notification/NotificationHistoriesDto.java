/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.notification;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class NotificationHistoriesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "NotificationHistories")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationHistoriesDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4179758713839676597L;

    /** The notification history. */
    private List<NotificationHistoryDto> notificationHistory;

    /**
     * Gets the notification history.
     *
     * @return the notification history
     */
    public List<NotificationHistoryDto> getNotificationHistory() {
        if (notificationHistory == null)
            notificationHistory = new ArrayList<NotificationHistoryDto>();
        return notificationHistory;
    }

    /**
     * Sets the notification history.
     *
     * @param notificationHistory the new notification history
     */
    public void setNotificationHistory(List<NotificationHistoryDto> notificationHistory) {
        this.notificationHistory = notificationHistory;
    }

    @Override
    public String toString() {
        return "NotificationHistoriesDto [notificationHistory=" + notificationHistory + ", toString()=" + super.toString() + "]";
    }
}