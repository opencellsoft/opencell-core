package org.meveo.api.dto.response.finance;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.finance.ReportExtractExecutionResultDto;
import org.meveo.api.dto.response.SearchResponse;

/** 
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 15 May 2018
 **/
public class ReportExtractExecutionResultsResponseDto extends SearchResponse {

    private static final long serialVersionUID = -2912427285239879481L;
    
    private List<ReportExtractExecutionResultDto> reportExtractExecutionResults = new ArrayList<>();

    public List<ReportExtractExecutionResultDto> getReportExtractExecutionResults() {
        return reportExtractExecutionResults;
    }

    public void setReportExtractExecutionResults(List<ReportExtractExecutionResultDto> reportExtractExecutionResults) {
        this.reportExtractExecutionResults = reportExtractExecutionResults;
    }

    
}
