package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Currencies")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrenciesDto extends BaseDto {

	private static final long serialVersionUID = -7446426816621551014L;

	private List<CurrencyDto> currency;

	public List<CurrencyDto> getCurrency() {
		if (currency == null)
			currency = new ArrayList<CurrencyDto>();
		return currency;
	}

	public void setCurrency(List<CurrencyDto> currency) {
		this.currency = currency;
	}

}
