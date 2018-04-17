package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CreditCategoriesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CreditCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreditCategoriesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6348231694321723085L;

    /** The credit category. */
    private List<CreditCategoryDto> creditCategory;

    /**
     * Gets the credit category.
     *
     * @return the credit category
     */
    public List<CreditCategoryDto> getCreditCategory() {
        if (creditCategory == null) {
            creditCategory = new ArrayList<CreditCategoryDto>();
        }
        return creditCategory;
    }

    /**
     * Sets the credit category.
     *
     * @param creditCategory the new credit category
     */
    public void setCreditCategory(List<CreditCategoryDto> creditCategory) {
        this.creditCategory = creditCategory;
    }

}