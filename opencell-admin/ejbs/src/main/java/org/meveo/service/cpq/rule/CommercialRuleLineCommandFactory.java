package org.meveo.service.cpq.rule;

import java.util.List;

import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommercialRuleLineCommandFactory {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private CommercialRuleHeader commercialRuleHeader;
    private SelectedAttributes selectedAttributes;
    private List<SelectedAttributes> sourceSelectedAttributes;

    public CommercialRuleLineCommandFactory(CommercialRuleHeader commercialRuleHeader, SelectedAttributes selectedAttributes, List<SelectedAttributes> sourceSelectedAttributes) {
        this.commercialRuleHeader = commercialRuleHeader;
        this.selectedAttributes = selectedAttributes;
        this.sourceSelectedAttributes = sourceSelectedAttributes;
    }

    public CommercialRuleLineCommand create(RuleOperatorEnum ruleOperator, boolean isQuoteScope){
        switch (ruleOperator){
            case EXISTS:
                return new ExistLineCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes, isQuoteScope);
            case EQUAL:
                return new EqualLineCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);
            case NOT_EQUAL:
                return new NotEqualLineCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);  
            case GREATER_THAN:
                return new GreaterThanCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);
            case GREATER_THAN_OR_EQUAL:
                return new GreaterThanOrEqualCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);    
            case LESS_THAN:
                return new LessThanCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);
            case LESS_THAN_OR_EQUAL:
                return new LessThanOrEqualCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes); 
            case CONTAINS:
                return new ContainLineCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);   
            case NOT_CONTAINS:
                return new NotContainLineCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes);    
                

            default: {
                log.warn("Only Exist operator can be applied on commercial rules of type replacement");
                return new ExistLineCommand(commercialRuleHeader, selectedAttributes, sourceSelectedAttributes, isQuoteScope);
            }
        }
    }
}
