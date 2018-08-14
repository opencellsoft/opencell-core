package org.meveo.api.dto.response.document;

import org.meveo.api.dto.response.BaseResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A DTO Class holding the response of Generating a  PDF Contract file.
 */
public class PDFContractResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The pdf file path. */
    private String pdfFilePath;
    
    /** The pdf file. */
    private byte[] pdfFile;

    /**
     * Checks if is success result.
     *
     * @param returnPdf the return pdf
     * @return true, if is success result
     */
    @JsonIgnore
    public boolean isSuccessResult(boolean returnPdf) {
        return (pdfFilePath !=  null) && (!returnPdf || pdfFile != null );
    }
    
    /**
     * Gets the pdf file path.
     *
     * @return the pdfFilePath
     */
    public String getPdfFilePath() {
        return pdfFilePath;
    }
    
    /**
     * Sets the pdf file path.
     *
     * @param pdfFilePath the pdfFilePath to set
     */
    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }
    
    /**
     * Gets the pdf file.
     *
     * @return the pdfFile
     */
    public byte[] getPdfFile() {
        return pdfFile;
    }
    
    /**
     * Sets the pdf file.
     *
     * @param pdfFile the pdfFile to set
     */
    public void setPdfFile(byte[] pdfFile) {
        this.pdfFile = pdfFile;
    }

}
