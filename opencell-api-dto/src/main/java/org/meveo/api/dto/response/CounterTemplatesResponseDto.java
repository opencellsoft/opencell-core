package org.meveo.api.dto.response;

import org.meveo.api.dto.catalog.CounterTemplateDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class CounterTemplatesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "CounterTemplatesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterTemplatesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The counterTemplates DTO. */
    private List<CounterTemplateDto> counterTemplatesDto = new ArrayList<>();

    /**
     * Constructor of CounterTemplatesResponseDto.
     */
    public CounterTemplatesResponseDto() {
    }

    public CounterTemplatesResponseDto(GenericSearchResponse<CounterTemplateDto> searchResponse) {
        super(searchResponse.getPaging());
        this.setCounterTemplates(searchResponse.getSearchResults());
    }

    /**
     * Get the list of counterTemplateDtos.
     *
     * @return the list of counterTemplateDtos
     */
    public List<CounterTemplateDto> getCounterTemplates() {
        return counterTemplatesDto;
    }

    /**
     * Sets the counterTemplates DTO.
     *
     * @param counterTemplatesDto the counterTemplates DTO
     */
    public void setCounterTemplates(List<CounterTemplateDto> counterTemplatesDto) {
        this.counterTemplatesDto = counterTemplatesDto;
    }

    @Override
    public String toString() {
        return "ListCounterTemplatesResponseDto [counterTemplates=" + counterTemplatesDto + ", toString()=" + super.toString() + "]";
    }
}
