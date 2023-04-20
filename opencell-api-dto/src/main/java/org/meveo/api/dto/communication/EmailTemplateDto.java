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

package org.meveo.api.dto.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.communication.email.EmailTemplate;

import java.util.List;

/**
 * The Class EmailTemplateDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 4:49:13 AM
 */
@XmlRootElement(name = "EmailTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailTemplateDto extends MessageTemplateDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1739876218558380262L;
    
    /** The subject. */
    @XmlElement(required = true)
    private String subject;
    
    /** The html content. */
    private String htmlContent;
    
    /** The text content. */

    private List<TranslatedHtmlContentDto> translatedHtmlContent;

    private List<TranslatedSubjectDto> translatedSubject;

    /**
     * Instantiates a new email template dto.1
     */
    public EmailTemplateDto() {
        super();
    }

    /**
     * Instantiates a new email template dto.
     *
     * @param emailTemplate the email template
     */
    public EmailTemplateDto(EmailTemplate emailTemplate) {
        super(emailTemplate);
        this.subject = emailTemplate.getSubject();
        this.htmlContent = emailTemplate.getHtmlContent();
    }

    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the new subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the html content.
     *
     * @return the html content
     */
    public String getHtmlContent() {
        return htmlContent;
    }

    /**
     * Sets the html content.
     *
     * @param htmlContent the new html content
     */
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public List<TranslatedHtmlContentDto> getTranslatedHtmlContent() {
        return translatedHtmlContent;
    }

    public void setTranslatedHtmlContent(List<TranslatedHtmlContentDto> translatedHtmlContent) {
        this.translatedHtmlContent = translatedHtmlContent;
    }

    public List<TranslatedSubjectDto> getTranslatedSubject() {
        return translatedSubject;
    }

    public void setTranslatedSubject(List<TranslatedSubjectDto> translatedSubject) {
        this.translatedSubject = translatedSubject;
    }

}