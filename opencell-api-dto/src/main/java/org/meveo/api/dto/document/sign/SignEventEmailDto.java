package org.meveo.api.dto.document.sign;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO encapsulating an email template inputs for a signature procedure event.
 */
public class SignEventEmailDto extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The subject. */
    private String subject;
    
    /** The message. */
    private String message;
    
    /** The to. */
    private List<String> to;

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the to
     */
    public List<String> getTo() {
        return to;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param to the to to set
     */
    public void setTo(List<String> to) {
        this.to = to;
    }
    
}
