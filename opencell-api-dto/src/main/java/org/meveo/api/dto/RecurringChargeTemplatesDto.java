package org.meveo.api.dto;

import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecurringChargeTemplatesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4830516684799736318L;

    /** The list of recurringChargeTemplates. */
    private List<RecurringChargeTemplateDto> recurringChargeTemplates;

    /**
     * Gets recurringChargeTemplates.
     *
     * @return the recurringChargeTemplates
     */
    public List<RecurringChargeTemplateDto> getRecurringChargeTemplates() {
        if (recurringChargeTemplates == null)
            recurringChargeTemplates = new ArrayList<RecurringChargeTemplateDto>();
        return recurringChargeTemplates;
    }

    /**
     * Sets the recurringChargeTemplates.
     *
     * @param recurringChargeTemplates
     */
    public void setRecurringChargeTemplates(List<RecurringChargeTemplateDto> recurringChargeTemplates) {
        this.recurringChargeTemplates = recurringChargeTemplates;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RecurringChargeTemplates [recurringChargeTemplates=" + recurringChargeTemplates + "]";
    }

}

