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

package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.ApiErrorCodeEnum;
import org.meveo.api.MeveoApiErrorCodeEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
    @XmlElement(type = MeveoApiErrorCodeEnum.class)
    private ApiErrorCodeEnum errorCode;

    /**
     * A detailed error message if applicable, can contain the entity id that was created.
     */
    @XmlElement(required = true)
    private String message;

    /**
     * The entity identifier
     */
    @XmlElement
    private Long entityId;

    /**
     * The entity code
     */
    @XmlElement
    private String entityCode;

    /**
     * Number of items/records affected by the action
     */
    @XmlElement
    private Integer nrAffected;
    
    private String json;

    public ActionStatus() {
        status = ActionStatusEnum.SUCCESS;
        json = "{\"status\":\"" + status + "\",\"errorCode\": \"" + errorCode + "\",\"message\": \"" + message + "\",\"entityId\": \"" + entityId + "\",\"entityCode\": \"" + entityCode + "\",\"nrAffected\": \""
                + nrAffected + "\"}";
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
    public ActionStatus(ActionStatusEnum status, ApiErrorCodeEnum errorCode, String message) {
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
    public ApiErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ApiErrorCodeEnum errorCode) {
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

    /**
     * @return Number of items/records affected by the action
     */
    public Integer getNrAffected() {
        return nrAffected;
    }

    /**
     * @param nrAffected Number of items/records affected by the action
     */
    public void setNrAffected(Integer nrAffected) {
        this.nrAffected = nrAffected;
    }

    public String getjson() {
    	return json;
    }

    @Override
    public String toString() {
        return "ActionStatus [status=" + status + ", errorCode=" + errorCode + ", message=" + message + ", entityId=" + entityId + ", entityCode=" + entityCode + ", nrAffected=" + nrAffected + "]";
    }

	/**
	 * @param json the json to set
	 */
	public void setJson(String json) {
		this.json = json;
	}
}