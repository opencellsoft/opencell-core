package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.TaxDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetTaxResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTaxResponse extends BaseResponse {

	private static final long serialVersionUID = 1336652304727158329L;

	private TaxDto tax;

	public TaxDto getTax() {
		return tax;
	}

	public void setTax(TaxDto tax) {
		this.tax = tax;
	}

	@Override
	public String toString() {
		return "GetTaxResponse [tax=" + tax + ", toString()=" + super.toString() + "]";
	}

}
