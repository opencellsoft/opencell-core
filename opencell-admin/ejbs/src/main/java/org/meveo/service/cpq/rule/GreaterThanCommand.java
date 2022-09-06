package org.meveo.service.cpq.rule;

import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.List;
import java.util.Optional;

public class GreaterThanCommand implements CommercialRuleLineCommand {

    private final CommercialRuleHeader commercialRuleHeader;
    private final SelectedAttributes selectedAttributes;
    private final List<SelectedAttributes> selectedSourceAttributes;

    public GreaterThanCommand(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        this.commercialRuleHeader = commercialRuleHeader;
        this.selectedAttributes = selectedAttributes;
        this.selectedSourceAttributes = selectedSourceAttributes;
    }

    @Override
    public boolean execute(CommercialRuleLine commercialRuleLine) {
    	Optional<SelectedAttributes> exist = getSelectedSourceAttributeWitchMatchWithRuleLine(this.selectedSourceAttributes, commercialRuleLine);
    	boolean greaterThan=false;
    	if(exist.isPresent()) {
    		var attributeValue=exist.get().getSelectedAttributesMap().get(commercialRuleLine.getSourceAttribute().getCode());
    		if((attributeValue!=null && !attributeValue.equals("")) && (commercialRuleLine.getSourceAttributeValue()!=null && !commercialRuleLine.getSourceAttributeValue().equals(""))) 
    			greaterThan=Double.valueOf(attributeValue.toString())>Double.valueOf((commercialRuleLine.getSourceAttributeValue()));
    	}
    	return greaterThan;
    }

    public void replace(CommercialRuleLine commercialRuleLine) {
        Optional<SelectedAttributes> exist = getSelectedSourceAttributeWitchMatchWithRuleLine(this.selectedSourceAttributes, commercialRuleLine);
        this.selectedAttributes.getSelectedAttributesMap().put(this.commercialRuleHeader.getTargetAttribute().getCode(), exist.get().getSelectedAttributesMap().get(commercialRuleLine.getSourceAttribute().getCode()));
    }

    private Optional<SelectedAttributes> getSelectedSourceAttributeWitchMatchWithRuleLine(List<SelectedAttributes> selectedSourceAttributes, CommercialRuleLine commercialRuleLine) {
        return selectedSourceAttributes
                .stream()
                .filter(selectedSourceAttribute -> selectedSourceAttribute.match(commercialRuleLine))
                .findAny();
    }
}
