package org.meveo.api.dto.response.payment;

import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CreditCategoryResponseDto.
 *
 * @author Edward P. Legaspi
 */
public class CreditCategoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9018184504399717410L;

    /** The credit category. */
    private CreditCategoryDto creditCategory;

    /**
     * Gets the credit category.
     *
     * @return the credit category
     */
    public CreditCategoryDto getCreditCategory() {
        return creditCategory;
    }

    /**
     * Sets the credit category.
     *
     * @param creditCategory the new credit category
     */
    public void setCreditCategory(CreditCategoryDto creditCategory) {
        this.creditCategory = creditCategory;
    }

}
