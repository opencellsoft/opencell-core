package org.meveo.service.cpq.rule;

import org.meveo.model.cpq.trade.CommercialRuleLine;

public interface CommercialRuleLineCommand {

    boolean execute(CommercialRuleLine commercialRuleLine);

    void replace(CommercialRuleLine commercialRuleLine);
}
