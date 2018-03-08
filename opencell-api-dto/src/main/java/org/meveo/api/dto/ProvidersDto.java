package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

@XmlRootElement(name = "Providers")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProvidersDto extends SearchResponse {

    private static final long serialVersionUID = 1893591052731642142L;

    @XmlElementWrapper(name = "providers")
    @XmlElement(name = "provider")
    private List<ProviderDto> providers = new ArrayList<>();

    public List<ProviderDto> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderDto> providers) {
        this.providers = providers;
    }
}