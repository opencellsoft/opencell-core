package org.meveo.api.dto.document.sign;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The Enum WebhookEventEnum for Yousign webhook events : https://dev.yousign.com/#/apiDoc
 */
public enum YousignEventEnum {
    
    PROCEDURE_STARTED("procedure.started"), PROCEDURE_FINISHED("procedure.finished");
    
    private YousignEventEnum (String eventId) {
        this.eventId = eventId;
    }
    
    @JsonValue
    public String getValue () {
        return this.eventId;
    }
    private final String eventId;
}
