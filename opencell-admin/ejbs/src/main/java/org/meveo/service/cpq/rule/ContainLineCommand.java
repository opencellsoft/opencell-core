package org.meveo.service.cpq.rule;

import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class ContainLineCommand implements CommercialRuleLineCommand {
	
	 @Inject
	 private ParamBeanFactory paramBeanFactory;

    private final CommercialRuleHeader commercialRuleHeader;
    private final SelectedAttributes selectedAttributes;
    private final List<SelectedAttributes> selectedSourceAttributes;
    private String multiValuesAttributeSeparator = paramBeanFactory.getInstance().getProperty("attribute.multivalues.separator", ";");


    public ContainLineCommand(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        this.commercialRuleHeader = commercialRuleHeader;
        this.selectedAttributes = selectedAttributes;
        this.selectedSourceAttributes = selectedSourceAttributes;
    }

    @Override
    public boolean execute(CommercialRuleLine commercialRuleLine) {
        Optional<SelectedAttributes> exist = getSelectedSourceAttributeWitchMatchWithRuleLine(this.selectedSourceAttributes, commercialRuleLine);
        String convertedValueStr=exist.isPresent()?String.valueOf(exist.get().getSelectedAttributesMap().get(commercialRuleLine.getSourceAttribute().getCode())):null;
        List<String> values = convertedValueStr!=null?Arrays.asList(convertedValueStr.split(multiValuesAttributeSeparator)):new ArrayList<String>();
		if (values.contains(commercialRuleLine.getSourceAttributeValue())){
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
