package org.meveo.api.dto.response;

import org.meveo.api.dto.RecurringChargeTemplatesDto;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class RecurringChargeTemplateResponseDto.
 *
 * @author Thang nguyen
 */
@XmlRootElement(name = "RecurringChargeTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecurringChargeTemplateResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4089213743113945008L;

    /** The recurringChargeTemplates. */
    private RecurringChargeTemplatesDto recurringChargeTemplates = new RecurringChargeTemplatesDto();

    /**
     * Constructor
     */
    public RecurringChargeTemplateResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public RecurringChargeTemplateResponseDto(GenericSearchResponse<RecurringChargeTemplateDto> searchResponse) {
        super(searchResponse.getPaging());
        this.recurringChargeTemplates.setRecurringChargeTemplates(searchResponse.getSearchResults());
    }

    /**
     * Gets the recurringChargeTemplates.
     *
     * @return the recurringChargeTemplates
     */
    public RecurringChargeTemplatesDto getRecurringChargeTemplates() {
        return recurringChargeTemplates;
    }

    /**
     * Sets the recurringChargeTemplates.
     *
     * @param recurringChargeTemplates
     */
    public void setRecurringChargeTemplates(RecurringChargeTemplatesDto recurringChargeTemplates) {
        this.recurringChargeTemplates = recurringChargeTemplates;
    }

    @Override
    public String toString() {
        return "RecurringChargeTemplateResponse [recurringChargeTemplates=" + recurringChargeTemplates + ", toString()=" + super.toString() + "]";
    }
}
