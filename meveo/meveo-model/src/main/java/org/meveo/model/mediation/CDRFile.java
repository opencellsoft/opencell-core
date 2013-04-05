/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "MEDINA_CDR_FILE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_CDR_FILE_SEQ")
public class CDRFile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "FILENAME")
    private String filename;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date analysisStartDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date analysisEndDate;
    
    @Column(name = "UNIQ_STEP_TOTAL_DURATION")
    private Long uniquenessStepTotalDuration;
    
    @Column(name = "UNIQ_STEP_EXECUTION_COUNT")
    private Long uniquenessStepExecutionCount;
    
    @Column(name = "UNIQ_STEP_AVERAGE_DURATION", columnDefinition = "float")
    private double uniquenessStepAverageDuration;
    
    @Column(name = "ACCESS_STEP_TOTAL_DURATION")
    private Long accessStepTotalDuration;
    
    @Column(name = "ACCESS_STEP_EXECUTION_COUNT")
    private Long accessStepExecutionCount;
    
    @Column(name = "ACCESS_STEP_AVERAGE_DURATION", columnDefinition = "float")
    private Double accessStepAverageDuration;
    
    @Column(name = "ZONNING_STEP_TOTAL_DURATION")
    private Long zonningStepTotalDuration;
    
    @Column(name = "ZONNING_STEP_EXECUTION_COUNT")
    private Long zonningStepExecutionCount;
    
    @Column(name = "ZONNING_STEP_AVERAGE_DURATION", columnDefinition = "float")
    private Double zonningStepAverageDuration;
    
    @Column(name = "PROV_STEP_TOTAL_DURATION")
    private Long provissioningStepTotalDuration;
    
    @Column(name = "PROV_STEP_EXECUTION_COUNT")
    private Long provissioningStepExecutionCount;
    
    @Column(name = "PROV_STEP_AVERAGE_DURATION", columnDefinition = "float")
    private Double provissioningStepAverageDuration;
    
    @Column(name = "PROCESS_DURATION")
    private Long processDuration;
    
    @Column(name = "COMMIT_DURATION")
    private Long commitDuration;
    
    @Column(name = "TOTAL_DURATION")
    private Long totalDuration;

    @Column(name = "ERROR_FILENAME")
    private String errorFilename;
    
    @Column(name = "IGNORED_FILENAME")
    private String ignoredFilename;

    @Column(name = "PARSED_CDR")
    private Long parsedCDRs;

    @Column(name = "SUCCESSED_CDR")
    private Long successedCDRs;

    @Column(name = "REJECTED_CDR")
    private Long rejectedCDRs;

    @Column(name = "IGNORED_CDR")
    private Long ignoredCDRs;
    
    @Column(name = "USAGE_DATA_VOLUME")
    private Long usageDATAVolume;
    
    @Column(name = "USAGE_VOICE_VOLUME")
    private Long usageVOICEVolume;
    
    @Column(name = "USAGE_SMS_VOLUME")
    private Long usageSMSVolume;

    @Column(name = "REJECTED_DATA_VOLUME")
    private Long rejectedDATAVolume;
    
    @Column(name = "REJECTED_VOICE_VOLUME")
    private Long rejectedVOICEVolume;
    
    @Column(name = "REJECTED_SMS_VOLUME")
    private Long rejectedSMSVolume;
    
    @Column(name = "FILE_DATE")
    private Date fileDate;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getAnalysisStartDate() {
        return analysisStartDate;
    }

    public void setAnalysisStartDate(Date analysisStartDate) {
        this.analysisStartDate = analysisStartDate;
    }

    public Date getAnalysisEndDate() {
        return analysisEndDate;
    }

    public void setAnalysisEndDate(Date analysisEndDate) {
        this.analysisEndDate = analysisEndDate;
    }

    public String getErrorFilename() {
        return errorFilename;
    }

    public void setErrorFilename(String errorFilename) {
        this.errorFilename = errorFilename;
    }

    public String getIgnoredFilename() {
        return ignoredFilename;
    }

    public void setIgnoredFilename(String ignoredFilename) {
        this.ignoredFilename = ignoredFilename;
    }

    public Long getIgnoredCDRs() {
        return ignoredCDRs;
    }

    public void setIgnoredCDRs(Long ignoredCDRs) {
        this.ignoredCDRs = ignoredCDRs;
    }

    public Long getParsedCDRs() {
        return parsedCDRs;
    }

    public void setParsedCDRs(Long parsedCDRs) {
        this.parsedCDRs = parsedCDRs;
    }

    public Long getSuccessedCDRs() {
        return successedCDRs;
    }

    public void setSuccessedCDRs(Long successedCDRs) {
        this.successedCDRs = successedCDRs;
    }

    public Long getRejectedCDRs() {
        return rejectedCDRs;
    }

    public void setRejectedCDRs(Long rejectedCDRs) {
        this.rejectedCDRs = rejectedCDRs;
    }

	public Long getUsageDATAVolume() {
		return usageDATAVolume;
	}

	public void setUsageDATAVolume(Long usageDATAVolume) {
		this.usageDATAVolume = usageDATAVolume;
	}

	public Long getUsageVOICEVolume() {
		return usageVOICEVolume;
	}

	public void setUsageVOICEVolume(Long usageVOICEVolume) {
		this.usageVOICEVolume = usageVOICEVolume;
	}

	public Long getUsageSMSVolume() {
		return usageSMSVolume;
	}

	public void setUsageSMSVolume(Long usageSMSVolume) {
		this.usageSMSVolume = usageSMSVolume;
	}

	public Long getRejectedDATAVolume() {
		return rejectedDATAVolume;
	}

	public void setRejectedDATAVolume(Long rejectedDATAVolume) {
		this.rejectedDATAVolume = rejectedDATAVolume;
	}

	public Long getRejectedVOICEVolume() {
		return rejectedVOICEVolume;
	}

	public void setRejectedVOICEVolume(Long rejectedVOICEVolume) {
		this.rejectedVOICEVolume = rejectedVOICEVolume;
	}

	public Long getRejectedSMSVolume() {
		return rejectedSMSVolume;
	}

	public void setRejectedSMSVolume(Long rejectedSMSVolume) {
		this.rejectedSMSVolume = rejectedSMSVolume;
	}

	public Date getFileDate() {
		return fileDate;
	}

	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	public long getUniquenessStepTotalDuration() {
		return uniquenessStepTotalDuration;
	}

	public void setUniquenessStepTotalDuration(long uniquenessStepTotalDuration) {
		this.uniquenessStepTotalDuration = uniquenessStepTotalDuration;
	}

	public long getUniquenessStepExecutionCount() {
		return uniquenessStepExecutionCount;
	}

	public void setUniquenessStepExecutionCount(long uniquenessStepExecutionCount) {
		this.uniquenessStepExecutionCount = uniquenessStepExecutionCount;
	}

	public double getUniquenessStepAverageDuration() {
		return uniquenessStepAverageDuration;
	}

	public void setUniquenessStepAverageDuration(
			double uniquenessStepAverageDuration) {
		this.uniquenessStepAverageDuration = uniquenessStepAverageDuration;
	}

	public long getAccessStepTotalDuration() {
		return accessStepTotalDuration;
	}

	public void setAccessStepTotalDuration(long accessStepTotalDuration) {
		this.accessStepTotalDuration = accessStepTotalDuration;
	}

	public long getAccessStepExecutionCount() {
		return accessStepExecutionCount;
	}

	public void setAccessStepExecutionCount(long accessStepExecutionCount) {
		this.accessStepExecutionCount = accessStepExecutionCount;
	}

	public double getAccessStepAverageDuration() {
		return accessStepAverageDuration;
	}

	public void setAccessStepAverageDuration(double accessStepAverageDuration) {
		this.accessStepAverageDuration = accessStepAverageDuration;
	}

	public long getZonningStepTotalDuration() {
		return zonningStepTotalDuration;
	}

	public void setZonningStepTotalDuration(long zonningStepTotalDuration) {
		this.zonningStepTotalDuration = zonningStepTotalDuration;
	}

	public long getZonningStepExecutionCount() {
		return zonningStepExecutionCount;
	}

	public void setZonningStepExecutionCount(long zonningStepExecutionCount) {
		this.zonningStepExecutionCount = zonningStepExecutionCount;
	}

	public double getZonningStepAverageDuration() {
		return zonningStepAverageDuration;
	}

	public void setZonningStepAverageDuration(double zonningStepAverageDuration) {
		this.zonningStepAverageDuration = zonningStepAverageDuration;
	}

	public long getProvissioningStepTotalDuration() {
		return provissioningStepTotalDuration;
	}

	public void setProvissioningStepTotalDuration(
			long provissioningStepTotalDuration) {
		this.provissioningStepTotalDuration = provissioningStepTotalDuration;
	}

	public long getProvissioningStepExecutionCount() {
		return provissioningStepExecutionCount;
	}

	public void setProvissioningStepExecutionCount(
			long provissioningStepExecutionCount) {
		this.provissioningStepExecutionCount = provissioningStepExecutionCount;
	}

	public double getProvissioningStepAverageDuration() {
		return provissioningStepAverageDuration;
	}

	public void setProvissioningStepAverageDuration(
			double provissioningStepAverageDuration) {
		this.provissioningStepAverageDuration = provissioningStepAverageDuration;
	}

	public long getProcessDuration() {
		return processDuration;
	}

	public void setProcessDuration(long processDuration) {
		this.processDuration = processDuration;
	}

	public long getCommitDuration() {
		return commitDuration;
	}

	public void setCommitDuration(long commitDuration) {
		this.commitDuration = commitDuration;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public void setTotalDuration(long totalDuration) {
		this.totalDuration = totalDuration;
	}
	
}
