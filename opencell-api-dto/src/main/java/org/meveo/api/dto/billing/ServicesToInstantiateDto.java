package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ServicesToInstantiateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ServicesToInstantiate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServicesToInstantiateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6088111478916521480L;

    /** The service. */
    @XmlElement(required = true)
    private List<ServiceToInstantiateDto> service;

    /**
     * Gets the service.
     *
     * @return the service
     */
    public List<ServiceToInstantiateDto> getService() {
        if (service == null) {
            service = new ArrayList<>();
        }
        return service;
    }

    /**
     * Sets the service.
     *
     * @param services the new service
     */
    public void setService(List<ServiceToInstantiateDto> services) {
        this.service = services;
    }

    @Override
    public String toString() {
        return "ServicesToInstantiateDto [service=" + service + "]";
    }

}