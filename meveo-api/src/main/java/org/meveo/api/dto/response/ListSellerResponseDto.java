package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SellersDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListSellersResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListSellerResponseDto extends BaseResponse {

	private static final long serialVersionUID = 6134470575443721802L;

	private SellersDto sellers = new SellersDto();

	public SellersDto getSellers() {
		return sellers;
	}

	public void setSellers(SellersDto sellers) {
		this.sellers = sellers;
	}

	@Override
	public String toString() {
		return "ListSellerResponseDto [sellers=" + sellers + ", toString()=" + super.toString() + "]";
	} 

}
