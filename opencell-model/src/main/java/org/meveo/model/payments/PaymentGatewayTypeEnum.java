/**
 * 
 */
package org.meveo.model.payments;

/**
 * 
 * @author anasseh
 * 
 * @since Opencell 4.8
 */
public enum PaymentGatewayTypeEnum {
    CUSTOM, NATIF;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
