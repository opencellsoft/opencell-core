package org.meveo.api.dto.response;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CreditCategoriesDto;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetCustomerAccountConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerAccountConfigurationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8195022047384406801L;

	private List<PaymentMethodEnum> paymentMethods = Arrays.asList(PaymentMethodEnum.values());
	private CreditCategoriesDto creditCategories = new CreditCategoriesDto();

	public List<PaymentMethodEnum> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<PaymentMethodEnum> paymentMethods) {
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
