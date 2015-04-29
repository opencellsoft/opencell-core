package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetWebHookNotificationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetWebHookNotificationResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1520769709468268817L;
	
	private WebhookNotificationDto webhookDto;

	public WebhookNotificationDto getWebhookDto() {
		return webhookDto;
	}

	public void setWebhookDto(WebhookNotificationDto webhookDto) {
		this.webhookDto = webhookDto;
	}

}
