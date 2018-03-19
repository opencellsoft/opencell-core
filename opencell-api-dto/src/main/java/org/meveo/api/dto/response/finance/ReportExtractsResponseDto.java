package org.meveo.api.dto.response.finance;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 * @created 7 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "ReportExtractsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportExtractsResponseDto extends BaseResponse {

    private static final long serialVersionUID = -4212820720933880625L;

    @XmlElementWrapper(name = "reportExtracts")
    @XmlElement(name = "reportExtract")
    private List<ReportExtractDto> reportExtracts;

    public List<ReportExtractDto> getReportExtracts() {
        return reportExtracts;
    }

    public void setReportExtracts(List<ReportExtractDto> reportExtracts) {
        this.reportExtracts = reportExtracts;
    }

}
