package org.meveo.model.quote;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_customer_service", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "quote_version"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_customer_service_seq"), })
@NamedQuery(name = "QuoteCustomerService.findByCodeAndVersion", query = "select q from QuoteCustomerService q where q.quote.code=:code and q.quoteVersion=:quoteVersion")
@NamedQuery(name = "QuoteCustomerService.findLastVersionByCode", query = "select qcs from QuoteCustomerService qcs left join qcs.quote qq where qq.code=:codeQuote order by qcs.quoteVersion desc")
public class QuoteCustomerService extends BusinessEntity {



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
	 * name 
	 */
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;
	
	@Column(name = "duration")
	private int duration;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "execution_date")
	private Date executionDate;

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the executionDate
	 */
	public Date getExecutionDate() {
		return executionDate;
	}

	/**
	 * @param executionDate the executionDate to set
	 */
	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(duration, executionDate, name, quote, quoteVersion);
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
		QuoteCustomerService other = (QuoteCustomerService) obj;
		return duration == other.duration && Objects.equals(executionDate, other.executionDate)
				&& Objects.equals(name, other.name) && Objects.equals(quote, other.quote)
				&& Objects.equals(quoteVersion, other.quoteVersion);
	}
	
	
}
