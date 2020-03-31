package org.meveo.model.billing;

import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;

import java.io.Serializable;
import java.util.Map;
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
    private BusinessEntity entity;

    public ExtraMinAmount(BusinessEntity entity, Map<String, Amounts> createdAmount) {
        this.createdAmount = createdAmount;
        this.entity = entity;
    }

    public Map<String, Amounts> getCreatedAmount() {
        return createdAmount;
    }

    public void setCreatedAmount(Map<String, Amounts> createdAmount) {
        this.createdAmount = createdAmount;
    }

    public BusinessEntity getEntity() {
        return entity;
    }

    public void setEntity(BusinessEntity entity) {
        this.entity = entity;
    }
}
