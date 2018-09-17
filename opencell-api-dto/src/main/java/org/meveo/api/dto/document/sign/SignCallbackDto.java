package org.meveo.api.dto.document.sign;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class SignCallbackDto holding request payload of yousign webhook callback
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignCallbackDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private SignProcedureResponseDto procedure;
    private YousignEventEnum eventName;
    
    /**
     * @return the procedure
     */
    public SignProcedureResponseDto getProcedure() {
        return procedure;
    }
    /**
     * @return the eventName
     */
    public YousignEventEnum getEventName() {
        return eventName;
    }
    /**
     * @param procedure the procedure to set
     */
    public void setProcedure(SignProcedureResponseDto procedure) {
        this.procedure = procedure;
    }
    /**
     * @param eventName the eventName to set
     */
    public void setEventName(YousignEventEnum eventName) {
        this.eventName = eventName;
    }

}
