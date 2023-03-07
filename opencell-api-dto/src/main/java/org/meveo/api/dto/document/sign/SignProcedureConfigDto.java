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

    @Override
    public String toString() {
        return "SignProcedureConfigDto [email=" + email + ", webhook=" + webhook + "]";
    }
}
