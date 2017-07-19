package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CardTokenResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardTokenResponseDto extends BaseResponse{

	private static final long serialVersionUID = 1L;
	private CardTokenDto cardTokenDto;
	public CardTokenResponseDto(){

	}
	public CardTokenDto getCardTokenDto() {
		return cardTokenDto;
	}
	public void setCardTokenDto(CardTokenDto cardTokenDto) {
		this.cardTokenDto = cardTokenDto;
	}
	

}
