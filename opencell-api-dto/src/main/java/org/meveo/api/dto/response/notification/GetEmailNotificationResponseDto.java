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
