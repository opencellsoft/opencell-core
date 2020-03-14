package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.commons.utils.StringUtils;

/**
 * Payment by Paypal payment method
 * 
 * @author anasseh
 */
@Entity
@DiscriminatorValue(value = "PAYPAL")
public class PaypalPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571611074346199L;
    
    public PaypalPaymentMethod() {
        this.paymentType = PaymentMethodEnum.PAYPAL;
    }

    public PaypalPaymentMethod(boolean isDisabled, String alias, boolean preferred, CustomerAccount customerAccount,String userId) {
        super();
        setDisabled(isDisabled);
        this.paymentType = PaymentMethodEnum.PAYPAL;
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
        if(StringUtils.isBlank(userId)) {
        	userId = customerAccount.getContactInformationNullSafe().getEmail();
        }else {
        this.userId = userId;
        }
    }

    public PaypalPaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.PAYPAL;
        this.alias = alias;
        this.preferred = preferred;
    }
    


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof PaypalPaymentMethod)) {
			return false;
		}

		PaypalPaymentMethod other = (PaypalPaymentMethod) obj;

		if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
			return true;
		}

		if (getUserId() != null && getUserId().equals(other.getUserId())) {
			return true;
		}

		
		return false;
	}

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        setAlias(paymentMethod.getAlias());
        setPreferred(paymentMethod.isPreferred());
    }

    @Override
    public String toString() {
        return "PaypalPaymentMethod [alias= " + getAlias() + ", preferred=" + isPreferred() + "]";
    }
}