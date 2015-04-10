package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/

@XmlType(name = "FindWalletOperations")
@XmlAccessorType(XmlAccessType.FIELD)
public class FindWalletOperationsDto extends BaseDto {

	private static final long serialVersionUID = 4342970913973071312L;

	private String offerCode;
	private String seller;
	private String status;

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	@Override
	public String toString() {
		return "FindWalletOperationsDto [offerCode=" + offerCode + ", seller=" + seller + ", status=" + status + "]";
	}

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
