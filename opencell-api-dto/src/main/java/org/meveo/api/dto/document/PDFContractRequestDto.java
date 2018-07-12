package org.meveo.api.dto.document;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

/**
 * A Dto holding information required to generate the contract pdf file.
 *
 * @author Said Ramli
 */
public class PDFContractRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The list templates. */
    private List<PDFTemplateDto> listTemplates;

    /** The contract destination dir. */
    private String contractDestinationDir;
    
    /** The contarct name prefix. */
    private String contarctNamePrefix;
    
    /** The return pdf. */
    private Boolean returnPdf;
    
    /**
     * Instantiates a new PDF contarct request dto.
     */
    public PDFContractRequestDto() {

    }

    /**
     * Gets the contarct name prefix.
     *
     * @return the contarctNamePrefix
     */
    public String getContarctNamePrefix() {
        return contarctNamePrefix;
    }

    /**
     * Sets the contarct name prefix.
     *
     * @param contarctNamePrefix the contarctNamePrefix to set
     */
    public void setContarctNamePrefix(String contarctNamePrefix) {
        this.contarctNamePrefix = contarctNamePrefix;
    }

    /**
     * Gets the contract destination dir.
     *
     * @return the contractDestinationDir
     */
    public String getContractDestinationDir() {
        return contractDestinationDir;
    }

    /**
     * Sets the contract destination dir.
     *
     * @param contractDestinationDir the contractDestinationDir to set
     */
    public void setContractDestinationDir(String contractDestinationDir) {
        this.contractDestinationDir = contractDestinationDir;
    }

    /**
     * Gets the return pdf.
     *
     * @return the returnPdf
     */
    public Boolean getReturnPdf() {
        return returnPdf;
    }

    /**
     * Sets the return pdf.
     *
     * @param returnPdf the returnPdf to set
     */
    public void setReturnPdf(Boolean returnPdf) {
        this.returnPdf = returnPdf;
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
}
