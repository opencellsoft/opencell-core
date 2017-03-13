package org.meveo.admin.wf.types;

import java.util.ArrayList;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteItem;
import org.meveo.model.quote.QuoteStatusEnum;

@WorkflowTypeClass
public class QuoteWF extends WorkflowType<Quote> {

    public QuoteWF() {
        super();
    }

    public QuoteWF(Quote e) {
        super(e);
    }

    @Override
    public List<String> getStatusList() {
        List<String> values = new ArrayList<String>();
        for (QuoteStatusEnum ouoteStatusEnum : QuoteStatusEnum.values()) {
            values.add(ouoteStatusEnum.name());
        }
        return values;
    }

    @Override
    public void changeStatus(String newStatus) throws BusinessException {
        entity.setStatus(QuoteStatusEnum.valueOf(newStatus));
        for (QuoteItem quoteItem : entity.getQuoteItems()) {
            quoteItem.setStatus(entity.getStatus());
        }
    }

    @Override
    public String getActualStatus() {
        return entity.getStatus().name();
    }

}