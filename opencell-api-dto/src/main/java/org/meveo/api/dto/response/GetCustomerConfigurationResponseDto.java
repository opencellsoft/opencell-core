package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class GetCustomerConfigurationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetCustomerConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerConfigurationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6164457513010272879L;

    /** The customer brands. */
    private CustomerBrandsDto customerBrands = new CustomerBrandsDto();
    
    /** The customer categories. */
    private CustomerCategoriesDto customerCategories = new CustomerCategoriesDto();
    
    /** The titles. */
    private TitlesDto titles = new TitlesDto();

    /**
     * Gets the customer brands.
     *
     * @return the customer brands
     */
    public CustomerBrandsDto getCustomerBrands() {
        return customerBrands;
    }

    /**
     * Sets the customer brands.
     *
     * @param customerBrands the new customer brands
     */
    public void setCustomerBrands(CustomerBrandsDto customerBrands) {
        this.customerBrands = customerBrands;
    }

    /**
     * Gets the customer categories.
     *
     * @return the customer categories
     */
    public CustomerCategoriesDto getCustomerCategories() {
        return customerCategories;
    }

    /**
     * Sets the customer categories.
     *
     * @param customerCategories the new customer categories
     */
    public void setCustomerCategories(CustomerCategoriesDto customerCategories) {
        this.customerCategories = customerCategories;
    }

    /**
     * Gets the titles.
     *
     * @return the titles
     */
    public TitlesDto getTitles() {
        return titles;
    }

    /**
     * Sets the titles.
     *
     * @param titles the new titles
     */
    public void setTitles(TitlesDto titles) {
        this.titles = titles;
    }

    @Override
    public String toString() {
        return "GetCustomerConfigurationResponseDto [customerBrands=" + customerBrands + ", customerCategories=" + customerCategories + ", titles=" + titles + ", toString()="
                + super.toString() + "]";
    }
}