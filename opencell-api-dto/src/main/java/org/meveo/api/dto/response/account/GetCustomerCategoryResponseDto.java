package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * GetCustomerCategoryResponse Dto.
 *
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "GetCustomerCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerCategoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8824614133076085044L;

    /** The customer category. */
    private CustomerCategoryDto customerCategory;

    /**
     * Instantiates a new gets the customer category response dto.
     */
    public GetCustomerCategoryResponseDto() {
        super();
    }

    /**
     * Gets the customer category.
     *
     * @return the customer category
     */
    public CustomerCategoryDto getCustomerCategory() {
        return customerCategory;
    }

    /**
     * Sets the customer category.
     *
     * @param customerCategory the new customer category
     */
    public void setCustomerCategory(CustomerCategoryDto customerCategory) {
        this.customerCategory = customerCategory;
    }

    @Override
    public String toString() {
        return "GetCustomerCategoryResponse [customerCategory=" + customerCategory + ", toString()=" + super.toString() + "]";
    }
}