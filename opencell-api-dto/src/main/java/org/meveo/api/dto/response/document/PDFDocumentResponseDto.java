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

package org.meveo.api.dto.response.document;

import java.util.List;

import org.meveo.api.dto.response.BaseResponse;

/**
 * A DTO Class holding the response of Generating a document PDF file.
 */
public class PDFDocumentResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The list pdf file paths. */
    private List<String> listPdfFilePaths;
    
    /** The pdf file path. */
    private String pdfFilePath;
    
    /** The pdf file. */
    @Deprecated
    private byte[] pdfFile;
    
    
    private  List<byte[]>  pdfFiles;
    
    /**
     * @return the listPdfFilePaths
     */
    public List<String> getListPdfFilePaths() {
        return listPdfFilePaths;
    }

    /**
     * @param listPdfFilePaths the listPdfFilePaths to set
     */
    public void setListPdfFilePaths(List<String> listPdfFilePaths) {
        this.listPdfFilePaths = listPdfFilePaths;
    }

    /**
     * @return the pdfFiles
     */
    public List<byte[]> getPdfFiles() {
        return pdfFiles;
    }

    /**
     * @param pdfFiles the pdfFiles to set
     */
    public void setPdfFiles(List<byte[]> pdfFiles) {
        this.pdfFiles = pdfFiles;
    }

    /**
     * @return the pdfFilePath
     */
    public String getPdfFilePath() {
        return pdfFilePath;
    }

    /**
     * @return the pdfFile
     */
    public byte[] getPdfFile() {
        return pdfFile;
    }

    /**
     * @param pdfFilePath the pdfFilePath to set
     */
    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    /**
     * @param pdfFile the pdfFile to set
     */
    public void setPdfFile(byte[] pdfFile) {
        this.pdfFile = pdfFile;
    }

}
