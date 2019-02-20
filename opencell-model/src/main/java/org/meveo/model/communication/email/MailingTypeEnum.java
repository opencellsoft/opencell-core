package org.meveo.model.communication.email;

/**
 * An Enumeration for invoice mailing behavior:
 * <p>
 * If mailingType is "auto", automatically send the invoice after PDF is produced and when invoice.alreadySent is false.<br/>
 * If mailingType is "batch", the Job SendInvoiceJob  sends the invoice when PDF is available and invoice.alreadySent is false.<br/>
 * If mailingType is "manual", only the GUI action and API can send the invoice.
 * </p>
 *
 * @author HORRI Khalid
 * @LastModefiedVersion 5.4
 */
public enum MailingTypeEnum {

    MANUAL(1, "mailingType.manual","manual"), AUTO(2, "mailingType.auto","auto"), BATCH(3, "mailingType.batch","batch");

    private Integer id;
    private String label;
    private String name;

    private MailingTypeEnum(Integer id, String label,String name) {
        this.id = id;
        this.label = label;
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        return name();
    }

    /**
     * Gets MailingTypeEnum basing on it's label
     * @param name's MailingTypeEnum
     * @return a MailingTypeEnum
     */
    public static MailingTypeEnum getByLabel(String name) {
        for (MailingTypeEnum mailingType : MailingTypeEnum.values()) {
            if (mailingType.getName().equalsIgnoreCase(name)) {
                return mailingType;
            }
        }
        return null;
    }
}
