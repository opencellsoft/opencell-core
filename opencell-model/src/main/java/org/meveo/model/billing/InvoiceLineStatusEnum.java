package org.meveo.model.billing;

public enum InvoiceLineStatusEnum {

    OPEN(1, "invoiceLineStatus.open"), BILLED(2, "invoiceLineStatus.billed"),
    REJECTED(3, "invoiceLineStatus.rejected"),
    RERATED(4, "invoiceLineStatus.rerated"), CANCELED(5, "invoiceLineStatus.canceled");

    private Integer id;
    private String label;

    InvoiceLineStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public String toString() {
        return name();
    }

}
