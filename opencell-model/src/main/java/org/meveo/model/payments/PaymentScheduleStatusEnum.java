/**
 * 
 */
package org.meveo.model.payments;

/**
 * @author anasseh
 *
 */
public enum PaymentScheduleStatusEnum {
    IN_PROGRESS,//in progress
    OBSOLETE, //updated and a new one are created
    DONE, //all items are processed
    CANCELLED,//Cancelled (logical delete)
    TERMINATED;//terminated by user

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
