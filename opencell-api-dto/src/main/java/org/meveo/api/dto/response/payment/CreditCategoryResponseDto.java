package org.meveo.api.dto.response.payment;

import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 * 
 */
public class CreditCategoryResponseDto extends BaseResponse {

	private static final long serialVersionUID = 9018184504399717410L;

	private CreditCategoryDto creditCategory;

	public CreditCategoryDto getCreditCategory() {
		return creditCategory;
	}

	public void setCreditCategory(CreditCategoryDto creditCategory) {
		this.creditCategory = creditCategory;
	}

}
