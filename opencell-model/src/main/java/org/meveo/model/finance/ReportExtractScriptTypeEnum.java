package org.meveo.model.finance;

/**
 * @author Edward P. Legaspi
 * @created 30 Jan 2018
 * @lastModifiedVersion 5.0
 **/
public enum ReportExtractScriptTypeEnum {

    JAVA("reportExtractScriptType.JAVA"), SQL("reportExtractScriptType.SQL");

    private String label;

    private ReportExtractScriptTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
