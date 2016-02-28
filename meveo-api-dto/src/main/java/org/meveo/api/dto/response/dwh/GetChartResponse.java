package org.meveo.api.dto.response.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetChartResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetChartResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private ChartDto chartDto;

	public ChartDto getChartDto() {

		return chartDto;

	}

	public void setChartDto(ChartDto chartDto) {
		this.chartDto = chartDto;
	}

	@Override
	public String toString() {
		return "GetChartResponse [chartDto=" + chartDto + "]";
	}

}
