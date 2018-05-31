package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ProviderDto;

/**
 * The Class GetProviderResponse.
 *
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 */
@XmlRootElement(name = "GetProviderResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetProviderResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7308813550235264178L;

    /** The provider. */
    private ProviderDto provider;

    /**
     * Instantiates a new gets the provider response.
     */
    public GetProviderResponse() {
        super();
    }

    /**
     * Gets the provider.
     *
     * @return the provider
     */
    public ProviderDto getProvider() {
        return provider;
    }

    /**
     * Sets the provider.
     *
     * @param provider the new provider
     */
    public void setProvider(ProviderDto provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "GetProviderResponse [provider=" + provider + ", toString()=" + super.toString() + "]";
    }
}