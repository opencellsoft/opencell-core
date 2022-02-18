package org.meveo.service.cpq.rule;

import org.meveo.model.cpq.trade.CommercialRuleLine;

public interface CommercialRuleLineCommand {

    void execute(CommercialRuleLine line);
}
