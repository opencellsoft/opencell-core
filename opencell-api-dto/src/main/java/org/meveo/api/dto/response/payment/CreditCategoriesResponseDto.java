package org.meveo.api.dto.response.payment;

import java.util.List;

import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CreditCategoriesResponseDto.
 *
 * @author Edward P. Legaspi
 * @since 22 Aug 2017
 */
public class CreditCategoriesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2074397196583085935L;

    /** The credit categories. */
    private List<CreditCategoryDto> creditCategories;

    /**
     * Gets the credit categories.
     *
     * @return the credit categories
     */
    public List<CreditCategoryDto> getCreditCategories() {
        return creditCategories;
    }

    /**
     * Sets the credit categories.
     *
     * @param creditCategories the new credit categories
     */
    public void setCreditCategories(List<CreditCategoryDto> creditCategories) {
        this.creditCategories = creditCategories;
    }

}
