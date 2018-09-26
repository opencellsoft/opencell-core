package org.meveo.api.dto.document.sign;

import java.util.List;
import java.util.Map;

import org.meveo.api.dto.BaseEntityDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  DTO used basically to configure email notification, based on some events , kind of procedure.started ... 
 *  
 *  @author Said Ramli
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignProcedureConfigDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L; 
    
    public SignProcedureConfigDto () {
    }
    
    public SignProcedureConfigDto (Map<YousignEventEnum, List<SignEventWebhookDto>> webhook) {
        this.webhook = webhook;
    }
    
    /** The email. */
    private Map<String, List<SignEventEmailDto>> email;
    
    /** The webhook. */
    private Map<YousignEventEnum, List<SignEventWebhookDto>> webhook;
    
    /**
     * Gets the email.
     *
     * @return the email
     */
    public Map<String, List<SignEventEmailDto>> getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(Map<String, List<SignEventEmailDto>> email) {
        this.email = email;
    }

    /**
     * Gets the webhook.
     *
     * @return the webhook
     */
    public Map<YousignEventEnum, List<SignEventWebhookDto>> getWebhook() {
        return webhook;
    }

    /**
     * Sets the webhook.
     *
     * @param webhook the webhook to set
     */
    public void setWebhook(Map<YousignEventEnum, List<SignEventWebhookDto>> webhook) {
        this.webhook = webhook;
    }

}
