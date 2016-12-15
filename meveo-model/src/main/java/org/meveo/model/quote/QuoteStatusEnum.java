package org.meveo.model.quote;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * Quote status lifecycle
 * 
 * @author Andrius Karpavicius
 * 
 */
@XmlType(name = "QuoteStatus")
@XmlEnum
public enum QuoteStatusEnum {

    /**
     * Quote being prepared
     */
    @XmlEnumValue("InProgress")
    IN_PROGRESS("InProgress"),
    /**
     * Quote needs to be validated from the SP perspective for tariff validation or to capture detailed information.
     */
    @XmlEnumValue("Pending")
    PENDING("Pending"),

    /**
     * Quote process is stopped from a SP decision. A cancelled quote has never been send to the customer.
     */
    @XmlEnumValue("Cancelled")
    CANCELLED("Cancelled"),

    /**
     * Quote has been internally approved and sent to the customer. The quote is no longer updatable
     */
    @XmlEnumValue("Approved")
    APPROVED("Approved"),
    /**
     * The customer agreed to commit to the order and signed the quote.
     */
    @XmlEnumValue("Accepted")
    ACCEPTED("Accepted"),

    /**
     * the customer does not wish to progress with the quotation. It could his final decision and no other quote will be initiated from this quote or it could be during negotiation
     * phase and a new quote version is triggered from this quote. This new version of the quote is created with the in Progress state.
     */
    @XmlEnumValue("Rejected")
    REJECTED("Rejected");

    private String apiState;

    private QuoteStatusEnum(String apiState) {
        this.apiState = apiState;
    }

    public String getApiState() {
        return apiState;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    public static QuoteStatusEnum valueByApiState(String apiState) {
        for (QuoteStatusEnum enumValue : values()) {
            if (enumValue.getApiState().equals(apiState)) {
                return enumValue;
            }
        }
        return null;
    }
}