package org.meveo.api.dto.response.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.BaseResponse;

/** 
 * @author Edward P. Legaspi
 * @created 7 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "ReportExtractResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportExtractResponseDto extends BaseResponse {

    private static final long serialVersionUID = -3067032223816612298L;
    
    private ReportExtractDto reportExtract;

    public ReportExtractDto getReportExtract() {
        return reportExtract;
    }

    public void setReportExtract(ReportExtractDto reportExtract) {
        this.reportExtract = reportExtract;
    }
    
}
