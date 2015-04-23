package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListSellerCodesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListSellerCodesResponseDto extends BaseResponse {

	private static final long serialVersionUID = 386494190197359162L;

	private List<String> sellerCodes;

	public List<String> getSellerCodes() {
		if (sellerCodes == null) {
			sellerCodes = new ArrayList<String>();
		}
		return sellerCodes;
	}

	public void setSellerCodes(List<String> sellerCodes) {
		this.sellerCodes = sellerCodes;
	}

	@Override
	public String toString() {
		return "ListSellerCodesResponseDto [sellerCodes=" + sellerCodes + "]";
	}

}
