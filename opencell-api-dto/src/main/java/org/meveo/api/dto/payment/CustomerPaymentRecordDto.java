package org.meveo.api.dto.payment;


import java.io.Serializable;
import java.util.Date;

/**
 * Customer payment record Dto
 *
 * @author Abdellatif BARI
 */
public class CustomerPaymentRecordDto implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -7871959548573647798L;

    // the code assigned to OC customer payments in the source system
    private String payCode;
    private String accountNumber;
    // checkDigit can be ignored by OC
    private String checkDigit;
    private String paidAmount;
    // filler always null, can be ignored by OC
    private String filler;
    private Date date;
    // Source of Payment
    private String reference;
    // end of record : Always / hard coded as V
    private String endOfRecord;
    private StringBuilder errorMessage;
    private String fileName;

    /**
     * Gets the payCode
     *
     * @return the payCode
     */
    public String getPayCode() {
        return payCode;
    }

    /**
     * Sets the payCode.
     *
     * @param payCode the payCode
     */
    public void setPayCode(String payCode) {
        this.payCode = payCode;
    }

    /**
     * Gets the accountNumber
     *
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber.
     *
     * @param accountNumber the accountNumber
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the checkDigit
     *
     * @return the checkDigit
     */
    public String getCheckDigit() {
        return checkDigit;
    }

    /**
     * Sets the checkDigit.
     *
     * @param checkDigit the checkDigit
     */
    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }

    /**
     * Gets the paidAmount
     *
     * @return the paidAmount
     */
    public String getPaidAmount() {
        return paidAmount;
    }

    /**
     * Sets the paidAmount.
     *
     * @param paidAmount the paidAmount
     */
    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    /**
     * Gets the filler
     *
     * @return the filler
     */
    public String getFiller() {
        return filler;
    }

    /**
     * Sets the filler.
     *
     * @param filler the filler
     */
    public void setFiller(String filler) {
        this.filler = filler;
    }

    /**
     * Gets the date
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets the reference
     *
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     *
     * @param reference the reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the endOfRecord
     *
     * @return the endOfRecord
     */
    public String getEndOfRecord() {
        return endOfRecord;
    }

    /**
     * Sets the endOfRecord.
     *
     * @param endOfRecord the endOfRecord
     */
    public void setEndOfRecord(String endOfRecord) {
        this.endOfRecord = endOfRecord;
    }

    /**
     * Gets the errorMessage
     *
     * @return the errorMessage
     */
    public StringBuilder getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the errorMessage.
     *
     * @param errorMessage the errorMessage
     */
    public void setErrorMessage(StringBuilder errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the fileName
     *
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the fileName.
     *
     * @param fileName the fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}