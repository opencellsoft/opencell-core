package org.meveo.service.cpq.rule;

import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommercialRuleLineCommandFactory {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

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

            default: {
                log.warn("Only Exist operator can be applied on commercial rules of type replacement");
                return new ExistLineCommand(targetAttribute, selectedAttributes, sourceSelectedAttributes, isQuoteScope);
            }
        }
    }
}
