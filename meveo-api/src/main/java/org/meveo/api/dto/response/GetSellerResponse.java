package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SellerDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetSellerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSellerResponse extends BaseResponse {

	private static final long serialVersionUID = -1927118061401041786L;

	private SellerDto seller;
	
	public GetSellerResponse() {
		super();
	}

	public SellerDto getSeller() {
		return seller;
	}

	public void setSeller(SellerDto seller) {
		this.seller = seller;
	}

	@Override
	public String toString() {
		return "GetSellerResponse [seller=" + seller + ", toString()=" + super.toString() + "]";
	}

}
