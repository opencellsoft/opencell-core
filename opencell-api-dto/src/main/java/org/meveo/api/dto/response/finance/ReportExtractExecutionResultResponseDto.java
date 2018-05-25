package org.meveo.api.dto.response.finance;

import org.meveo.api.dto.finance.ReportExtractExecutionResultDto;
import org.meveo.api.dto.response.BaseResponse;

/** 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 15 May 2018
 * @lastModifiedVersion 5.1
 **/
public class ReportExtractExecutionResultResponseDto extends BaseResponse {

    private static final long serialVersionUID = -2912427285239879481L;
    
    private ReportExtractExecutionResultDto reportExtractExecutionResult;

    public ReportExtractExecutionResultDto getReportExtractExecutionResult() {
        return reportExtractExecutionResult;
    }

    public void setReportExtractExecutionResult(ReportExtractExecutionResultDto reportExtractExecutionResult) {
        this.reportExtractExecutionResult = reportExtractExecutionResult;
    }
    
}
