package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward P. Legaspi
 **/
public class GetCustomerAccountConfigurationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8195022047384406801L;

	private List<String> paymentMethods = new ArrayList<String>();
	private List<String> creditCategories = new ArrayList<String>();

	public List<String> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<String> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public List<String> getCreditCategories() {
		return creditCategories;
	}

	public void setCreditCategories(List<String> creditCategories) {
		this.creditCategories = creditCategories;
	}

	@Override
	public String toString() {
		return "GetCustomerAccountConfigurationResponseDto [paymentMethods=" + paymentMethods + ", creditCategories=" + creditCategories + "]";
	}
}
