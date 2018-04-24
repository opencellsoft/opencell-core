package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerBrandDto;

/**
 * The Class CustomerBrandsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CustomerBrands")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerBrandsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3495786003526429089L;

    /** The customer brand. */
    private List<CustomerBrandDto> customerBrand;

    /**
     * Gets the customer brand.
     *
     * @return the customer brand
     */
    public List<CustomerBrandDto> getCustomerBrand() {
        if (customerBrand == null)
            customerBrand = new ArrayList<CustomerBrandDto>();
        return customerBrand;
    }

    /**
     * Sets the customer brand.
     *
     * @param customerBrand the new customer brand
     */
    public void setCustomerBrand(List<CustomerBrandDto> customerBrand) {
        this.customerBrand = customerBrand;
    }

    @Override
    public String toString() {
        return "CustomerBrandsDto [customerBrand=" + customerBrand + "]";
    }

}
