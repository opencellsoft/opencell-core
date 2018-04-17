package org.meveo.api.dto.response.communication;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class EmailTemplateResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "EmailTemplateResponse")
public class EmailTemplateResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6442797527168418565L;
    
    /** The email template. */
    private EmailTemplateDto emailTemplate;

    /**
     * Gets the email template.
     *
     * @return the email template
     */
    public EmailTemplateDto getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * Sets the email template.
     *
     * @param emailTemplate the new email template
     */
    public void setEmailTemplate(EmailTemplateDto emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

}
