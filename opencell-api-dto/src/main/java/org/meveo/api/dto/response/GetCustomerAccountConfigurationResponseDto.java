package org.meveo.api.dto.response;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CreditCategoriesDto;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * The Class GetCustomerAccountConfigurationResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetCustomerAccountConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCustomerAccountConfigurationResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8195022047384406801L;

    /** The payment methods. */
    private List<PaymentMethodEnum> paymentMethods = Arrays.asList(PaymentMethodEnum.values());
    
    /** The credit categories. */
    private CreditCategoriesDto creditCategories = new CreditCategoriesDto();

    /**
     * Gets the payment methods.
     *
     * @return the payment methods
     */
    public List<PaymentMethodEnum> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * Sets the payment methods.
     *
     * @param paymentMethods the new payment methods
     */
    public void setPaymentMethods(List<PaymentMethodEnum> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * Gets the credit categories.
     *
     * @return the credit categories
     */
    public CreditCategoriesDto getCreditCategories() {
        return creditCategories;
    }

    /**
     * Sets the credit categories.
     *
     * @param creditCategories the new credit categories
     */
    public void setCreditCategories(CreditCategoriesDto creditCategories) {
        this.creditCategories = creditCategories;
    }
    
    @Override
    public String toString() {
        return "GetCustomerAccountConfigurationResponseDto [paymentMethods=" + paymentMethods + ", creditCategories=" + creditCategories + "]";
    }    
}