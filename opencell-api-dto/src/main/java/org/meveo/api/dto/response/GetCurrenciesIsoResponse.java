package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CurrencyIsoDto;

/**
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 **/
@XmlRootElement(name = "GetCurrenciesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCurrenciesIsoResponse extends BaseResponse {

	private static final long serialVersionUID = 12269486818856166L;

	private List<CurrencyIsoDto> currencies;

	public GetCurrenciesIsoResponse() {
		super();
	}

	public List<CurrencyIsoDto> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(List<CurrencyIsoDto> currencies) {
		this.currencies = currencies;
	}

	@Override
	public String toString() {
		return "GetCurrenciesIsoResponse [currencies=" + currencies + "]";
	}

}
