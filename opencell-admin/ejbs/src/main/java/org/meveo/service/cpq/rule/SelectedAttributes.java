package org.meveo.service.cpq.rule;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.LinkedHashMap;

public class SelectedAttributes {

    private String offerCode;
    private String productCode;
    private LinkedHashMap<String, Object> selectedAttributes;

    public SelectedAttributes(String offerCode, String productCode, LinkedHashMap<String, Object> selectedAttributes) {
        this.offerCode = offerCode;
        this.productCode = productCode;
        this.selectedAttributes = selectedAttributes;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public LinkedHashMap<String, Object> getSelectedAttributes() {
        return selectedAttributes;
    }

    public boolean isSourceOfferAttribute() {
        return productCode == null;
    }

    public boolean match(String offerTemplateCode, String productCode, Attribute attribute) {
        return StringUtils.equals(offerCode, offerTemplateCode) && StringUtils.equals(this.productCode, productCode) && selectedAttributes.containsKey(attribute.getCode());
    }
}
