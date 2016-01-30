package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountriesDto;
import org.meveo.api.dto.CurrenciesDto;
import org.meveo.api.dto.LanguagesDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetTradingConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTradingConfigurationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -598052725975586031L;

	private CountriesDto countries = new CountriesDto();
	private CurrenciesDto currencies = new CurrenciesDto();
	private LanguagesDto languages = new LanguagesDto();;

	public CountriesDto getCountries() {
		return countries;
	}

	public void setCountries(CountriesDto countries) {
		this.countries = countries;
	}

	public CurrenciesDto getCurrencies() {
		return currencies;
	}

	public void setCurrencies(CurrenciesDto currencies) {
		this.currencies = currencies;
	}

	public LanguagesDto getLanguages() {
		return languages;
	}

	public void setLanguages(LanguagesDto languages) {
		this.languages = languages;
	}

	@Override
	public String toString() {
		return "GetTradingConfigurationResponseDto [countries=" + countries + ", currencies=" + currencies
				+ ", languages=" + languages + ", toString()=" + super.toString() + "]";
	}

}
