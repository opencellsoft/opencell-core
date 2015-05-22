package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.account.CreditCategoriesDto;

/**
 * @author Edward P. Legaspi
 **/
public class GetCustomerAccountConfigurationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8195022047384406801L;

	private List<String> paymentMethods = new ArrayList<String>();
	private CreditCategoriesDto creditCategories = new CreditCategoriesDto();

	public List<String> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<String> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	@Override
	public String toString() {
		return "GetCustomerAccountConfigurationResponseDto [paymentMethods=" + paymentMethods + ", creditCategories=" + creditCategories + "]";
	}

	public CreditCategoriesDto getCreditCategories() {
		return creditCategories;
	}

	public void setCreditCategories(CreditCategoriesDto creditCategories) {
		this.creditCategories = creditCategories;
	}
}
