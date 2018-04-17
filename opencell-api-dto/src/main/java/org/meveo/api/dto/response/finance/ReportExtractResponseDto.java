package org.meveo.api.dto.response.finance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ReportExtractResponseDto.
 *
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "ReportExtractResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportExtractResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3067032223816612298L;

    /** The report extract. */
    private ReportExtractDto reportExtract;

    /**
     * Gets the report extract.
     *
     * @return the report extract
     */
    public ReportExtractDto getReportExtract() {
        return reportExtract;
    }

    /**
     * Sets the report extract.
     *
     * @param reportExtract the new report extract
     */
    public void setReportExtract(ReportExtractDto reportExtract) {
        this.reportExtract = reportExtract;
    }

}
