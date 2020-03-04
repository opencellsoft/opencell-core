package org.meveo.api.dto.response.tax;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.tax.TaxCategoryDto;

/**
 * API response containing the Tax category Dto
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "TaxCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxCategoryResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The title dto. */
    private TaxCategoryDto dto;

    /**
     * @return The Tax category dto
     */
    public TaxCategoryDto getDto() {
        return dto;
    }

    /**
     * @param dto The Tax category dto
     */
    public void setDto(TaxCategoryDto dto) {
        this.dto = dto;
    }

    @Override
    public String toString() {
        return "TaxCategoryResponse [" + dto + ", " + super.toString() + "]";
    }
}