/**
 * 
 */
package org.meveo.model.payments;

/**
 * @author anasseh
 *
 */
public enum PaymentScheduleStatusEnum {
    IN_PROGRESS, OBSOLETE, DONE, CANCELLED;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
