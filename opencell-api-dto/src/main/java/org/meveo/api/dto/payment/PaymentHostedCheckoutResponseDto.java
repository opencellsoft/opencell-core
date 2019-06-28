package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatus;

/**
 * The Class PaymentGatewayResponseDto.
 *
 * @author Mounir Bahije
 */

@XmlRootElement(name = "PaymentHostedCheckoutResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentHostedCheckoutResponseDto extends ActionStatus {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3151651854190686940L;

    /** The urlPaymentHostedCheckout. */
    private Result result;

    /**
     * Instantiates a new payment gateway response dto.
     */
    public PaymentHostedCheckoutResponseDto() {

    }

    public PaymentHostedCheckoutResponseDto(String hostedCheckoutUrl, String ca, String returnUrl) {
        Result result = new Result(hostedCheckoutUrl, ca, returnUrl);
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

class Result {
    String hostedCheckoutUrl;
    String ca;
    String returnUrl;

    public Result(String hostedCheckoutUrl, String ca, String returnUrl) {
        this.hostedCheckoutUrl = hostedCheckoutUrl;
        this.ca = ca;
        this.returnUrl = returnUrl;
    }

    public String getHostedCheckoutUrl() {
        return hostedCheckoutUrl;
    }

    public void setHostedCheckoutUrl(String hostedCheckoutUrl) {
        this.hostedCheckoutUrl = hostedCheckoutUrl;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
