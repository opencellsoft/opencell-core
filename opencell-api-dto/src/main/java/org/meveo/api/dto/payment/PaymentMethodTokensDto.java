package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class PaymentMethodTokensDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "PaymentMethodTokens")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodTokensDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6255115951743225824L;

    /** The payment methods. */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "paymentMethod")
    private List<PaymentMethodDto> paymentMethods = new ArrayList<PaymentMethodDto>();

    /**
     * Gets the payment methods.
     *
     * @return the payment methods
     */
    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * Sets the payment methods.
     *
     * @param paymentMethods the new payment methods
     */
    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

}