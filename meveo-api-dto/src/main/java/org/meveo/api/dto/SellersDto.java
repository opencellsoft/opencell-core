package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Sellers")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellersDto implements Serializable {

	private static final long serialVersionUID = -2924035308389476982L;

	private List<SellerDto> seller;

	public List<SellerDto> getSeller() {
		if (seller == null) {
			seller = new ArrayList<SellerDto>();
		}

		return seller;
	}

	public void setSeller(List<SellerDto> seller) {
		this.seller = seller;
	}

	@Override
	public String toString() {
		return "SellersDto [seller=" + seller + "]";
	}

}
