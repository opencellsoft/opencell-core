package org.meveo.model.notification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.ModuleItem;
import org.meveo.model.admin.User;

@Entity
@ModuleItem
@Table(name="adm_notif_email")
public class EmailNotification extends Notification {
	
	private static final long serialVersionUID = -8948201462950547554L;

	@Column(name="email_from",length=1000)
	@Size(max=1000)
	private String emailFrom;
	
	@Column(name="email_to_el",length=2000)
	@Size(max=2000)
	private String emailToEl;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name="adm_notif_email_list")
	private Set<String> emails = new HashSet<String>();
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name="adm_notif_email_user")
	private Set<User> users;
	
	@Column(name="email_subject",length=500,nullable=false)
	@NotNull
	@Size(max=500)
	private String subject;

	@Column(name="email_body", columnDefinition = "TEXT")
	private String body;

	@Column(name="email_html_body", columnDefinition = "TEXT")
	private String htmlBody;

	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name="adm_notif_email_attach")
    private Set<String> attachmentExpressions = new HashSet<String>();


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

	public Set<String> getEmails() {
		return emails;
	}

	public void setEmails(Set<String> emails) {
		this.emails = emails;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
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

	public Set<String> getAttachmentExpressions() {
		return attachmentExpressions;
	}

	public void setAttachmentExpressions(Set<String> attachmentExpressions) {
		this.attachmentExpressions = attachmentExpressions;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("EmailNotification [emailFrom=%s, emailToEl=%s, emails=%s,  subject=%s, attachmentExpressions=%s, notification=%s]", emailFrom, emailToEl,
            emails != null ? toString(emails, maxLen) : null, subject, attachmentExpressions != null ? toString(attachmentExpressions, maxLen) : null, super.toString());
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
}
