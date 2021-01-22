package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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
