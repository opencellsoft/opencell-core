package org.meveo.service.billing.impl;

/**
 * Exception when charge has no price plan set
 * 
 * @author Andrius Karpavicius
 * 
 */
public class ChargeWitoutPricePlanException extends Exception {

    private static final long serialVersionUID = 8278511653197820449L;

    public ChargeWitoutPricePlanException(String chargeCode) {
        super("Charge " + chargeCode + " has no price plan defined");
    }
}