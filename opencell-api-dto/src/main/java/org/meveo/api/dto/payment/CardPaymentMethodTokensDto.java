package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CardPaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardPaymentMethodTokensDto extends BaseResponse {

    private static final long serialVersionUID = 6255115951743225824L;

    @XmlElementWrapper(name = "cardPaymentMethods")
    @XmlElement(name = "cardPaymentMethod")
    private List<CardPaymentMethodDto> cardPaymentMethods = new ArrayList<CardPaymentMethodDto>();
    
    public CardPaymentMethodTokensDto() {					
	}
    
    public CardPaymentMethodTokensDto(PaymentMethodTokensDto response) {
		setActionStatus(response.getActionStatus());
		for(PaymentMethodDto paymentMethodDto : response.getPaymentMethods()){
			this.cardPaymentMethods.add(new CardPaymentMethodDto(paymentMethodDto))	;
		}
			
	}

	public List<CardPaymentMethodDto> getCardPaymentMethods() {
        return cardPaymentMethods;
    }

    public void setCardPaymentMethods(List<CardPaymentMethodDto> cardPaymentMethods) {
        this.cardPaymentMethods = cardPaymentMethods;
    }
}