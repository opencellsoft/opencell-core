/**
 * 
 */
package org.meveo.model.payments;

/**
 * 
 * @author anasseh
 * 
 * @since Opencell 5.3
 */
public enum PaymentGatewayChannelEnum {
    API, FILE;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
