package org.meveo.service.cpq.rule;

import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.RuleOperatorEnum;

import java.util.List;

public class CommercialRuleLineCommandFactory {

    private Attribute targetAttribute;
    private SelectedAttributes selectedAttributes;
    private List<SelectedAttributes> sourceSelectedAttributes;

    public CommercialRuleLineCommandFactory(Attribute targetAttribute, SelectedAttributes selectedAttributes, List<SelectedAttributes> sourceSelectedAttributes) {
        this.targetAttribute = targetAttribute;
        this.selectedAttributes = selectedAttributes;
        this.sourceSelectedAttributes = sourceSelectedAttributes;
    }

    public CommercialRuleLineCommand create(RuleOperatorEnum ruleOperator){
        switch (ruleOperator){
            case EXISTS:
                return new ExistLineCommand(targetAttribute, selectedAttributes, sourceSelectedAttributes);

            default:
                return null;
        }
    }
}
