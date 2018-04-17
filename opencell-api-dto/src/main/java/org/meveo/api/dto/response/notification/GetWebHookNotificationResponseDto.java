package org.meveo.api.dto.response.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetWebHookNotificationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetWebHookNotificationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetWebHookNotificationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1520769709468268817L;

    /** The webhook dto. */
    private WebHookDto webhookDto;

    /**
     * Gets the webhook dto.
     *
     * @return the webhook dto
     */
    public WebHookDto getWebhookDto() {
        return webhookDto;
    }

    /**
     * Sets the webhook dto.
     *
     * @param webhookDto the new webhook dto
     */
    public void setWebhookDto(WebHookDto webhookDto) {
        this.webhookDto = webhookDto;
    }
}