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
 * The Class ReportExtractsResponseDto.
 *
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "ReportExtractsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportExtractsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4212820720933880625L;

    /** The report extracts. */
    @XmlElementWrapper(name = "reportExtracts")
    @XmlElement(name = "reportExtract")
    private List<ReportExtractDto> reportExtracts;

    /**
     * Gets the report extracts.
     *
     * @return the report extracts
     */
    public List<ReportExtractDto> getReportExtracts() {
        return reportExtracts;
    }

    /**
     * Sets the report extracts.
     *
     * @param reportExtracts the new report extracts
     */
    public void setReportExtracts(List<ReportExtractDto> reportExtracts) {
        this.reportExtracts = reportExtracts;
    }

}
