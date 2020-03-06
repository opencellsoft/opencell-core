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
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * The Class RefundActionStatus.
 *
 * @author abdelmounaim akadid
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class RefundActionStatus extends ActionStatus {

    /** id of refund . */
    private Long refundId;

    /**
     * defaut constructor.
     */
    public RefundActionStatus() {
        super();
    }

    /**
     * Instantiates a new refund action status.
     *
     * @param status action status
     * @param message message.
     */
    public RefundActionStatus(ActionStatusEnum status, String message) {
        super(status, message);
    }

    /**
     * Instantiates a new refund action status.
     *
     * @param status status of refund action
     * @param errorCode error code
     * @param message message return from API
     */
    public RefundActionStatus(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        super(status, errorCode, message);
    }

    /**
     * Gets the refund id.
     *
     * @return the refundId
     */
    public Long getRefundId() {
        return refundId;
    }

    /**
     * Sets the refund id.
     *
     * @param refundId the refundId to set
     */
    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

}
