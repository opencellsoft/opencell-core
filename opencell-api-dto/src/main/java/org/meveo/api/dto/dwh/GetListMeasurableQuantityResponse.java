package org.meveo.api.dto.dwh;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetListMeasurableQuantityResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetListMeasurableQuantityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListMeasurableQuantityResponse extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The list measurable quantity dto. */
    @XmlElementWrapper(name = "listMeasurableQuantity")
    @XmlElement(name = "measurableQuantity")
    private List<MeasurableQuantityDto> listMeasurableQuantityDto = new ArrayList<MeasurableQuantityDto>();

    /**
     * Gets the list measurable quantity dto.
     *
     * @return the list measurable quantity dto
     */
    public List<MeasurableQuantityDto> getListMeasurableQuantityDto() {
        return listMeasurableQuantityDto;
    }

    /**
     * Sets the list measurable quantity dto.
     *
     * @param listMeasurableQuantityDto the new list measurable quantity dto
     */
    public void setListMeasurableQuantityDto(List<MeasurableQuantityDto> listMeasurableQuantityDto) {
        this.listMeasurableQuantityDto = listMeasurableQuantityDto;
    }

    @Override
    public String toString() {
        return "GetListMeasurableQuantityResponse [listMeasurableQuantityDto=" + (listMeasurableQuantityDto == null ? null : listMeasurableQuantityDto) + "]";
    }

}