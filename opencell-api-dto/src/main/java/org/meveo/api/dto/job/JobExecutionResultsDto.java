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

package org.meveo.api.dto.job;

import static java.lang.String.format;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobExecutionResultsDto implements Serializable {

    private static final long serialVersionUID = -8442798673237108841L;

    private List<JobExecutionResultDto> jobExecutionResults;

    private Long totalNumberOfRecords;

    public JobExecutionResultsDto() {
    }

    public JobExecutionResultsDto(List<JobExecutionResultDto> jobExecutionResults) {
        this.jobExecutionResults = jobExecutionResults;
    }

    public List<JobExecutionResultDto> getJobExecutionResults() {
        return jobExecutionResults;
    }

    public void setJobExecutionResults(List<JobExecutionResultDto> jobExecutionResults) {
        this.jobExecutionResults = jobExecutionResults;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

    @Override
    public String toString() {
        return format("JobExecutionResultListDto{jobExecutionResults=%s}", jobExecutionResults);
    }
}
