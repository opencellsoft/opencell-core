package org.meveo.api.dto.response.catalog;

import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class GetListUnitOfMeasureResponseDto.
 * 
 * @author Mounir Bahije
 */
@XmlRootElement(name = "GetListUnitOfMeasureResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListUnitOfMeasureResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6452134563213220603L;

    /** The list UnitOfMeasure. */
    private List<UnitOfMeasureDto> listUnitOfMeasure;

    /**
     * Instantiates a new gets the list unitOfMeasure response dto.
     */
    public GetListUnitOfMeasureResponseDto() {
    }

    /**
     * Gets the list UnitOfMeasure.
     *
     * @return the list UnitOfMeasure
     */
    public List<UnitOfMeasureDto> getListUnitOfMeasure() {
        return listUnitOfMeasure;
    }

    /**
     * Sets the list UnitOfMeasure.
     *
     * @param listUnitOfMeasure the new list UnitOfMeasure
     */
    public void setListUnitOfMeasure(List<UnitOfMeasureDto> listUnitOfMeasure) {
        this.listUnitOfMeasure = listUnitOfMeasure;
    }

    /**
     * Adds the unitOfMeasure.
     *
     * @param unitOfMeasure the unitOfMeasure
     */
    public void addUnitOfMeasure(UnitOfMeasureDto unitOfMeasure) {
        if (listUnitOfMeasure == null) {
            listUnitOfMeasure = new ArrayList<>();
        }
        listUnitOfMeasure.add(unitOfMeasure);
    }
}