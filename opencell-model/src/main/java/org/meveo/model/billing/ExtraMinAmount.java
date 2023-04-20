package org.meveo.model.billing;

import java.io.Serializable;
import java.util.Map;

import org.meveo.model.crm.IInvoicingMinimumApplicable;
/**
 * A class to store extra amount generated to achieve the min amount billable for an account
 * @author khalid HORRI
 * @lastModified 10.0
 */
public class ExtraMinAmount implements Serializable{

    /**
     * The extra amount created by the min amount
     */
    private Map<String, Amounts> createdAmount;
    /**
     * The Entity generating the amounts
     */
    private transient IInvoicingMinimumApplicable entity;

    public ExtraMinAmount(IInvoicingMinimumApplicable entity, Map<String, Amounts> createdAmount) {
        this.createdAmount = createdAmount;
        this.entity = entity;
    }

    public Map<String, Amounts> getCreatedAmount() {
        return createdAmount;
    }

    public void setCreatedAmount(Map<String, Amounts> createdAmount) {
        this.createdAmount = createdAmount;
    }

    public IInvoicingMinimumApplicable getEntity() {
        return entity;
    }

    public void setEntity(IInvoicingMinimumApplicable entity) {
        this.entity = entity;
    }
}
