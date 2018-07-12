package org.meveo.api.dto.document;

import java.util.List;
import java.util.Map;

import org.meveo.api.dto.BaseEntityDto;

/**
 * A Dto class to encapsulate the PDFTemplate request params.
 *
 * @author Said Ramli
 */
public class PDFTemplateDto extends BaseEntityDto {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The template fields. */
    private Map<String, String> templateFields;
    
    /** The template name. */
    private String templateName;
    
    /** The template path. */
    private String templatePath;
    
    /** The bar code fields. */
    public List<String> barCodeFields;
    
    /**
     * Gets the template fields.
     *
     * @return the templateFields
     */
    public Map<String, String> getTemplateFields() {
        return templateFields;
    }
    
    /**
     * Gets the template path.
     *
     * @return the templatePath
     */
    public String getTemplatePath() {
        return templatePath;
    }
    
    /**
     * Gets the bar code fields.
     *
     * @return the barCodeFields
     */
    public List<String> getBarCodeFields() {
        return barCodeFields;
    }
    
    /**
     * Sets the template fields.
     *
     * @param templateFields the templateFields to set
     */
    public void setTemplateFields(Map<String, String> templateFields) {
        this.templateFields = templateFields;
    }
    
    /**
     * Sets the template path.
     *
     * @param templatePath the templatePath to set
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }
    
    /**
     * Sets the bar code fields.
     *
     * @param barCodeFields the barCodeFields to set
     */
    public void setBarCodeFields(List<String> barCodeFields) {
        this.barCodeFields = barCodeFields;
    }

    /**
     * Gets the template name.
     *
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Sets the template name.
     *
     * @param templateName the templateName to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
