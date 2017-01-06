package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.meveo.api.dto.CustomEntityInstanceDto;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "GetCustomEntityInstancesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityInstancesResponseDto extends BaseResponse {

    private static final long serialVersionUID = 7328605270701696329L;

    @XmlElementWrapper(name = "customEntityInstances")
    @XmlElement(name = "customEntityInstance")
    private List<CustomEntityInstanceDto> customEntityInstances = new ArrayList<>();

    public List<CustomEntityInstanceDto> getCustomEntityInstances() {
        return customEntityInstances;
    }

    public void setCustomEntityInstances(List<CustomEntityInstanceDto> customEntityInstances) {
        this.customEntityInstances = customEntityInstances;
    }
}