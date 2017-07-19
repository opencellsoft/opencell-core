package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "ListCardTokenResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListCardTokenResponseDto extends BaseResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElementWrapper(name="listCardToken")
    @XmlElement(name="cardToken")
	private List<CardTokenDto> listCardToken = new ArrayList<CardTokenDto>();

	public List<CardTokenDto> getListCardToken() {
		return listCardToken;
	}

	public void setListCardToken(List<CardTokenDto> listCardToken) {
		this.listCardToken = listCardToken;
	}
	
}
