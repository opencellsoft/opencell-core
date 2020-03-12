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

package org.meveo.api.dto.document;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

/**
 * A Dto holding information required to generate the document pdf file.
 *
 * @author Said Ramli
 */
public class PDFDocumentRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The list templates. */
    private List<PDFTemplateDto> listTemplates;

    /** The document destination dir. */
    private String documentDestinationDir;
    
    /** The document name prefix. */
    private String documentNamePrefix;
    
    /** The return pdf. */
    private boolean returnPdf;
    
    /** 
     * A flag to leading the service generating pdf (/document/pdf) to combine all PDFs in one file , 
     * or to create a PDF file per template. */
    private boolean combineFiles;
    
    /** The absolut paths. */
    private boolean absolutePaths;

    /**
     * If true the pdf form is flattened and be a read-only pdf
     */
    private Boolean flattened;
    
    
    /**
     * Instantiates a new PDF contarct request dto.
     */
    public PDFDocumentRequestDto() {

    }

    /**
     * Gets the list templates.
     *
     * @return the listTemplates
     */
    public List<PDFTemplateDto> getListTemplates() {
        return listTemplates;
    }

    /**
     * Sets the list templates.
     *
     * @param listTemplates the listTemplates to set
     */
    public void setListTemplates(List<PDFTemplateDto> listTemplates) {
        this.listTemplates = listTemplates;
    }

    /**
     * Gets the document destination dir.
     *
     * @return the documentDestinationDir
     */
    public String getDocumentDestinationDir() {
        return documentDestinationDir;
    }

    /**
     * Gets the document name prefix.
     *
     * @return the documentNamePrefix
     */
    public String getDocumentNamePrefix() {
        return documentNamePrefix;
    }

    /**
     * Sets the document destination dir.
     *
     * @param documentDestinationDir the documentDestinationDir to set
     */
    public void setDocumentDestinationDir(String documentDestinationDir) {
        this.documentDestinationDir = documentDestinationDir;
    }

    /**
     * Sets the document name prefix.
     *
     * @param documentNamePrefix the documentNamePrefix to set
     */
    public void setDocumentNamePrefix(String documentNamePrefix) {
        this.documentNamePrefix = documentNamePrefix;
    }

    /**
     * @return the returnPdf
     */
    public boolean isReturnPdf() {
        return returnPdf;
    }

    /**
     * @return the absolutPaths
     */
    public boolean isAbsolutePaths() {
        return absolutePaths;
    }

    /**
     * @param returnPdf the returnPdf to set
     */
    public void setReturnPdf(boolean returnPdf) {
        this.returnPdf = returnPdf;
    }

    /**
     * @param absolutPaths the absolutPaths to set
     */
    public void setAbsolutePaths(boolean absolutPaths) {
        this.absolutePaths = absolutPaths;
    }

    /**
     * @return the combineFiles
     */
    public boolean isCombineFiles() {
        return combineFiles;
    }

    /**
     * @param combineFiles the combineFiles to set
     */
    public void setCombineFiles(boolean combineFiles) {
        this.combineFiles = combineFiles;
    }

    /**
     *
     * @return flattened flag
     */
    public Boolean isFlattened() {
        if(flattened == null){
            return true;
        }
        return flattened;
    }

    /**
     * Sets the flattened flag
     * @param flattened flattened flag
     */
    public void setFlattened(Boolean flattened) {
        this.flattened = flattened;
    }
}
