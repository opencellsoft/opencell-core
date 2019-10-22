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
