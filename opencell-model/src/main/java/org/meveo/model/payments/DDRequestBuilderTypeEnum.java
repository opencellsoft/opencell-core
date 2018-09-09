/**
 * 
 */
package org.meveo.model.payments;

/**
 * 
 * @author anasseh
 * 
 * @since Opencell 5.2
 */
public enum DDRequestBuilderTypeEnum {
    CUSTOM, NATIF;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
