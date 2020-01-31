package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.BaseResponse;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class GetUnitOfMeasureResponseDto.
 * 
 * @author Mounir Bahije
 */
@XmlRootElement(name = "GetUnitOfMeasureResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUnitOfMeasureResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7905666519449995575L;

    /** The unitOfMeasure. */
    private UnitOfMeasureDto unitOfMeasure;

    /**
     * Gets the unitOfMeasure Dto.
     *
     * @return the unitOfMeasureDto
     */
    public UnitOfMeasureDto getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Sets the unitOfMeasure.
     *
     * @param unitOfMeasure the new unitOfMeasure
     */
    public void setUnitOfMeasure(UnitOfMeasureDto unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public String toString() {
        return "GetUnitOfMeasureResponseDto [unitOfMeasure=" + unitOfMeasure + ", toString()=" + super.toString() + "]";
    }
}