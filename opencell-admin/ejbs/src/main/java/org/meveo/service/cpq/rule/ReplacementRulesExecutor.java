package org.meveo.service.cpq.rule;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class ReplacementRulesExecutor {

    private boolean isQuoteScope;

    public ReplacementRulesExecutor(boolean isQuoteScope) {
        this.isQuoteScope = isQuoteScope;
    }

    public ReplacementResult execute(Optional<SelectedAttributes> selectedProductAttributes, Optional<SelectedAttributes> selectedOfferAttributes, List<SelectedAttributes> sourceAttributes, List<CommercialRuleHeader> commercialRuleHeaders) {

        selectedProductAttributes.ifPresent(selectedAttributes -> executeReplacements(selectedAttributes, sourceAttributes, commercialRuleHeaders));
        LinkedHashMap<String, Object> replacedProductAttributes = getReplacedAttributes(selectedProductAttributes);

        selectedOfferAttributes.ifPresent(selectedAttributes -> executeReplacements(selectedAttributes, sourceAttributes, commercialRuleHeaders));
        LinkedHashMap<String, Object> replacedOfferAttribute = getReplacedAttributes(selectedOfferAttributes);
        return new ReplacementResult(replacedProductAttributes, replacedOfferAttribute);
    }

    public void executeReplacements(SelectedAttributes selectedAttributes, List<SelectedAttributes> sourceAttributes, List<CommercialRuleHeader> commercialRuleHeaders) {
        commercialRuleHeaders.stream()
                .filter(rule -> rule.getRuleType().equals(RuleTypeEnum.REPLACEMENT))
                .filter(rule -> !rule.isDisabled())
                .filter(selectedAttributes::match)
                .forEach(
                        rule -> executeItems(rule.getCommercialRuleItems(), rule.getTargetAttribute(), selectedAttributes, sourceAttributes)
                );
    }

    private LinkedHashMap<String, Object> getReplacedAttributes(Optional<SelectedAttributes> selectedProductAttributes) {
        return selectedProductAttributes.orElse(new SelectedAttributes(null, null, new LinkedHashMap<>())).getSelectedAttributesMap();
    }

    private void executeItems(List<CommercialRuleItem> ruleItems, Attribute targetAttribute, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        ruleItems.stream()
                .forEach(item -> executeLines(targetAttribute, selectedAttributes, selectedSourceAttributes, item));
    }

    private void executeLines(Attribute targetAttribute, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes, CommercialRuleItem item) {
        item.getCommercialRuleLines()
                .stream()
                .forEach(commercialRuleLine -> {

                    CommercialRuleLineCommand commercialRuleLineCommand = new CommercialRuleLineCommandFactory(targetAttribute, selectedAttributes, selectedSourceAttributes).create(commercialRuleLine.getOperator(), isQuoteScope);
                    commercialRuleLineCommand.execute(commercialRuleLine);
                });
    }
}
