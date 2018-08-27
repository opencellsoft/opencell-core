/**
 * 
 */
package org.meveo.model.payments;

/**
 * @author anasseh
 *
 */
public enum PaymentScheduleStatusEnum {
    NEW, IN_PROGRESS, UPDATED, DONE, TERMINATED, CANCELLED;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
