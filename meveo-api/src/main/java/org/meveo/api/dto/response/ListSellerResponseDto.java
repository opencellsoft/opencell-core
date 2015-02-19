package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SellerDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListSellersResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListSellerResponseDto extends BaseResponse {

	private static final long serialVersionUID = 6134470575443721802L;

	private List<SellerDto> sellers;

	public List<SellerDto> getSellers() {
		return sellers;
	}

	public void setSellers(List<SellerDto> sellers) {
		this.sellers = sellers;
	}
}
