package org.meveo.service.cpq.rule;

import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.trade.CommercialRuleLine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class ExistLineCommand implements CommercialRuleLineCommand {


    private Attribute targetAttribute;
    private SelectedAttributes selectedAttributes;
    private List<SelectedAttributes> selectedSourceAttributes;

    public ExistLineCommand(Attribute targetAttribute, SelectedAttributes selectedAttributes, List<SelectedAttributes> selectedSourceAttributes) {
        this.targetAttribute = targetAttribute;
        this.selectedAttributes = selectedAttributes;
        this.selectedSourceAttributes = selectedSourceAttributes;
    }

    @Override
    public void execute(CommercialRuleLine commercialRuleLine) {
        Optional<LinkedHashMap<String, Object>> exist = evaluate(this.selectedSourceAttributes, commercialRuleLine);
        if (exist.isPresent()) {
            this.selectedAttributes.getSelectedAttributes().put(this.targetAttribute.getCode(), exist.get().get(commercialRuleLine.getSourceAttribute().getCode()));
        }
    }

    private Optional<LinkedHashMap<String, Object>> evaluate(List<SelectedAttributes> selectedSourceAttributes, CommercialRuleLine commercialRuleLine) {
        return selectedSourceAttributes
                .stream()
                .filter(selectedAttributes -> selectedAttributes.match(commercialRuleLine.getSourceOfferTemplateCode(), commercialRuleLine.getSourceProductCode(), commercialRuleLine.getSourceAttribute()))
                .map(SelectedAttributes::getSelectedAttributes)
                .findAny();
    }
}
