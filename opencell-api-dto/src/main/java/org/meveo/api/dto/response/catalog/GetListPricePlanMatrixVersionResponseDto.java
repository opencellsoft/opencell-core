package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.SearchResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "GetListPricePlanMatrixVersionResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListPricePlanMatrixVersionResponseDto extends SearchResponse {

    @XmlElementWrapper(name = "ppmVersions")
    @XmlElement(name = "ppmVersions")
    private List<PricePlanMatrixVersionDto> ppmVersions = new ArrayList<>();

    public List<PricePlanMatrixVersionDto> getPpmVersions() {
        return ppmVersions;
    }

    public void setPpmVersions(List<PricePlanMatrixVersionDto> ppmVersions) {
        this.ppmVersions = ppmVersions;
    }
}
