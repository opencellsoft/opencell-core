package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetCustomerAccountResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetCustomerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerAccountResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8824614133076085044L;

    /** The customer account. */
    private CustomerAccountDto customerAccount;

    /**
     * Instantiates a new gets the customer account response dto.
     */
    public GetCustomerAccountResponseDto() {
        super();
    }

    /**
     * Gets the customer account.
     *
     * @return the customer account
     */
    public CustomerAccountDto getCustomerAccount() {
        return customerAccount;
    }

    /**
     * Sets the customer account.
     *
     * @param customerAccount the new customer account
     */
    public void setCustomerAccount(CustomerAccountDto customerAccount) {
        this.customerAccount = customerAccount;
    }

    /* (non-Javadoc)
     * @see org.meveo.api.dto.response.BaseResponse#toString()
     */
    @Override
    public String toString() {
        return "GetCustomerAccountResponse [customerAccount=" + customerAccount + ", toString()=" + super.toString() + "]";
    }

}
