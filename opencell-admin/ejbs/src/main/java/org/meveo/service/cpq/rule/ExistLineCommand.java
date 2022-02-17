package org.meveo.service.cpq.rule;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class ExistLineCommand implements CommercialRuleLineCommand {


    private Attribute targetAttribute;
    private SelectedAttributes selectedAttributes;
    private List<SelectedAttributes> selectedSourceAttributes;
    private boolean isQuoteScope;

    public ExistLineCommand(Attribute targetAttribute, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes, boolean isQuoteScope) {
        this.targetAttribute = targetAttribute;
        this.selectedAttributes = selectedAttributes;
        this.selectedSourceAttributes = selectedSourceAttributes;
        this.isQuoteScope = isQuoteScope;
    }

    @Override
    public void execute(CommercialRuleLine commercialRuleLine) {
        Optional<LinkedHashMap<String, Object>> exist = evaluate(this.selectedSourceAttributes, commercialRuleLine);
        if(exist.isPresent() && (isQuoteScope || StringUtils.equals(this.selectedAttributes.getOfferCode(), commercialRuleLine.getSourceOfferTemplateCode()))) {
            this.selectedAttributes.getSelectedAttributesMap().put(this.targetAttribute.getCode(), exist.get().get(commercialRuleLine.getSourceAttribute().getCode()));
        }
    }

    private Optional<LinkedHashMap<String, Object>> evaluate(List<SelectedAttributes> selectedSourceAttributes, CommercialRuleLine commercialRuleLine) {
        return selectedSourceAttributes
                .stream()
                .filter(selectedAttributes -> selectedAttributes.match(commercialRuleLine))
                .map(SelectedAttributes::getSelectedAttributesMap)
                .findAny();
    }
}
