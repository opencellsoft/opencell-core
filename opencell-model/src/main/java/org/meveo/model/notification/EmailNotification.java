package org.meveo.model.notification;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.admin.User;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;

/**
 * Notification by sending email
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "EmailNotification")
@Table(name = "adm_notif_email")
public class EmailNotification extends Notification {

    private static final long serialVersionUID = -8948201462950547554L;
    
    public static final String EMAIL_TEMPLATE_PARAMS = "EmailTemplate_Params";

    /**
     * Sender's email
     */
    @Column(name = "email_from", length = 1000)
    @Size(max = 1000)
    private String emailFrom;

    /**
     * Expression to determine a recipient's email address
     */
    @Column(name = "email_to_el", length = 2000)
    @Size(max = 2000)
    private String emailToEl;

    /**
     * Recipient's email addresses
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_email_list")
    private Set<String> emails = new HashSet<String>();

    /**
     * Users email should be send to
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_email_user")
    private Set<User> users;

    /**
     * EmailTemplate containing subject and body email
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_template_id")
    private EmailTemplate emailTemplate = new EmailTemplate();

    /**
     * A list of expressions to determine email's attachments
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "adm_notif_email_attach")
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

    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
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
            emails != null ? toString(emails, maxLen) : null, emailTemplate != null ? emailTemplate.getSubject() : null, attachmentExpressions != null ? toString(attachmentExpressions, maxLen) : null, super.toString());
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
    
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate paramsCF = new CustomFieldTemplate();
        paramsCF.setCode(EMAIL_TEMPLATE_PARAMS);
        paramsCF.setAppliesTo("EmailNotification");
        paramsCF.setActive(true);
        paramsCF.setDescription("EmailTemplate parameters");
        paramsCF.setFieldType(CustomFieldTypeEnum.STRING);
        paramsCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
        paramsCF.setValueRequired(false);
        paramsCF.setMaxValue(256L);
        paramsCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        result.put(EMAIL_TEMPLATE_PARAMS, paramsCF);

        return result;
    }
}
