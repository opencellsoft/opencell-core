/**
 * 
 */
package org.meveo.model.payments;

/**
 * @author anasseh
 * @lastModifiedVersion 5.0
 */
public enum PaymentErrorTypeEnum {
    ERROR, REJECT;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
