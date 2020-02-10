package org.meveo.api.dto.response.tax;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.tax.TaxClassDto;

/**
 * API response containing the Tax class Dto
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxClassResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxClassResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The title dto. */
    private TaxClassDto dto;

    /**
     * @return The Tax class dto
     */
    public TaxClassDto getDto() {
        return dto;
    }

    /**
     * @param dto The Tax class dto
     */
    public void setDto(TaxClassDto dto) {
        this.dto = dto;
    }

    @Override
    public String toString() {
        return "TaxClassResponse [" + dto + ", " + super.toString() + "]";
    }
}