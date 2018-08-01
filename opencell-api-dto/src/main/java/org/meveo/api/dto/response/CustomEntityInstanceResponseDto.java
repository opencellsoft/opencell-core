package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomEntityInstanceDto;

/**
 * The Class CustomEntityInstanceResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "GetCustomEntityInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomEntityInstanceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7328605270701696329L;

    /** The custom entity instance. */
    private CustomEntityInstanceDto customEntityInstance;

    /**
     * Gets the custom entity instance.
     *
     * @return the custom entity instance
     */
    public CustomEntityInstanceDto getCustomEntityInstance() {
        return customEntityInstance;
    }

    /**
     * Sets the custom entity instance.
     *
     * @param customEntityInstance the new custom entity instance
     */
    public void setCustomEntityInstance(CustomEntityInstanceDto customEntityInstance) {
        this.customEntityInstance = customEntityInstance;
    }
}