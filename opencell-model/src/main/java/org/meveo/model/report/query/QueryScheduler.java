package org.meveo.model.report.query;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobInstance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "query_scheduler")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @Parameter(name = "sequence_name", value = "query_scheduler_seq") })
@NamedQueries({
    @NamedQuery(name = "QueryScheduler.findByReportQuery", query = "select qs from QueryScheduler qs where qs.reportQuery = :reportQuery") })
public class QueryScheduler extends BusinessEntity {

	private static final long serialVersionUID = 6103654572821099121L;

	@Column(name = "file_format")
    private String fileFormat;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE )
    @JoinTable(name = "query_scheduler_users", joinColumns = @JoinColumn(name = "query_scheduler_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> usersToNotify = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "query_scheduler_emails", joinColumns = @JoinColumn(name = "query_scheduler_id"))
    @Column(name = "emails")
    private List<String> emailsToNotify = new ArrayList<>();
    
    @Embedded
    private QueryTimer queryTimer = new QueryTimer();
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_query_id")
	private ReportQuery reportQuery;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_instance_id")
	private JobInstance jobInstance;

	@Convert(converter = NumericBooleanConverter.class)
	@Column(name = "is_query_scheduler")
	private boolean isQueryScheduler;

	public QueryScheduler() {
		super();
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public List<User> getUsersToNotify() {
		return usersToNotify;
	}

	public void setUsersToNotify(List<User> usersToNotify) {
		this.usersToNotify = usersToNotify;
	}

	public void setQueryTimer(QueryTimer queryTimer) {
		this.queryTimer = queryTimer;
	}

	public QueryTimer getQueryTimer() {
		return queryTimer;
	}

	public ReportQuery getReportQuery() {
		return reportQuery;
	}

	public void setReportQuery(ReportQuery reportQuery) {
		this.reportQuery = reportQuery;
	}

	public JobInstance getJobInstance() {
		return jobInstance;
	}

	public void setJobInstance(JobInstance jobInstance) {
		this.jobInstance = jobInstance;
	}

	public List<String> getEmailsToNotify() {
		return emailsToNotify;
	}

	public void setEmailsToNotify(List<String> emailsToNotify) {
		this.emailsToNotify = emailsToNotify;
	}

	public void setIsQueryScheduler(boolean isQueryScheduler) {
		this.isQueryScheduler = isQueryScheduler;
	}

	public boolean getIsQueryScheduler() {
		return isQueryScheduler;
	}
}