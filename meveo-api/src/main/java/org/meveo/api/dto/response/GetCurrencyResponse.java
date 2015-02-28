package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CurrencyDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetCurrencyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrencyResponse extends BaseResponse {

	private static final long serialVersionUID = -5595545533673878857L;

	private CurrencyDto currency;

	public GetCurrencyResponse() {
		super();
	}

	public CurrencyDto getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyDto currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "GetCurrencyResponse [currency=" + currency + ", toString()=" + super.toString() + "]";
	}

}
