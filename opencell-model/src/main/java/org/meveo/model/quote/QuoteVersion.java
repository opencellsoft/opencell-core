package org.meveo.model.quote;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_version", uniqueConstraints = @UniqueConstraint(columnNames = { "code" , "quoteVersion"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_version_seq"), })
public class QuoteVersion extends BaseEntity{


	/**
     * Record/entity identifier
     */
    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    @JsonProperty
    protected Long id;

    /**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private Quote quote;
    
	/**
	 * quoteVersion
	 */
	@Column(name = "quote_version", nullable = false)
	private Integer quoteVersion;
	/**
	 * status
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status", nullable = false)
	private VersionStatusEnum status;
	
	/**
	 * status date
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "status_date", nullable = false)
	private Date statusDate;
	
	/**
	 * start date of quote version 
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;
	
	/**
	 * end date of quote version
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private Date endDate;
	
	/**
	 * billing plan code
	 */
	// TODO: associe a l'entité plan facturation
	@Column(name = "billing_plan_code", length = 20)
	@Size(max = 20)
	private String billingPlanCode;

	/**
	 * @return the quoteVersion
	 */
	public Integer getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(Integer quoteVersion) {
		this.quoteVersion = quoteVersion;
	}

	/**
	 * @return the status
	 */
	public VersionStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(VersionStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the billingPlanCode
	 */
	public String getBillingPlanCode() {
		return billingPlanCode;
	}

	/**
	 * @param billingPlanCode the billingPlanCode to set
	 */
	public void setBillingPlanCode(String billingPlanCode) {
		this.billingPlanCode = billingPlanCode;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the quote
	 */
	public Quote getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(billingPlanCode, endDate, id, quote, quoteVersion, startDate, status, statusDate);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuoteVersion other = (QuoteVersion) obj;
		return Objects.equals(billingPlanCode, other.billingPlanCode) && Objects.equals(endDate, other.endDate)
				&& Objects.equals(id, other.id) && Objects.equals(quote, other.quote)
				&& Objects.equals(quoteVersion, other.quoteVersion) && Objects.equals(startDate, other.startDate)
				&& status == other.status && Objects.equals(statusDate, other.statusDate);
	}

	

	
	
}
