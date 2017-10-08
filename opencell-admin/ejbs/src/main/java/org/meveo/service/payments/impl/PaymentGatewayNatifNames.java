/**
 * 
 */
package org.meveo.service.payments.impl;

/**
 * The Enum PaymentGatewayNatifNames.
 *
 * @author anasseh
 */
public enum PaymentGatewayNatifNames {
    
    /** The ingenico gc. */
    INGENICO_GC("org.meveo.service.payments.impl.IngenicoGatewayPayment"),
    
    /** The sepa. */
    SEPA("org.meveo.admin.sepa.SepaFile"),
    
    /** The paynum. */
    PAYNUM("org.meveo.admin.sepa.PaynumFile");
    
    /** The class name. */
    private String className;
    
    /**
     * Instantiates a new payment gateway natif names.
     *
     * @param className the class name
     */
    PaymentGatewayNatifNames(String className){
	this.className = className;
    }

    /**
     * Gets the class name.
     *
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name.
     *
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }
    
}
