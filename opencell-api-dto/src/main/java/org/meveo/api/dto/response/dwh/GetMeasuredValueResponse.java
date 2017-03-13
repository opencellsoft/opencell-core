package org.meveo.api.dto.response.dwh;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.dwh.MeasuredValueDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetMeasuredValueResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetMeasuredValueResponse extends BaseResponse {

	private static final long serialVersionUID = -2457439470106519433L;

	@XmlElementWrapper(name = "measuredValues")
	@XmlElement(name = "measuredValue")
	private List<MeasuredValueDto> measuredValues;

	public List<MeasuredValueDto> getMeasuredValues() {
		return measuredValues;
	}

	public void setMeasuredValues(List<MeasuredValueDto> measuredValues) {
		this.measuredValues = measuredValues;
	}

	@Override
	public String toString() {
		return "GetMeasuredValueResponse [measuredValues=" + measuredValues + "]";
	}

}
