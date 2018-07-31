package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.crm.CustomerBrand;

/**
 * The Class CustomerBrandDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CustomerBrand")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerBrandDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6809423084709875338L;

    /**
     * Instantiates a new customer brand dto.
     */
    public CustomerBrandDto() {

    }

    /**
     * Instantiates a new customer brand dto.
     *
     * @param customerBrand the CustomerBrand entity
     */
    public CustomerBrandDto(CustomerBrand customerBrand) {
        super(customerBrand);
    }

    @Override
    public String toString() {
        return "CustomerBrandDto [code=" + getCode() + ", description=" + getDescription() + "]";
    }

}