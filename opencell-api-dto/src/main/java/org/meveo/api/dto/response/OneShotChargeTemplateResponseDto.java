package org.meveo.api.dto.response;

import org.meveo.api.dto.OneShotChargeTemplatesDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;

public class OneShotChargeTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4089213743113945008L;

    /** The oneShotChargeTemplates. */
    private OneShotChargeTemplatesDto oneShotChargeTemplates = new OneShotChargeTemplatesDto();

    /**
     * Constructor
     */
    public OneShotChargeTemplateResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public OneShotChargeTemplateResponseDto(GenericSearchResponse<OneShotChargeTemplateDto> searchResponse) {
        super(searchResponse.getPaging());
        this.oneShotChargeTemplates.setOneShotChargeTemplates(searchResponse.getSearchResults());
    }

    /**
     * Gets the oneShotChargeTemplates.
     *
     * @return the oneShotChargeTemplates
     */
    public OneShotChargeTemplatesDto getOneShotChargeTemplates() {
        return oneShotChargeTemplates;
    }

    /**
     * Sets the oneShotChargeTemplates.
     *
     * @param oneShotChargeTemplates
     */
    public void setOneShotChargeTemplates(OneShotChargeTemplatesDto oneShotChargeTemplates) {
        this.oneShotChargeTemplates = oneShotChargeTemplates;
    }

    @Override
    public String toString() {
        return "OneShotChargeTemplateResponse [oneShotChargeTemplates=" + oneShotChargeTemplates + ", toString()=" + super.toString() + "]";
    }
}
