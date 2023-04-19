package org.meveo.model.quote;

import static javax.persistence.FetchType.LAZY;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
import org.meveo.model.AuditableCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.ReferenceIdentifierCode;
import org.meveo.model.ReferenceIdentifierDescription;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.commercial.InvoicingPlan;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.QuoteOffer;

@SuppressWarnings("serial")
@Entity
@WorkflowedEntity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "QuoteVersion")
@ReferenceIdentifierCode("quote")
@ReferenceIdentifierDescription("quoteVersion")
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
public class QuoteVersion extends AuditableCFEntity implements IReferenceEntity{

    /**
     * quote
     */
	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "cpq_quote_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private CpqQuote quote;
	
    /**
     * quote products
     */
    @OneToMany(mappedBy = "quoteVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteProduct> quoteProducts=new ArrayList<QuoteProduct>();
    
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
	 * invoicing plan 
	 */ 
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "invoicing_plan_id", referencedColumnName = "id")
	private InvoicingPlan invoicingPlan;
 
    @Column(name = "short_description", length = 255)
    @Size(max = 255)
    private String shortDescription;
    
    @OneToMany(mappedBy = "quoteVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteOffer> quoteOffers=new ArrayList<QuoteOffer>();
    
    
    @OneToMany(mappedBy = "quoteVersion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteArticleLine> quoteArticleLines=new ArrayList<QuoteArticleLine>();
    
    /**
	 * discountPlan attached to this quote version
	 */
    @ManyToOne(fetch = LAZY)
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    
    /**
 	  * XML file name
 	  */
 	 @Column(name = "xml_filename", length = 255)
 	 @Size(max = 255)
 	 private String xmlFilename;
 	 
 	 
 	 /**
	  * PDF file name
	  */
	@Column(name = "pdf_filename", length = 255)
	@Size(max = 255)
	private String pdfFilename;
	
	
	/**
	 * contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	private Contract contract;
	
	
	 /**
	  * comment
	  */
	@Column(name = "comment", length = 2000)
	@Size(max = 2000)
	private String comment;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "cpq_quote_version_media", joinColumns = @JoinColumn(name = "cpq_quote_version_id"), inverseJoinColumns = @JoinColumn(name = "cpq_media_id"))
	private List<Media> medias = new ArrayList<Media>();
	
	@OneToMany(mappedBy = "quoteVersion", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id")
	private List<QuotePrice> quotePrices = new ArrayList<QuotePrice>();
    
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
	 * @return the quoteProducts
	 */
	public List<QuoteProduct> getQuoteProducts() {
		return quoteProducts;
	}

	/**
	 * @param quoteProducts the quoteProducts to set
	 */
	public void setQuoteProducts(List<QuoteProduct> quoteProducts) {
		this.quoteProducts = quoteProducts;
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
				+ Objects.hash(invoicingPlan, endDate, id, quote, quoteVersion, startDate, status, statusDate);
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
		return Objects.equals(invoicingPlan, other.invoicingPlan) && Objects.equals(endDate, other.endDate)
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

	public InvoicingPlan getInvoicingPlan() {
		return invoicingPlan;
	}

	public void setInvoicingPlan(InvoicingPlan invoicingPlan) {
		this.invoicingPlan = invoicingPlan;
	}

	@Override
	public String getReferenceCode() {
		return quote.getCode();
	}

	@Override
	public void setReferenceCode(Object value) {
		setQuote((CpqQuote) value);
		
	}

	@Override
	public String getReferenceDescription() {
		return "" + quoteVersion;
	}

	public List<QuoteArticleLine> getQuoteArticleLines() {
		return quoteArticleLines;
	}

	public void setQuoteArticleLines(List<QuoteArticleLine> quoteArticleLines) {
		this.quoteArticleLines = quoteArticleLines;
	}

	public String getXmlFilename() {
		return xmlFilename;
	}

	public void setXmlFilename(String xmlFilename) {
		this.xmlFilename = xmlFilename;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public String getPdfFilename() {
		return pdfFilename;
	}

	public void setPdfFilename(String pdfFilename) {
		this.pdfFilename = pdfFilename;
	}

	public List<Media> getMedias() {
		return medias;
	}

	public void setMedias(List<Media> medias) {
		this.medias = medias;
	}

	public List<QuotePrice> getQuotePrices() {
		return quotePrices;
	}

	public void setQuotePrices(List<QuotePrice> quotePrices) {
		this.quotePrices = quotePrices;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	
	
	
	
	
	
	
}
