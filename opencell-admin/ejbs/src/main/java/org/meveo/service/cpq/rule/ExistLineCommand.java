package org.meveo.service.cpq.rule;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.List;
import java.util.Optional;

public class ExistLineCommand implements CommercialRuleLineCommand {


    private CommercialRuleHeader commercialRuleHeader;
    private SelectedAttributes selectedAttributes;
    private List<SelectedAttributes> selectedSourceAttributes;
    private boolean isQuoteScope;

    public ExistLineCommand(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes, boolean isQuoteScope) {
        this.commercialRuleHeader = commercialRuleHeader;
        this.selectedAttributes = selectedAttributes;
        this.selectedSourceAttributes = selectedSourceAttributes;
        this.isQuoteScope = isQuoteScope;
    }

    @Override
    public boolean execute(CommercialRuleLine commercialRuleLine) {
        Optional<SelectedAttributes> selectedSourceAttribute = getSelectedSourceAttributeWitchMatchWithRuleLine(this.selectedSourceAttributes, commercialRuleLine);
        return selectedSourceAttribute.isPresent() && (isQuoteScope || StringUtils.equals(this.selectedAttributes.getOfferCode(), commercialRuleLine.getSourceOfferTemplateCode()));
    }

    public void replace(CommercialRuleLine commercialRuleLine) {
        Optional<SelectedAttributes> selectedSourceAttribute = getSelectedSourceAttributeWitchMatchWithRuleLine(this.selectedSourceAttributes, commercialRuleLine);
        this.selectedAttributes.getSelectedAttributesMap().put(this.commercialRuleHeader.getTargetAttribute().getCode(), selectedSourceAttribute.get().getSelectedAttributesMap().get(commercialRuleLine.getSourceAttribute().getCode()));
    }

    private Optional<SelectedAttributes> getSelectedSourceAttributeWitchMatchWithRuleLine(List<SelectedAttributes> selectedSourceAttributes, CommercialRuleLine commercialRuleLine) {
        return selectedSourceAttributes
                .stream()
                .filter(selectedAttributes -> selectedAttributes.match(commercialRuleLine))
                .findAny();
    }
}
