package org.meveo.api.dto.response.tax;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.tax.TaxMappingDto;

/**
 * API response containing the Tax mapping Dto
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxMappingResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxMappingResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The title dto. */
    private TaxMappingDto dto;

    /**
     * @return The Tax mapping dto
     */
    public TaxMappingDto getDto() {
        return dto;
    }

    /**
     * @param dto The Tax mapping dto
     */
    public void setDto(TaxMappingDto dto) {
        this.dto = dto;
    }

    @Override
    public String toString() {
        return "TaxMappingResponse [" + dto + ", " + super.toString() + "]";
    }
}