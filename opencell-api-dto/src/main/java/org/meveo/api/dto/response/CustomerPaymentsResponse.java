package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.PaymentDto;

/**
 * The Class CustomerPaymentsResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CustomerPaymentsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerPaymentsResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5831455659437348223L;

    /** The customer payment dto list. */
    private List<PaymentDto> customerPaymentDtoList;
    
    /** The balance. */
    private double balance;

    /**
     * Instantiates a new customer payments response.
     */
    public CustomerPaymentsResponse() {
        super();
    }

    /**
     * Gets the customer payment dto list.
     *
     * @return the customer payment dto list
     */
    public List<PaymentDto> getCustomerPaymentDtoList() {
        return customerPaymentDtoList;
    }

    /**
     * Sets the customer payment dto list.
     *
     * @param customerPaymentDtoList the new customer payment dto list
     */
    public void setCustomerPaymentDtoList(List<PaymentDto> customerPaymentDtoList) {
        this.customerPaymentDtoList = customerPaymentDtoList;
    }

    /**
     * Gets the balance.
     *
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the balance.
     *
     * @param balance the new balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "CustomerPaymentsResponse [customerPaymentDtoList=" + customerPaymentDtoList + ", balance=" + balance + ", toString()=" + super.toString() + "]";
    }

}
