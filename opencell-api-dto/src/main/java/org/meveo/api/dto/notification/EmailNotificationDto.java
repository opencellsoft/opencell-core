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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.Notification;

/**
 * The Class EmailNotificationDto.
 *
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 7.0
 */
@XmlRootElement(name = "EmailNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailNotificationDto extends NotificationDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4101784852715599124L;

    /** The email from. */
    @XmlElement(required = true)
    private String emailFrom;
    
    /** The email to el. */
    private String emailToEl;

    /** The subject. */
    @XmlElement(required = true)
    private String subject;

    /** The body. */
    private String body;
    
    /** The html body. */
    private String htmlBody;

    /** The send to mail. */
    @XmlElement(name = "sendToMail")
    private List<String> sendToMail = new ArrayList<>();

    /**
     * Instantiates a new email notification dto.
     */
    public EmailNotificationDto() {

    }

    /**
     * Instantiates a new email notification dto.
     *
     * @param emailNotification the EmailNotification entity
     */
    public EmailNotificationDto(EmailNotification emailNotification) {
        super((Notification) emailNotification);
        emailFrom = emailNotification.getEmailFrom();
        emailToEl = emailNotification.getEmailToEl();
        sendToMail.addAll(emailNotification.getEmails());
        description = emailNotification.getDescription();
        if (emailNotification != null && emailNotification.getEmailTemplate() != null) {
            subject = emailNotification.getEmailTemplate().getSubject();
            body = emailNotification.getEmailTemplate().getTextContent();
            htmlBody = emailNotification.getEmailTemplate().getHtmlContent();
        }
    }

    /**
     * Gets the email from.
     *
     * @return the email from
     */
    public String getEmailFrom() {
        return emailFrom;
    }

    /**
     * Sets the email from.
     *
     * @param emailFrom the new email from
     */
    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    /**
     * Gets the email to el.
     *
     * @return the email to el
     */
    public String getEmailToEl() {
        return emailToEl;
    }

    /**
     * Sets the email to el.
     *
     * @param emailToEl the new email to el
     */
    public void setEmailToEl(String emailToEl) {
        this.emailToEl = emailToEl;
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
     * Gets the body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body.
     *
     * @param body the new body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the html body.
     *
     * @return the html body
     */
    public String getHtmlBody() {
        return htmlBody;
    }

    /**
     * Sets the html body.
     *
     * @param htmlBody the new html body
     */
    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    /**
     * Gets the send to mail.
     *
     * @return the send to mail
     */
    public List<String> getSendToMail() {
        return sendToMail;
    }

    /**
     * Sets the send to mail.
     *
     * @param sendToMail the new send to mail
     */
    public void setSendToMail(List<String> sendToMail) {
        this.sendToMail = sendToMail;
    }


    @Override
    public String toString() {
        return "EmailNotificationDto [emailFrom=" + emailFrom + ", emailToEl=" + emailToEl + ", , emails=" + sendToMail + " subject=" + subject + ", body=" + body + ", htmlBody="
                + htmlBody + "]";
    }

}