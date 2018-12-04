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
    @Deprecated
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
