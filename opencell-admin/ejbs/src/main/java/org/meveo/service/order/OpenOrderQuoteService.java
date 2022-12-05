package org.meveo.service.order;

import org.meveo.model.ordering.OpenOrderQuote;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;
import org.meveo.service.base.BusinessService;

import jakarta.ejb.Stateless;

@Stateless
public class OpenOrderQuoteService extends BusinessService<OpenOrderQuote> {

    public OpenOrderQuote changeStatus(OpenOrderQuote ooq, OpenOrderQuoteStatusEnum newStatus) {
        ooq.setStatus(newStatus);
        super.update(ooq);
        return ooq;
    }
}