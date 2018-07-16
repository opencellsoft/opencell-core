package org.meveo.model.finance;

/**
 * Type of file result generated from ReportExtract when type is SQL
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 2 May 2018
 * @lastModifiedVersion 5.1
 **/
public enum ReportExtractResultTypeEnum {
    CSV("reportExtractResultTypeEnum.CSV"), HTML("reportExtractResultTypeEnum.HTML");

    private String label;

    private ReportExtractResultTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
