package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlElement;

public class Header {
    @XmlElement
    private BillingAccount billingAccount;

    public Header() {
    }

    public Header(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }
}
