package org.meveo.model.finance;

/**
 * Channel in which a report was executed.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 23 Apr 2018
 * @lastModifiedVersion 5.1
 **/
public enum ReportExtractExecutionOrigin {
    API, JOB, GUI;

    public String getLabel() {
        return name();
    }
}
