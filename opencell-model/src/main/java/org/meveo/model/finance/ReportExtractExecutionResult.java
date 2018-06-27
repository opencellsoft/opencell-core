package org.meveo.model.finance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.NotifiableEntity;

/**
 * Result of ReportExtract execution.
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 23 Apr 2018
 * @lastModifiedVersion 5.1
 **/
@Entity
@Table(name = "dwh_report_extract_execution")
@NotifiableEntity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_report_extract_execution_seq"), })
public class ReportExtractExecutionResult extends AuditableEntity {

    private static final long serialVersionUID = 5905033282016568860L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_extract_id")
    private ReportExtract reportExtract;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "line_count")
    private int lineCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin")
    private ReportExtractExecutionOrigin origin;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "status")
    private boolean status;

    public ReportExtract getReportExtract() {
        return reportExtract;
    }

    public void setReportExtract(ReportExtract reportExtract) {
        this.reportExtract = reportExtract;
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
        setStatus(false);
        this.errorMessage = errorMessage;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
