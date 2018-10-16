package org.meveo.model.jobs;

/**
 * Enum for Ftp transfer operation
 * @author mboukayoua
 */
public enum  FtpOperationEnum {

    /**
     * import operations
     */
    IMPORT("ftpOperation.import"), EXPORT("ftpOperation.export");

    /**
     * ihm label
     */
    private String label;

    /**
     * enum label
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * private constructor
     * @param label enum label
     */
    FtpOperationEnum(final String label) {
        this.label = label;
    }

    /**
     * toString
     * @return toString
     */
    public String toString() {
        return name();
    }

}

