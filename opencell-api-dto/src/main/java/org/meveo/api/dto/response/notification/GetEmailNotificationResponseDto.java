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

package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetEmailNotificationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetEmailNotificationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetEmailNotificationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5936896951116667399L;

    /** The email notification dto. */
    private EmailNotificationDto emailNotificationDto;

    /**
     * Gets the email notification dto.
     *
     * @return the email notification dto
     */
    public EmailNotificationDto getEmailNotificationDto() {
        return emailNotificationDto;
    }

    /**
     * Sets the email notification dto.
     *
     * @param emailNotificationDto the new email notification dto
     */
    public void setEmailNotificationDto(EmailNotificationDto emailNotificationDto) {
        this.emailNotificationDto = emailNotificationDto;
    }

}
