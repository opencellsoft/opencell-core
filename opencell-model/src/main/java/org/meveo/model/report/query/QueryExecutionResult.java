package org.meveo.model.report.query;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "query_execution_result")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "query_result_seq"), })
@NamedQueries({
        @NamedQuery(name = "QueryExecutionResult.findIdsByReportQuery", query = "select qer.id from QueryExecutionResult qer where qer.reportQuery = :reportQuery") })
public class QueryExecutionResult extends AuditableEntity {

    private static final long serialVersionUID = -4801486541562306601L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "execution_duration")
    private Long executionDuration;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "line_count")
    private Integer lineCount;

    @Column(name = "error_message")
    private String errorMessage;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private QueryStatusEnum queryStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "query_execution_mode")
    private QueryExecutionModeEnum queryExecutionMode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "query_scheduler_id")
    private QueryScheduler queryScheduler;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_query_id")
    private ReportQuery reportQuery;

    public QueryExecutionResult() {
        super();
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

    public Long getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(Long executionDuration) {
        this.executionDuration = executionDuration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getLineCount() {
        return lineCount;
    }

    public void setLineCount(Integer lineCount) {
        this.lineCount = lineCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public QueryStatusEnum getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(QueryStatusEnum queryStatus) {
        this.queryStatus = queryStatus;
    }

    public QueryExecutionModeEnum getQueryExecutionMode() {
        return queryExecutionMode;
    }

    public void setQueryExecutionMode(QueryExecutionModeEnum queryExecutionMode) {
        this.queryExecutionMode = queryExecutionMode;
    }

    public QueryScheduler getQueryScheduler() {
        return queryScheduler;
    }

    public void setQueryScheduler(QueryScheduler queryScheduler) {
        this.queryScheduler = queryScheduler;
    }

    /**
     * @return the reportQuery
     */
    public ReportQuery getReportQuery() {
        return reportQuery;
    }

    /**
     * @param reportQuery the reportQuery to set
     */
    public void setReportQuery(ReportQuery reportQuery) {
        this.reportQuery = reportQuery;
    }
}