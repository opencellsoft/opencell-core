package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;

/**
 * The Class PaymentActionStatus.
 *
 * @author phung
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentActionStatus extends ActionStatus {

    /** id of payment . */
    private Long paymentId;

    /**
     * defaut constructor.
     */
    public PaymentActionStatus() {
        super();
    }

    /**
     * Instantiates a new payment action status.
     *
     * @param status action status
     * @param message message.
     */
    public PaymentActionStatus(ActionStatusEnum status, String message) {
        super(status, message);
    }

    /**
     * Instantiates a new payment action status.
     *
     * @param status status of payment action
     * @param errorCode error code
     * @param message message return from API
     */
    public PaymentActionStatus(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        super(status, errorCode, message);
    }

    /**
     * Gets the payment id.
     *
     * @return the paymentId
     */
    public Long getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the payment id.
     *
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

}
