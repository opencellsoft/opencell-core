/**
 * 
 */
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
 * The Class PaymentHistoriesDto.
 *
 * @author anasseh
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentHistoriesDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The payment histories. */
    @XmlElementWrapper(name = "paymentHistories")
    @XmlElement(name = "paymentHistory")
    private List<PaymentHistoryDto> paymentHistories = new ArrayList<PaymentHistoryDto>();

    /**
     * Instantiates a new payment histories dto.
     */
    public PaymentHistoriesDto() {

    }

    /**
     * Gets the payment histories.
     *
     * @return the paymentHistories
     */
    public List<PaymentHistoryDto> getPaymentHistories() {
        return paymentHistories;
    }

    /**
     * Sets the payment histories.
     *
     * @param paymentHistories the paymentHistories to set
     */
    public void setPaymentHistories(List<PaymentHistoryDto> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }

}
