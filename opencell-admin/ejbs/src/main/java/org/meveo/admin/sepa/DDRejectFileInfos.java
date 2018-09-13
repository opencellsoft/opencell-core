/**
 * 
 */
package org.meveo.admin.sepa;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class DDRejectFileInfos.
 *
 * @author anasseh
 */
public class DDRejectFileInfos {
    
    /** The file name. */
    private String fileName;
    
    /** The dd request lot id. */
    private String ddRequestLotId;
    
    /** The DD request file was rejected. */
    private boolean theDDRequestFileWasRejected = false;
    
    /** The return status code. */
    private String returnStatusCode;
    
    /** The list of invoice refs rejected and reason reject. */
    private Map<String,String> listInvoiceRefsRejected = new HashMap<String,String>();

    /**
     * Gets the dd request lot id.
     *
     * @return the ddRequestLotId
     */
    public String getDdRequestLotId() {
        return ddRequestLotId;
    }

    /**
     * Sets the dd request lot id.
     *
     * @param ddRequestLotId the ddRequestLotId to set
     */
    public void setDdRequestLotId(String ddRequestLotId) {
        this.ddRequestLotId = ddRequestLotId;
    }

    /**
     * Checks if is the DD request file was rejected.
     *
     * @return the theDDRequestFileWasRejected
     */
    public boolean isTheDDRequestFileWasRejected() {
        return theDDRequestFileWasRejected;
    }

    /**
     * Sets the the DD request file was rejected.
     *
     * @param theDDRequestFileWasRejected the theDDRequestFileWasRejected to set
     */
    public void setTheDDRequestFileWasRejected(boolean theDDRequestFileWasRejected) {
        this.theDDRequestFileWasRejected = theDDRequestFileWasRejected;
    }

    /**
     * Gets the return status code.
     *
     * @return the returnStatusCode
     */
    public String getReturnStatusCode() {
        return returnStatusCode;
    }

    /**
     * Sets the return status code.
     *
     * @param returnStatusCode the returnStatusCode to set
     */
    public void setReturnStatusCode(String returnStatusCode) {
        this.returnStatusCode = returnStatusCode;
    }

     /**
     * @return the listInvoiceRefsRejected
     */
    public Map<String, String> getListInvoiceRefsRejected() {
        return listInvoiceRefsRejected;
    }

    /**
     * @param listInvoiceRefsRejected the listInvoiceRefsRejected to set
     */
    public void setListInvoiceRefsRejected(Map<String, String> listInvoiceRefsRejected) {
        this.listInvoiceRefsRejected = listInvoiceRefsRejected;
    }

    /**
     * Gets the file name.
     *
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
