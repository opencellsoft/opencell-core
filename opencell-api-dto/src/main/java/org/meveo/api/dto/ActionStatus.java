package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * Determine the status of the MEVEO API web service response.
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class ActionStatus {

    /**
     * Tells whether the instance of this <code>ActionStatus</code> object ok or not.
     */
    @XmlElement(required = true)
    private ActionStatusEnum status;

    /**
     * An error code.
     */
    private MeveoApiErrorCodeEnum errorCode;

    /**
     * A detailed error message if applicable, can contain the entity id that was created.
     */
    @XmlElement(required = true)
    private String message;

    /**
     * the entity identifier
     */
    @XmlElement
    private Long entityId;

    /**
     * the entity code
     */
    @XmlElement
    private String entityCode;

    public ActionStatus() {
        status = ActionStatusEnum.SUCCESS;
    }

    /**
     * Sets status and message.
     * 
     * @param status action status
     * @param message message
     */
    public ActionStatus(ActionStatusEnum status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Sets status, error code and message.
     * 
     * @param status action status
     * @param errorCode error code
     * @param message message.
     */
    public ActionStatus(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ActionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ActionStatusEnum status) {
        this.status = status;
    }

    /**
     * Error code.
     * 
     * @return Error code
     */
    public MeveoApiErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(MeveoApiErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the entity id
     *
     * @return the entity id
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the entity id.
     *
     * @param entityId the new entity id
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the entity code
     *
     * @return the entity code
     */
    public String getEntityCode() {
        return entityCode;
    }

    /**
     * Sets the entity code.
     *
     * @param entityCode the new entity code
     */
    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getjson() {
        return "{\"status\":\"" + status + "\",\"errorCode\": \"" + errorCode + "\",\"message\": \""
                + message + "\",\"entityId\": \"" + entityId + "\",\"entityCode\": \"" + entityCode + "\"}";
    }

    @Override
    public String toString() {
        return "ActionStatus{" +
                "status=" + status +
                ", errorCode=" + errorCode +
                ", message='" + message + '\'' +
                ", entityId=" + entityId +
                ", entityCode='" + entityCode + '\'' +
                '}';
    }
}
