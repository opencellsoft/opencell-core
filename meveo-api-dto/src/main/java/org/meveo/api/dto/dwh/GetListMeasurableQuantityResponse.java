package org.meveo.api.dto.dwh;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetListMeasurableQuantityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListMeasurableQuantityResponse extends BaseResponse {	
	private static final long serialVersionUID = 1L;
	

	@XmlElementWrapper(name="listMeasurableQuantity")
    @XmlElement(name="measurableQuantity")
	private List<MeasurableQuantityDto> listMeasurableQuantityDto = new ArrayList<MeasurableQuantityDto>();

	public List<MeasurableQuantityDto> getListMeasurableQuantityDto() {
		return listMeasurableQuantityDto;
	}

	public void setListMeasurableQuantityDto(List<MeasurableQuantityDto> listMeasurableQuantityDto) {
		this.listMeasurableQuantityDto = listMeasurableQuantityDto;
	}

	@Override
	public String toString() {
		return "GetListMeasurableQuantityResponse [listMeasurableQuantityDto=" +( listMeasurableQuantityDto == null ? null : listMeasurableQuantityDto )+ "]";
	}

}
