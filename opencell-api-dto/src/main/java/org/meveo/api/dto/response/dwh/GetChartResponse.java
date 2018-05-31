package org.meveo.api.dto.response.dwh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetChartResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetChartResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetChartResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The chart dto. */
    private ChartDto chartDto;

    /**
     * Gets the chart dto.
     *
     * @return the chart dto
     */
    public ChartDto getChartDto() {

        return chartDto;

    }

    /**
     * Sets the chart dto.
     *
     * @param chartDto the new chart dto
     */
    public void setChartDto(ChartDto chartDto) {
        this.chartDto = chartDto;
    }

    @Override
    public String toString() {
        return "GetChartResponse [chartDto=" + chartDto + "]";
    }
}