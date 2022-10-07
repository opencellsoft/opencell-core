package org.meveo.service.cpq.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleLine;


public class NotContainLineCommand implements CommercialRuleLineCommand {


    private final CommercialRuleHeader commercialRuleHeader;
    private final SelectedAttributes selectedAttributes;
    private final List<SelectedAttributes> selectedSourceAttributes;

    public NotContainLineCommand(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        this.commercialRuleHeader = commercialRuleHeader;
        this.selectedAttributes = selectedAttributes;
        this.selectedSourceAttributes = selectedSourceAttributes;
    }
    

	@Override
    public boolean execute(CommercialRuleLine commercialRuleLine) {
    	String multiValuesAttributeSeparator = ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
        Optional<SelectedAttributes> exist = getSelectedSourceAttributeWitchMatchWithRuleLine(this.selectedSourceAttributes, commercialRuleLine);
        String convertedValueStr=exist.isPresent()?String.valueOf(exist.get().getSelectedAttributesMap().get(commercialRuleLine.getSourceAttribute().getCode())):null;
        List<String> values = convertedValueStr!=null?Arrays.asList(convertedValueStr.split(multiValuesAttributeSeparator)):new ArrayList<String>();
		if (!values.contains(commercialRuleLine.getSourceAttributeValue())){
			return true;
		}
        return false;
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
