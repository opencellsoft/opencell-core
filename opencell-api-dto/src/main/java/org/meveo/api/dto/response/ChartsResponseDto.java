package org.meveo.api.dto.response;

import org.meveo.api.dto.dwh.ChartDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ChartResponsesDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "ChartResponses")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChartsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The chart DTO. */
    private List<ChartDto> chartsDto = new ArrayList<>();

    /**
     * Constructor of ChartsResponseDto.
     */
    public ChartsResponseDto() {
    }

    /**
     * Get the list of ChartDto.
     *
     * @return the list of ChartDto
     */
    public List<ChartDto> getCharts() {
        return chartsDto;
    }

    /**
     * Sets the charts DTO.
     *
     * @param chartsDto the charts DTO
     */
    public void setCharts(List<ChartDto> chartsDto) {
        this.chartsDto = chartsDto;
    }

    @Override
    public String toString() {
        return "ListChartsResponseDto [chartsDto=" + chartsDto + ", toString()=" + super.toString() + "]";
    }
}
