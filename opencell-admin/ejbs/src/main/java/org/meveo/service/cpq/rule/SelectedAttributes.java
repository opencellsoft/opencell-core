package org.meveo.service.cpq.rule;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.LinkedHashMap;

public class SelectedAttributes {

    private String offerCode;
    private String productCode;
    private LinkedHashMap<String, Object> selectedAttributes;
    private boolean canReplace=false;

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

    public LinkedHashMap<String, Object> getSelectedAttributesMap() {
        return selectedAttributes;
    }


    public boolean match(CommercialRuleHeader rule) {
        return (rule.getTargetOfferTemplate() == null || StringUtils.equals(offerCode, rule.getTargetOfferTemplateCode())) && StringUtils.equals(this.productCode, rule.getTargetProductCode()) && selectedAttributes.containsKey(rule.getTargetAttribute().getCode());
    }

    public boolean match(CommercialRuleLine commercialRuleLine) {
        return StringUtils.equals(offerCode, commercialRuleLine.getSourceOfferTemplateCode()) && StringUtils.equals(this.productCode, commercialRuleLine.getSourceProductCode()) && selectedAttributes.containsKey(commercialRuleLine.getSourceAttribute().getCode());
    }

    public boolean match(String offerCode, String productCode){
        return StringUtils.equals(offerCode, this.offerCode) && StringUtils.equals(productCode, this.productCode);
    }

	public boolean isCanReplace() {
		return canReplace;
	}

	public void setCanReplace(boolean canReplace) {
		this.canReplace = canReplace;
	}
    

}
