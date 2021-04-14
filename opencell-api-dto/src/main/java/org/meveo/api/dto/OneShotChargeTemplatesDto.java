package org.meveo.api.dto;

import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;

import java.util.ArrayList;
import java.util.List;

public class OneShotChargeTemplatesDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4830516684799736318L;

    /** The invoice sub category. */
    private List<OneShotChargeTemplateDto> oneShotChargeTemplates;

    /**
     * Gets the oneShotChargeTemplates.
     *
     * @return the oneShotChargeTemplates
     */
    public List<OneShotChargeTemplateDto> getOneShotChargeTemplates() {
        if (oneShotChargeTemplates == null)
            oneShotChargeTemplates = new ArrayList<OneShotChargeTemplateDto>();
        return oneShotChargeTemplates;
    }

    /**
     * Sets the oneShotChargeTemplates.
     *
     * @param oneShotChargeTemplates
     */
    public void setOneShotChargeTemplates(List<OneShotChargeTemplateDto> oneShotChargeTemplates) {
        this.oneShotChargeTemplates = oneShotChargeTemplates;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OneShotChargeTemplates [oneShotChargeTemplates=" + oneShotChargeTemplates + "]";
    }

}
