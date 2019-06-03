package org.meveo.api.dto.response;

import org.meveo.api.dto.SellerDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Properties;

/**
 * The Class GetConfigurationResponse.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 7.0
 */
@XmlRootElement(name = "GetConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetConfigurationResponse extends BaseResponse {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -1927118061401041786L;

    /**
     * The seller.
     */
    private Properties properties;

    /**
     * Instantiates a new gets the configuration response.
     */
    public GetConfigurationResponse() {
        super();
    }

    /**
     * Gets configuration as Properties
     *
     * @return a Properties object
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets configuration
     *
     * @param properties configuration properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "GetConfigurationResponse [properties=" + properties + ", toString()=" + super.toString() + "]";
    }
}