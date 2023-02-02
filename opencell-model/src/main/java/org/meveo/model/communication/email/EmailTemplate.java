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
package org.meveo.model.communication.email;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.communication.MessageTemplate;

import java.util.Map;

@Entity
@DiscriminatorValue("EMAIL")
public class EmailTemplate extends MessageTemplate {
    private static final long serialVersionUID = 7634125312706917352L;

    @Column(name = "subject", length = 255)
    @Size(max = 255)
    private String subject;

    @Type(type = "longText")
    @Column(name = "htmlcontent")
    private String htmlContent;

    @Type(type = "json")
    @Column(name = "htmlcontent_i18n", columnDefinition = "jsonb")
    private Map<String, String> translatedHtmlContent;

    @Type(type = "json")
    @Column(name = "subject_i18n", columnDefinition = "jsonb")
    private Map<String, String> translatedSubject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Map<String, String> getTranslatedHtmlContent() {
        return translatedHtmlContent;
    }

    public void setTranslatedHtmlContent(Map<String, String> translatedHtmlContent) {
        this.translatedHtmlContent = translatedHtmlContent;
    }

    public Map<String, String> getTranslatedSubject() {
        return translatedSubject;
    }

    public void setTranslatedSubject(Map<String, String> translatedSubject) {
        this.translatedSubject = translatedSubject;
    }

    @Override
    public String toString() {
        return super.toString() + "\n subject:" + subject + "\n html content:" + htmlContent + "\n text content:" + super.getTextContent();
    }
}
