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
    
    /** The absolut paths. */
    private boolean absolutePaths;
    
    
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
}
