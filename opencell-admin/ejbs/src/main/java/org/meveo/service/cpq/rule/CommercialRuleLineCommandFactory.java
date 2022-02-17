package org.meveo.service.cpq.rule;

import org.meveo.api.exception.BusinessApiException;
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

    public CommercialRuleLineCommand create(RuleOperatorEnum ruleOperator, boolean isQuoteScope){
        switch (ruleOperator){
            case EXISTS:
                return new ExistLineCommand(targetAttribute, selectedAttributes, sourceSelectedAttributes, isQuoteScope);

            default:
                throw new BusinessApiException("Only Exist operator can be applied on commercial rules of type replacement");
        }
    }
}
