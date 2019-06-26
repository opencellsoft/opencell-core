package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;

import org.meveo.model.admin.Seller;

public class RatedTransactionGroup {

    BillingAccount billingAccount;
    BillingCycle billingCycle;
    Seller seller;
    List<RatedTransaction> ratedTransactions = new ArrayList<RatedTransaction>();

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<RatedTransaction> getRatedTransactions() {
        return ratedTransactions;
    }

    public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
        this.ratedTransactions = ratedTransactions;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

}
