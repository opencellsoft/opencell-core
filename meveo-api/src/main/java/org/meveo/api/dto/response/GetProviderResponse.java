package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ProviderDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetProviderResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProviderResponse extends BaseResponse {

	private static final long serialVersionUID = -7308813550235264178L;

	private ProviderDto provider;

	public GetProviderResponse() {
		super();
	}

	public ProviderDto getProvider() {
		return provider;
	}

	public void setProvider(ProviderDto provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "GetProviderResponse [provider=" + provider + ", toString()=" + super.toString() + "]";
	}

}
