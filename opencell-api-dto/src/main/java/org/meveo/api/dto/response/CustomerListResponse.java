package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomersDto;

/**
 * The Class CustomerListResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CustomerListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerListResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7840902324622306237L;

    /** The customers. */
    private CustomersDto customers;

    /**
     * Gets the customers.
     *
     * @return the customers
     */
    public CustomersDto getCustomers() {
        return customers;
    }

    /**
     * Sets the customers.
     *
     * @param customers the new customers
     */
    public void setCustomers(CustomersDto customers) {
        this.customers = customers;
    }

    @Override
    public String toString() {
        return "CustomerListResponse [customers=" + customers + "]";
    }

}
