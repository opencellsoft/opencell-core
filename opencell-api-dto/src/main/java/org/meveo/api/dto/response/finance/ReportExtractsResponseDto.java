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

package org.meveo.api.dto.response.finance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.SearchResponse;

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
public class ReportExtractsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4212820720933880625L;

    /** The report extracts. */
    @XmlElementWrapper(name = "reportExtracts")
    @XmlElement(name = "reportExtract")
    private List<ReportExtractDto> reportExtracts = new ArrayList<>();

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
