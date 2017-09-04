package org.meveo.api.dto.response.payment;

import java.util.List;

import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 * @created 22 Aug 2017
 */
public class CreditCategoriesResponseDto extends BaseResponse {

	private static final long serialVersionUID = 2074397196583085935L;

	private List<CreditCategoryDto> creditCategories;

	public List<CreditCategoryDto> getCreditCategories() {
		return creditCategories;
	}

	public void setCreditCategories(List<CreditCategoryDto> creditCategories) {
		this.creditCategories = creditCategories;
	}

}
