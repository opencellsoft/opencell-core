package org.meveo.service.cpq.rule;

import org.jfree.util.Log;
import org.meveo.api.dto.cpq.ProductContextDTO;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class ReplacementRulesExecutor {
	
	protected Logger log = LoggerFactory.getLogger(getClass());

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
                        rule -> executeItems(rule, selectedAttributes, sourceAttributes)
                );
    }

    private LinkedHashMap<String, Object> getReplacedAttributes(Optional<SelectedAttributes> selectedProductAttributes) {
        return selectedProductAttributes.orElse(new SelectedAttributes(null, null, new LinkedHashMap<>())).getSelectedAttributesMap();
    }

    private void executeItems(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        commercialRuleHeader.getCommercialRuleItems().stream()
                .forEach(item -> executeLines(commercialRuleHeader, selectedAttributes, selectedSourceAttributes, item));
    }

    private void executeLines(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes, CommercialRuleItem item) {

        if (item.getCommercialRuleLines().size() == 1) {
            executeLine(commercialRuleHeader, item.getCommercialRuleLines().get(0), selectedAttributes, selectedSourceAttributes);
        } else {
            executeLines(commercialRuleHeader, item, selectedAttributes, selectedSourceAttributes);

        }
    }

    private void executeLines(CommercialRuleHeader commercialRuleHeader, CommercialRuleItem item, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        boolean canReplace = false;
        CommercialRuleLine matchedCommercialRuleLine=null;
        
        switch (item.getOperator()) {
            case AND:
                canReplace = item.getCommercialRuleLines()
                        .stream()
                        .allMatch(commercialRuleLine -> new CommercialRuleLineCommandFactory(commercialRuleHeader, selectedAttributes, selectedSourceAttributes).create(commercialRuleLine.getOperator(), isQuoteScope).execute(commercialRuleLine));
                break;
            case OR:
             matchedCommercialRuleLine = item.getCommercialRuleLines()
                        .stream()
                        .filter(commercialRuleLine -> new CommercialRuleLineCommandFactory(commercialRuleHeader, selectedAttributes, selectedSourceAttributes).create(commercialRuleLine.getOperator(), isQuoteScope).execute(commercialRuleLine)).findFirst()
                        .orElse(null);
             break;

        }
        log.info("matchedCommercialRuleLine {}, TargetAttributeValue={}",matchedCommercialRuleLine!=null?matchedCommercialRuleLine.getId()+"-"+matchedCommercialRuleLine.getSourceProductCode():null,commercialRuleHeader.getTargetAttributeValue());
        if (matchedCommercialRuleLine!=null && StringUtils.isBlank(commercialRuleHeader.getTargetAttributeValue())) {
        	CommercialRuleLineCommand command = new CommercialRuleLineCommandFactory(commercialRuleHeader, selectedAttributes, selectedSourceAttributes).create(matchedCommercialRuleLine.getOperator(), isQuoteScope);
        	command.replace(matchedCommercialRuleLine);
        }else if (canReplace || matchedCommercialRuleLine!=null) {
        	selectedAttributes.setCanReplace(true);
            selectedAttributes.getSelectedAttributesMap().put(commercialRuleHeader.getTargetAttribute().getCode(), commercialRuleHeader.getTargetAttributeValue());
        }
    }

    private void executeLine(CommercialRuleHeader commercialRuleHeader, CommercialRuleLine commercialRuleLine, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        CommercialRuleLineCommand command = new CommercialRuleLineCommandFactory(commercialRuleHeader, selectedAttributes, selectedSourceAttributes).create(commercialRuleLine.getOperator(), isQuoteScope);
        boolean match = command.execute(commercialRuleLine);
        if (match && StringUtils.isBlank(commercialRuleHeader.getTargetAttributeValue()))
            command.replace(commercialRuleLine);
        else if (match) {
        	selectedAttributes.setCanReplace(true);
            selectedAttributes.getSelectedAttributesMap().put(commercialRuleHeader.getTargetAttribute().getCode(), commercialRuleHeader.getTargetAttributeValue());
            
        }
    }
}
