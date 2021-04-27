package org.meveo.api.dto.response;

import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class TriggeredEdrsResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "TriggeredEdrsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The TriggeredEdrs DTO. */
    private List<TriggeredEdrTemplateDto> triggeredEdrTemplatesDto = new ArrayList<>();

    /**
     * Constructor of TriggeredEdrsResponseDto.
     */
    public TriggeredEdrsResponseDto() {
    }

    public TriggeredEdrsResponseDto(GenericSearchResponse<TriggeredEdrTemplateDto> searchResponse) {
        super(searchResponse.getPaging());
        this.setTriggeredEdrs(searchResponse.getSearchResults());
    }

    /**
     * Get the list of triggeredEdrTemplatesDto.
     *
     * @return the list of triggeredEdrTemplatesDto
     */
    public List<TriggeredEdrTemplateDto> getTriggeredEdrs() {
        return triggeredEdrTemplatesDto;
    }

    /**
     * Sets the triggeredEdrTemplates DTO.
     *
     * @param triggeredEdrTemplatesDto the triggeredEdrTemplates DTO
     */
    public void setTriggeredEdrs(List<TriggeredEdrTemplateDto> triggeredEdrTemplatesDto) {
        this.triggeredEdrTemplatesDto = triggeredEdrTemplatesDto;
    }

    @Override
    public String toString() {
        return "ListTriggeredEdrsResponseDto [triggeredEdrTemplates=" + triggeredEdrTemplatesDto + ", toString()=" + super.toString() + "]";
    }
}
