package org.meveo.api.dto.finance;

import java.util.Date;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.finance.ReportExtractExecutionResult;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 15 May 2018
 * @lastModifiedVersion 5.1
 **/
public class ReportExtractExecutionResultDto extends BaseDto {

    private static final long serialVersionUID = -7664046664640373627L;
    
    private String reportExtractCode;
    private Date startDate = new Date();
    private Date endDate;
    private String filePath;
    private int lineCount;
    private ReportExtractExecutionOrigin origin;
    private String errorMessage;
    private boolean status;
    
    public ReportExtractExecutionResultDto() {
        
    }

    public ReportExtractExecutionResultDto(ReportExtractExecutionResult e) {
        reportExtractCode = e.getReportExtract().getCode();
        startDate = e.getStartDate();
        endDate = e.getEndDate();
        filePath = e.getFilePath();
        lineCount = e.getLineCount();
        origin = e.getOrigin();
        errorMessage = e.getErrorMessage();
        status = e.isStatus();
    }

    public String getReportExtractCode() {
        return reportExtractCode;
    }

    public void setReportExtractCode(String reportExtractCode) {
        this.reportExtractCode = reportExtractCode;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public ReportExtractExecutionOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(ReportExtractExecutionOrigin origin) {
        this.origin = origin;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
