package org.meveo.model.quote;

import java.util.Date;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@CustomFieldEntity(cftCodePrefix = "QuoteLot")
@Table(name = "cpq_quote_lot", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "cpq_quote_version_id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_lot_seq")})
@NamedQuery(name = "QuoteLot.findByCodeAndVersion", query = "select q from QuoteLot q where q.code=:code and q.quoteVersion.id=:quoteVersionId")
//@NamedQuery(name = "QuoteCustomerService.findLastVersionByCode", query = "select qcs from QuoteCustomerService qcs left join qcs.quote qq where qq.code=:codeQuote order by qcs.quoteVersion desc")
public class QuoteLot extends BusinessCFEntity  {



    /**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_version_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private QuoteVersion quoteVersion;
    
	
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
		result = prime * result + Objects.hash(duration, executionDate, name, quoteVersion);
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
		QuoteLot other = (QuoteLot) obj;
		return duration == other.duration && Objects.equals(executionDate, other.executionDate)
				&& Objects.equals(name, other.name) 
				&& Objects.equals(quoteVersion, other.quoteVersion);
	}
	
	
}
