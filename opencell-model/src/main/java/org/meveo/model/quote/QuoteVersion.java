package org.meveo.model.quote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_version", uniqueConstraints = @UniqueConstraint(columnNames = { "cpq_quote_id" , "quote_version"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_version_seq"), })
@NamedQueries({ 
	@NamedQuery(name = "QuoteVersion.findByCode", query = "select qv from QuoteVersion qv left join qv.quote qq where qq.code=:code order by qv.quoteVersion desc"),
	@NamedQuery(name = "QuoteVersion.countCode", query = "select count(*) from QuoteVersion qv left join qv.quote qq where qq.code=:code"),
	@NamedQuery(name = "QuoteVersion.findByQuoteIdAndStatusActive", query = "select qv from QuoteVersion qv left join qv.quote qq where qq.id=:id and qv.status=org.meveo.model.cpq.enums.VersionStatusEnum.PUBLISHED"),
	@NamedQuery(name = "QuoteVersion.findByQuoteId", query = "select qv from QuoteVersion qv left join qv.quote qq where qq.id=:id"),
	@NamedQuery(name = "QuoteVersion.findByQuoteAndVersion", query = "select qv from QuoteVersion qv left join qv.quote qq where qq.code=:code and qv.quoteVersion=:quoteVersion")
})
public class QuoteVersion extends BaseEntity{


    /**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private CpqQuote quote;
    
	/**
	 * quoteVersion
	 */
	@Column(name = "quote_version", nullable = false)
	private Integer quoteVersion;
	/**
	 * status
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private VersionStatusEnum status;
	
	/**
	 * status date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_date", nullable = false)
	private Date statusDate;
	
	/**
	 * start date of quote version 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;
	
	/**
	 * end date of quote version
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;
	
	/**
	 * billing plan code
	 */
	// TODO: associe a l'entité plan facturation
	@Column(name = "billing_plan_code", length = 20)
	@Size(max = 20)
	private String billingPlanCode;
 
    @Column(name = "short_description", length = 255)
    @Size(max = 255)
    private String shortDescription;
    
    @OneToMany(mappedBy = "quoteVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteOffer> quoteOffers=new ArrayList<QuoteOffer>();
    

    /**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_quote_lot_id")
    private QuoteLot quoteLot;
    
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
	 * @return the quote
	 */
	public CpqQuote getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(CpqQuote quote) {
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

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the quoteOffers
	 */
	public List<QuoteOffer> getQuoteOffers() {
		return quoteOffers;
	}

	/**
	 * @param quoteOffers the quoteOffers to set
	 */
	public void setQuoteOffers(List<QuoteOffer> quoteOffers) {
		this.quoteOffers = quoteOffers;
	}

	/**
	 * @return the quoteLot
	 */
	public QuoteLot getQuoteLot() {
		return quoteLot;
	}

	/**
	 * @param quoteLot the quoteLot to set
	 */
	public void setQuoteLot(QuoteLot quoteLot) {
		this.quoteLot = quoteLot;
	}

	

	
	
}
