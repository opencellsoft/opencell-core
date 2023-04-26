/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

/**
 * 
 */
package org.meveo.admin.sepa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * The Class DDRejectFileInfos.
 *
 * @author anasseh
 * 
 * @lastModifiedVersion 5.7.3
 */
public class DDRejectFileInfos {
    
    /** The file name. */
    private String fileName;
    
    /** The dd request lot id. */
    private Long ddRequestLotId;
    
    /** The DD request file was rejected. */
    private boolean theDDRequestFileWasRejected = false;
    
    /** The return status code. */
    private String returnStatusCode;
    
    /** The list of invoice refs rejected and reason reject. */
    private Map<Long,String> listInvoiceRefsRejected = new HashMap<Long,String>();
    
    /** The nb items ok. */
    private int nbItemsOk= 0;
    
    /** The nb items ko. */
    private int nbItemsKo = 0;
    
    /** The list errors. */
    private List<String>listErrors = new ArrayList<String>();

    /**
     * Gets the dd request lot id.
     *
     * @return the ddRequestLotId
     */
    public Long getDdRequestLotId() {
        return ddRequestLotId;
    }

    /**
     * Sets the dd request lot id.
     *
     * @param ddRequestLotId the ddRequestLotId to set
     */
    public void setDdRequestLotId(Long ddRequestLotId) {
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
      * Gets the list invoice refs rejected.
      *
      * @return the listInvoiceRefsRejected
      */
    public Map<Long, String> getListInvoiceRefsRejected() {
        return listInvoiceRefsRejected;
    }

    /**
     * Sets the list invoice refs rejected.
     *
     * @param listInvoiceRefsRejected the listInvoiceRefsRejected to set
     */
    public void setListInvoiceRefsRejected(Map<Long, String> listInvoiceRefsRejected) {
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

	/**
	 * Gets the nb items ok.
	 *
	 * @return the nb items ok
	 */
	public int getNbItemsOk() {
		return nbItemsOk;
	}

	/**
	 * Sets the nb items ok.
	 *
	 * @param nbItemsOk the new nb items ok
	 */
	public void setNbItemsOk(int nbItemsOk) {
		this.nbItemsOk = nbItemsOk;
	}

	/**
	 * Gets the nb items ko.
	 *
	 * @return the nb items ko
	 */
	public int getNbItemsKo() {
		return nbItemsKo;
	}

	/**
	 * Sets the nb items ko.
	 *
	 * @param nbItemsKo the new nb items ko
	 */
	public void setNbItemsKo(int nbItemsKo) {
		this.nbItemsKo = nbItemsKo;
	}

	/**
	 * Gets the list errors.
	 *
	 * @return the list errors
	 */
	public List<String> getListErrors() {
		return listErrors;
	}

	/**
	 * Sets the list errors.
	 *
	 * @param listErrors the new list errors
	 */
	public void setListErrors(List<String> listErrors) {
		this.listErrors = listErrors;
	}

	/**
	 * Adds the item ok.
	 */
	public void addItemOk() {
		this.nbItemsOk ++;
		
	}
	
	/**
	 * Removes the item ok.
	 */
	public void removeItemOk() {
		this.nbItemsOk --;
		
	}

	/**
	 * Removes the item ko.
	 */
	public void removeItemKo() {
		this.nbItemsKo --;
		
	}

	/**
	 * Adds the item ko.
	 */
	public void addItemKo() {
		this.nbItemsKo ++;
		
	}
	
	public String formatErrorsReport() {
		String report = "";
		for(String error : listErrors) {
			report += error+"\n";
		}
		return report;
	}

	@Override
	public String toString() {
		return "DDRejectFileInfos [fileName=" + fileName + ", ddRequestLotId=" + ddRequestLotId
				+ ", theDDRequestFileWasRejected=" + theDDRequestFileWasRejected + ", returnStatusCode="
				+ returnStatusCode + ", listInvoiceRefsRejected=" + listInvoiceRefsRejected + ", nbItemsOk=" + nbItemsOk
				+ ", nbItemsKo=" + nbItemsKo + ", listErrors=" + listErrors + "]";
	}
	
    
    
}
