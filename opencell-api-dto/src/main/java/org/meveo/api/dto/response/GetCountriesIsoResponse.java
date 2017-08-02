package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountryIsoDto;

/**
 * @author Edward P. Legaspi
 * @since Aug 1, 2017
 **/
@XmlRootElement(name = "GetCountriesIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountriesIsoResponse extends BaseResponse {

	private static final long serialVersionUID = -8391118981393102116L;

	private List<CountryIsoDto> countries;

	public GetCountriesIsoResponse() {
		super();
	}

	public List<CountryIsoDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryIsoDto> countries) {
		this.countries = countries;
	}

	@Override
	public String toString() {
		return "GetCountriesIsoResponse [countries=" + countries + "]";
	}

}
