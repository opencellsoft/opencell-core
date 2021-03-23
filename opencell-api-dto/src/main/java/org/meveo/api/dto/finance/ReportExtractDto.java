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

package org.meveo.api.dto.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractResultTypeEnum;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;

/**
 * DTO class for ReportExtract entity
 *
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 5.1
 */
public class ReportExtractDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3600792942683148893L;

    /** The script type. */
    private ReportExtractScriptTypeEnum scriptType;

    /** The filename format. */
    private String filenameFormat;

    /** The file separator. */
    private String fileSeparator;

    /** The category. */
    private String category;
    
    /** The output directory. */
    private String outputDir;

    /** The script instance code. */
    private String scriptInstanceCode;

    /** The sql query. */
    private String sqlQuery;

    /** The params. */
    private Map<String, String> params = new HashMap<>();

    /** The start date. */
    private Date startDate;

    /** The end date. */
    private Date endDate;

    /**
     * The type of result that will be generated
     */
    private ReportExtractResultTypeEnum reportExtractResultType;

    private String style;

    private String imagePath;

    /**
     * Instantiate a new ReportExtract DTO
     */
    public ReportExtractDto() {
    }

    /**
     * Convert ReportExtract entity to DTO
     * 
     * @param reportExtract Entity to convert
     */
    public ReportExtractDto(ReportExtract reportExtract) {
        super(reportExtract);

        setCategory(reportExtract.getCategory());
        setOutputDir(reportExtract.getOutputDir());
        setCode(reportExtract.getCode());
        setDescription(reportExtract.getDescription());
        setEndDate(reportExtract.getEndDate());
        setFilenameFormat(reportExtract.getFilenameFormat());
        setFileSeparator(reportExtract.getFileSeparator());
        setParams(reportExtract.getParams());
        setStartDate(reportExtract.getStartDate());
        setScriptType(reportExtract.getScriptType());
        setReportExtractResultType(reportExtract.getReportExtractResultType());
        setStyle(reportExtract.getStyle());
        setImagePath(reportExtract.getImagePath());
        if (reportExtract.getScriptType().equals(ReportExtractScriptTypeEnum.JAVA)) {
            if (reportExtract.getScriptInstance() != null) {
                setScriptInstanceCode(reportExtract.getScriptInstance().getCode());
            }
        } else {
            setSqlQuery(reportExtract.getSqlQuery());
        }

    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the script type.
     *
     * @return the script type
     */
    public ReportExtractScriptTypeEnum getScriptType() {
        return scriptType;
    }

    /**
     * Sets the script type.
     *
     * @param scriptType the new script type
     */
    public void setScriptType(ReportExtractScriptTypeEnum scriptType) {
        this.scriptType = scriptType;
    }

    /**
     * Gets the filename format.
     *
     * @return the filename format
     */
    public String getFilenameFormat() {
        return filenameFormat;
    }

    /**
     * Sets the filename format.
     *
     * @param filenameFormat the new filename format
     */
    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    /**
     * Gets the script instance code.
     *
     * @return the script instance code
     */
    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    /**
     * Gets the file separator.
     *
     * @return the file separator
     */
    public String getFileSeparator() {
        return fileSeparator;
    }
    
    /**
     * Sets the file separator.
     *
     * @return the file separator
     */

    public void setFileSeparator(String fileSeparator) {
        this.fileSeparator = fileSeparator;
    }
    
    /**
     * Sets the script instance code.
     *
     * @param scriptInstanceCode the new script instance code
     */
    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    /**
     * Gets the sql query.
     *
     * @return the sql query
     */
    public String getSqlQuery() {
        return sqlQuery;
    }

    /**
     * Sets the sql query.
     *
     * @param sqlQuery the new sql query
     */
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Sets the params.
     *
     * @param params the params
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date.
     *
     * @param startDate the new start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date.
     *
     * @param endDate the new end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    /**
     * Gets the output directory.
     *      
     * @return the outputDir
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Sets the output directory.
     * 
     * @param outputDir the outputDir to set
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public ReportExtractResultTypeEnum getReportExtractResultType() {
        return reportExtractResultType;
    }

    public void setReportExtractResultType(ReportExtractResultTypeEnum reportExtractResultType) {
        this.reportExtractResultType = reportExtractResultType;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}