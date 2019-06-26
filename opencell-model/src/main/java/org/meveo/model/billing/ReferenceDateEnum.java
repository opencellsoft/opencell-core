package org.meveo.model.billing;

/**
 * Represents a reference date Enum
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
public enum ReferenceDateEnum {
    TODAY, NEXT_INVOICE_DATE, LAST_TRANSACTION_DATE, END_DATE;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
