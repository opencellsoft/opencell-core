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
    private List<String> barCodeFields;
    
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
