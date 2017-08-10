package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.TipPaymentMethod;

@XmlRootElement(name = "PaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PaymentMethodDto extends BaseDto {

    private static final long serialVersionUID = 4815935377652350103L;

    protected Long id;

    protected String alias;

    protected boolean preferred;

    protected String customerAccountCode;
    
    protected String userId;

    protected String info1;

    protected String info2;

    protected String info3;

    protected String info4;
    
    protected String info5;
    



    public PaymentMethodDto() {
    }

    public PaymentMethodDto(PaymentMethod paymentMethod) {
        this.id = paymentMethod.getId();
        this.alias = paymentMethod.getAlias();
        this.preferred = paymentMethod.isPreferred();
        this.userId = paymentMethod.getUserId();
        this.info1 = paymentMethod.getInfo1();
        this.info2 = paymentMethod.getInfo2();
        this.info3 = paymentMethod.getInfo3();
        this.info4 = paymentMethod.getInfo4();
        this.info5 = paymentMethod.getInfo5();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }
    
    

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	/**
     * Convert entity to DTO
     * 
     * @param paymentMethod Payment method to convert
     * @return DTO
     */
    public static PaymentMethodDto toDto(PaymentMethod paymentMethod) {

        PaymentMethodDto pmDto = null;
        switch (paymentMethod.getPaymentType()) {
        case CARD:
            pmDto = new CardPaymentMethodDto((CardPaymentMethod) paymentMethod);
            break;
        case DIRECTDEBIT:
            pmDto = new DDPaymentMethodDto((DDPaymentMethod) paymentMethod);
            break;
        case TIP:
            pmDto = new TipPaymentMethodDto((TipPaymentMethod) paymentMethod);
            break;
        case CHECK:
        case WIRETRANSFER:
            pmDto = new OtherPaymentMethodDto(paymentMethod);
            break;
        }

        return pmDto;
    }
}