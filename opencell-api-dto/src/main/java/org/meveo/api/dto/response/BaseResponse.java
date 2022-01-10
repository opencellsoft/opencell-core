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

package org.meveo.api.dto.response;

import java.io.Serializable;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * All the Opencell API web service response must extend this class.
 * 
 * @author Edward P. Legaspi
 **/
@JsonInclude(Include.NON_NULL)
public abstract class BaseResponse implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4985814323159091933L;

    /**
     * The status response of the web service response.
     */
    private ActionStatus actionStatus = new ActionStatus();

    /**
     * Instantiates a new base response.
     */
    public BaseResponse() {
        actionStatus = new ActionStatus();
    }

    /**
     * Instantiates a new base response.
     *
     * @param status the status
     * @param errorCode the error code
     * @param message the message
     */
    public BaseResponse(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        actionStatus = new ActionStatus(status, errorCode, message);
    }

    /**
     * Gets the action status.
     *
     * @return the action status
     */
    public ActionStatus getActionStatus() {
        return actionStatus;
    }

    /**
     * Sets the action status.
     *
     * @param actionStatus the new action status
     */
    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }


    @Override
    public String toString() {
        return "actionStatus=" + actionStatus;
    }
}