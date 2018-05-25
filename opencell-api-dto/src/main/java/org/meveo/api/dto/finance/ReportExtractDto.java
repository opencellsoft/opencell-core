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

    /** The category. */
    private String category;

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
        setCode(reportExtract.getCode());
        setDescription(reportExtract.getDescription());
        setEndDate(reportExtract.getEndDate());
        setFilenameFormat(reportExtract.getFilenameFormat());
        setParams(reportExtract.getParams());
        setStartDate(reportExtract.getStartDate());
        setScriptType(reportExtract.getScriptType());
        setReportExtractResultType(reportExtract.getReportExtractResultType());
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

    public ReportExtractResultTypeEnum getReportExtractResultType() {
        return reportExtractResultType;
    }

    public void setReportExtractResultType(ReportExtractResultTypeEnum reportExtractResultType) {
        this.reportExtractResultType = reportExtractResultType;
    }
}