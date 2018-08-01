package org.meveo.api.dto.response.communication;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class EmailTemplatesResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 */
@XmlRootElement(name = "EmailTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailTemplatesResponseDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7682857831421991842L;

    /** The email templates. */
    @XmlElementWrapper(name = "emailTemplates")
    @XmlElement(name = "emailTemplate")
    private List<EmailTemplateDto> emailTemplates;

    /**
     * Gets the email templates.
     *
     * @return the email templates
     */
    public List<EmailTemplateDto> getEmailTemplates() {
        return emailTemplates;
    }

    /**
     * Sets the email templates.
     *
     * @param emailTemplates the new email templates
     */
    public void setEmailTemplates(List<EmailTemplateDto> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

}
