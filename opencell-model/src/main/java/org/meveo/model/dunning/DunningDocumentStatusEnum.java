package org.meveo.model.dunning;

/**
 * DunningDocument entity status Enum
 *
 * @author mboukayoua
 */
public enum DunningDocumentStatusEnum {
    R1("Dunning Document created."), R2("Dunning email sent to client"), R3("internal email notif sent to Engie Agent"),
    R4("client's subscription suspended, due invoices marked as disputed and export effico is done");

    /** status description */
    private String description;

    /**
     * enum constructor
     * @param description status description
     */
    DunningDocumentStatusEnum(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}