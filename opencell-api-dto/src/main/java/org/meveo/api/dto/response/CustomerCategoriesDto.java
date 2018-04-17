package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerCategoryDto;

/**
 * The Class CustomerCategoriesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CustomerCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCategoriesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 88289090554367171L;

    /** The customer category. */
    private List<CustomerCategoryDto> customerCategory;

    /**
     * Gets the customer category.
     *
     * @return the customer category
     */
    public List<CustomerCategoryDto> getCustomerCategory() {
        if (customerCategory == null)
            customerCategory = new ArrayList<CustomerCategoryDto>();
        return customerCategory;
    }

    /**
     * Sets the customer category.
     *
     * @param customerCategory the new customer category
     */
    public void setCustomerCategory(List<CustomerCategoryDto> customerCategory) {
        this.customerCategory = customerCategory;
    }


    @Override
    public String toString() {
        return "CustomerCategoriesDto [customerCategory=" + customerCategory + "]";
    }

}
