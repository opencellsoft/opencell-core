package org.meveo.service.cpq.rule;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class ReplacementRulesExecutor {

    private boolean isQuoteScope;

    public ReplacementRulesExecutor(boolean isQuoteScope) {

        this.isQuoteScope = isQuoteScope;
    }

    public ReplacementResult execute(Optional<SelectedAttributes> selectedProductAttributes, Optional<SelectedAttributes> selectedOfferAttributes, List<SelectedAttributes> sourceAttributes, List<CommercialRuleHeader> commercialRuleHeaders) {

        if(selectedProductAttributes.isPresent())
            executeReplacements(selectedProductAttributes.get(), sourceAttributes, commercialRuleHeaders);
        LinkedHashMap<String, Object> replacedProductAttributes = getReplacedAttributes(selectedProductAttributes);

        if(selectedOfferAttributes.isPresent())
            executeReplacements(selectedOfferAttributes.get(), sourceAttributes, commercialRuleHeaders);
        LinkedHashMap<String, Object> replacedOfferAttribute = getReplacedAttributes(selectedOfferAttributes);
        return new ReplacementResult(replacedProductAttributes, replacedOfferAttribute);
    }

    public void executeReplacements(SelectedAttributes selectedAttributes, List<SelectedAttributes> sourceAttributes, List<CommercialRuleHeader> commercialRuleHeaders) {
            commercialRuleHeaders.stream()
                    .filter(rule -> rule.getRuleType().equals(RuleTypeEnum.REPLACEMENT))
                    .filter(rule -> selectedAttributes.match(rule.getTargetOfferTemplateCode(), rule.getTargetProductCode(), rule.getTargetAttribute()))
                    .forEach(
                            rule -> executeItems(rule.getCommercialRuleItems(), rule.getTargetOfferTemplateCode(), rule.getTargetAttribute(), selectedAttributes, sourceAttributes)
                    );
    }

    private LinkedHashMap<String, Object> getReplacedAttributes(Optional<SelectedAttributes> selectedProductAttributes) {
        return selectedProductAttributes.orElse(new SelectedAttributes(null, null, new LinkedHashMap<>())).getSelectedAttributes();
    }

    private void executeItems(List<CommercialRuleItem> ruleItems, String targetOfferTemplateCode, Attribute targetAttribute, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        ruleItems.stream()
                .forEach(
                        item -> {
                            item.getCommercialRuleLines()
                                    .stream()
                                    .forEach(commercialRuleLine -> {
                                        if (!isQuoteScope && !StringUtils.equals(commercialRuleLine.getSourceOfferTemplateCode(), targetOfferTemplateCode)) {
                                            return;
                                        }
                                        CommercialRuleLineCommand commercialRuleLineCommand = new CommercialRuleLineCommandFactory(targetAttribute, selectedAttributes, selectedSourceAttributes).create(commercialRuleLine.getOperator());
                                        commercialRuleLineCommand.execute(commercialRuleLine);
                                    });
                        }
                );
    }

    private Optional<LinkedHashMap<String, Object>> evaluate(List<SelectedAttributes> selectedSourceAttributes, CommercialRuleLine commercialRuleLine) {
        return selectedSourceAttributes
                .stream()
                .filter(selectedAttributes -> selectedAttributes.match(commercialRuleLine.getSourceOfferTemplateCode(), commercialRuleLine.getSourceProductCode(), commercialRuleLine.getSourceAttribute()))
                .map(SelectedAttributes::getSelectedAttributes)
                .findAny();
    }
}
