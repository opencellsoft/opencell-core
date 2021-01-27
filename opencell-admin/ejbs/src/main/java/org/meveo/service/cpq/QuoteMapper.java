package org.meveo.service.cpq;

import org.meveo.api.dto.cpq.xml.BillingAccount;
import org.meveo.api.dto.cpq.xml.Header;
import org.meveo.api.dto.cpq.xml.QuoteXmlDto;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.quote.QuoteVersion;

public class QuoteMapper {
    public QuoteXmlDto map(QuoteVersion quoteVersion) {

        CpqQuote quote = quoteVersion.getQuote();
        BillingAccount billingAccount = new BillingAccount(quote.getBillableAccount() == null ? quote.getApplicantAccount() : quote.getBillableAccount());
        Header header = new Header(billingAccount);
        return new QuoteXmlDto(header);
    }
}
