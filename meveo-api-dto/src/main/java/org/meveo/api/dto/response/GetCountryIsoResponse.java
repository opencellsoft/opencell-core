package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountryIsoDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetCountryIsoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountryIsoResponse extends BaseResponse {

	private static final long serialVersionUID = -7308813550235264178L;

	private CountryIsoDto country;

	public GetCountryIsoResponse() {
		super();
	}

	public CountryIsoDto getCountry() {
		return country;
	}

	public void setCountry(CountryIsoDto country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "GetCountryIsoResponse [country=" + country + ", toString()=" + super.toString() + "]";
	}

}
