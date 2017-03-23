package org.meveo.api.dto.notification;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.Notification;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "EmailNotification")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailNotificationDto extends NotificationDto {

	private static final long serialVersionUID = -4101784852715599124L;

	@XmlElement(required = true)
	private String emailFrom;
	private String emailToEl;

	@XmlElement(required = true)
	private String subject;

	private String body;
	private String htmlBody;
	
    @XmlElement(name = "sendToMail")
    private List<String> sendToMail = new ArrayList<String>();

	public EmailNotificationDto() {

	}

	public EmailNotificationDto(EmailNotification e) {
		super((Notification)e);
		emailFrom = e.getEmailFrom();
		emailToEl = e.getEmailToEl();
		subject = e.getSubject();
		body = e.getSubject();
		htmlBody = e.getHtmlBody();
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailToEl() {
		return emailToEl;
	}

	public void setEmailToEl(String emailToEl) {
		this.emailToEl = emailToEl;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	

	public List<String> getSendToMail() {
		return sendToMail;
	}

	public void setSendToMail(List<String> sendToMail) {
		this.sendToMail = sendToMail;
	}

	@Override
	public String toString() {
		return "EmailNotificationDto [emailFrom=" + emailFrom + ", emailToEl=" + emailToEl + ", , emails=" + sendToMail + " subject=" + subject + ", body=" + body + ", htmlBody=" + htmlBody +"]";
	}

}
