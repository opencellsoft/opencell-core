/**
 * 
 */
package org.meveo.model.payments;

/**
 * @author anasseh
 *
 */
public enum PaymentErrorTypeEnum {
    ERROR, REJECT;
    
    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
