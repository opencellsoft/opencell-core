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
 * The Class CustomEntityInstancesResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "GetCustomEntityInstancesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityInstancesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7328605270701696329L;

    /** The custom entity instances. */
    @XmlElementWrapper(name = "customEntityInstances")
    @XmlElement(name = "customEntityInstance")
    private List<CustomEntityInstanceDto> customEntityInstances = new ArrayList<>();

    /**
     * Gets the custom entity instances.
     *
     * @return the custom entity instances
     */
    public List<CustomEntityInstanceDto> getCustomEntityInstances() {
        return customEntityInstances;
    }

    /**
     * Sets the custom entity instances.
     *
     * @param customEntityInstances the new custom entity instances
     */
    public void setCustomEntityInstances(List<CustomEntityInstanceDto> customEntityInstances) {
        this.customEntityInstances = customEntityInstances;
    }
}