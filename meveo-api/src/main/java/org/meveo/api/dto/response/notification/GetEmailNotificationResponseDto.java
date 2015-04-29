package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetEmailNotificationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetEmailNotificationResponseDto extends BaseResponse {

	private static final long serialVersionUID = 5936896951116667399L;
	
	private EmailNotificationDto emailNotificationDto;

	public EmailNotificationDto getEmailNotificationDto() {
		return emailNotificationDto;
	}

	public void setEmailNotificationDto(EmailNotificationDto emailNotificationDto) {
		this.emailNotificationDto = emailNotificationDto;
	}

}
