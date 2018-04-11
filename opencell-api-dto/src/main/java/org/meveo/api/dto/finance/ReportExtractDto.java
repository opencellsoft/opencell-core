package org.meveo.api.dto.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
public class ReportExtractDto extends EnableBusinessDto {

    private static final long serialVersionUID = 3600792942683148893L;

    @NotNull
    private ReportExtractScriptTypeEnum scriptType;

    @NotNull
    private String filenameFormat;

    private String category;

    private String scriptInstanceCode;
    private String sqlQuery;
    private Map<String, String> params = new HashMap<>();
    private Date startDate;
    private Date endDate;

    public ReportExtractDto() {
    }

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
        if (reportExtract.getScriptType().equals(ReportExtractScriptTypeEnum.JAVA)) {
            if (reportExtract.getScriptInstance() != null) {
                setScriptInstanceCode(reportExtract.getScriptInstance().getCode());
            }
        } else {
            setSqlQuery(reportExtract.getSqlQuery());
        }

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ReportExtractScriptTypeEnum getScriptType() {
        return scriptType;
    }

    public void setScriptType(ReportExtractScriptTypeEnum scriptType) {
        this.scriptType = scriptType;
    }

    public String getFilenameFormat() {
        return filenameFormat;
    }

    public void setFilenameFormat(String filenameFormat) {
        this.filenameFormat = filenameFormat;
    }

    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
