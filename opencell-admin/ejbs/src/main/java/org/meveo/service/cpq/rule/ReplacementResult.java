package org.meveo.service.cpq.rule;

import java.util.LinkedHashMap;

public class ReplacementResult {

    private LinkedHashMap<String, Object> selectedProductAttributes;
    private final LinkedHashMap<String, Object> selectedOfferAttributes;

    public ReplacementResult(LinkedHashMap<String, Object> selectedProductAttributes, LinkedHashMap<String, Object> selectedOfferAttributes) {
        this.selectedProductAttributes = selectedProductAttributes;
        this.selectedOfferAttributes = selectedOfferAttributes;
    }

    public LinkedHashMap getSelectedProductAttributes() {
        return selectedProductAttributes;
    }

    public LinkedHashMap getSelectedOfferAttributes() {
        return selectedOfferAttributes;
    }
}
