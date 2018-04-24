package org.meveo.admin.exception;

/**
 * Exception thrown when an error in sql or script is encountered.
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 24 Apr 2018
 **/
public class ReportExtractExecutionException extends Exception {

    private static final long serialVersionUID = 8445987597240719123L;

    public ReportExtractExecutionException(String msg) {
        super(msg);
    }

}
