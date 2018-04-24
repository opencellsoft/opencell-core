package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.DigitalResourceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetDigitalResourceResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetDigitalResourceResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetDigitalResourceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7994925961817572482L;

    /** The digital resources dto. */
    private DigitalResourceDto digitalResourcesDto;

    /**
     * Gets the digital resources dto.
     *
     * @return the digital resources dto
     */
    public DigitalResourceDto getDigitalResourcesDto() {
        return digitalResourcesDto;
    }

    /**
     * Sets the digital resources dto.
     *
     * @param digitalResourcesDto the new digital resources dto
     */
    public void setDigitalResourcesDto(DigitalResourceDto digitalResourcesDto) {
        this.digitalResourcesDto = digitalResourcesDto;
    }

    @Override
    public String toString() {
        return "GetDigitalResourceResponseDto [digitalResource=" + digitalResourcesDto + "]";
    }
}