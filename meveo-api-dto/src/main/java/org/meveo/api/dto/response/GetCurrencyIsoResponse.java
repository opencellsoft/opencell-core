package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CurrencyIsoDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetCurrencyIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrencyIsoResponse extends BaseResponse {

	private static final long serialVersionUID = -5595545533673878857L;

	private CurrencyIsoDto currency;

	public GetCurrencyIsoResponse() {
		super();
	}

	public CurrencyIsoDto getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyIsoDto currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "GetCurrencyIsoResponse [currency=" + currency + ", toString()=" + super.toString() + "]";
	}

}
