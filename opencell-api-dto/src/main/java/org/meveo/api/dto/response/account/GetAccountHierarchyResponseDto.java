package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetAccountHierarchyResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "FindAccountHierarchyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAccountHierarchyResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8676287369018121754L;

    /** The customers. */
    private CustomersDto customers;

    /**
     * Gets the customers.
     *
     * @return the customers
     */
    public CustomersDto getCustomers() {
        if (customers == null) {
            customers = new CustomersDto();
        }
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
}