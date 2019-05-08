package org.meveo.service.custom;

import java.io.Serializable;
import java.sql.SQLException;

import org.meveo.admin.exception.BusinessException;

/**
 * Data import and export statistics and error information
 * 
 * @author Andrius Karpavicius
 */
public class DataImportExportStatistics implements Serializable {

    private static final long serialVersionUID = 6941465207345325405L;

    /**
     * Number of items processed
     */
    private Integer itemsProcessed;

    /**
     * Processing result message
     */
    private String resultMessage;

    /**
     * Occurred exception
     */
    private Throwable exception;

    public DataImportExportStatistics() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor
     * 
     * @param itemsProcessed Number of items processed
     */
    public DataImportExportStatistics(Integer itemsProcessed) {
        super();
        this.itemsProcessed = itemsProcessed;
    }

    /**
     * Constructor
     * 
     * @param resultMessage Processing result message
     */
    public DataImportExportStatistics(String resultMessage) {
        super();
        this.resultMessage = resultMessage;
    }

    /**
     * Constructor
     * 
     * @param exception Occurred exception
     */
    public DataImportExportStatistics(Throwable exception) {
        super();
        this.exception = exception;
    }

    /**
     * @return Number of items processed
     */
    public Integer getItemsProcessed() {
        return itemsProcessed;
    }

    /**
     * @return Processing result message
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * @return Occurred exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Retrieve an exception message from a root cause
     * 
     * @return Exception message
     */
    public String getExceptionMessage() {

        if (exception == null) {
            return null;
        }

        String message = exception.getMessage();

        Throwable cause = exception;
        while (cause != null) {

            if (cause instanceof SQLException || cause instanceof BusinessException) {
                message = cause.getMessage();

            } else if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
                message = cause.getCause().getMessage();
                break;
            }
            cause = cause.getCause();
        }

        return message;
    }
}