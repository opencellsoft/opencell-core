package org.meveo.api.dto.cpq;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.QuoteVersion;

/**
 * 
 * @author Mbarek-Ay
 * @version 10.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteVersionDto extends BaseEntityDto {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7824004884683019697L;
	/** The shortDescription. */
	@XmlAttribute()
	private String shortDescription;
	/** The product code. */
	@NotNull
	@XmlElement(required = true)
	private String quoteCode;
	/** The currentVersion. */
	private int currentVersion;

	/** The status. */
	private VersionStatusEnum status;

	/** The statusDate. */
	private Date statusDate;
	/** The startDate */
	private Date startDate;
	/** The endDate */
	private Date endDate;
	/** billing code */
	private String billingPlanCode;
	/** quote lot attached to quote version **/
	private String quoteLotCode;

	/**
	 * Instantiates a new product version dto.
	 */
	public QuoteVersionDto() {
	}

	public QuoteVersionDto(QuoteVersion q) {
	 super();
	 init(q);

	}
	private void init(QuoteVersion q) {
		this.shortDescription = q.getShortDescription();
		this.quoteCode = q.getQuote().getCode();
		this.currentVersion = q.getQuoteVersion();
		this.status = q.getStatus();
		this.endDate = q.getEndDate();
		this.billingPlanCode = q.getBillingPlanCode();
		this.startDate = q.getStartDate();
		if(q.getQuoteLot() != null)
			this.quoteLotCode = q.getQuoteLot().getCode();
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
	 * @return the currentVersion
	 */
	public int getCurrentVersion() {
		return currentVersion;
	}

	/**
	 * @param currentVersion the currentVersion to set
	 */
	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
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

	public String getQuoteCode() {
		return quoteCode;
	}

	public void setQuoteCode(String quoteCode) {
		this.quoteCode = quoteCode;
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
	 * @return the quoteLotCode
	 */
	public String getQuoteLotCode() {
		return quoteLotCode;
	}

	/**
	 * @param quoteLotCode the quoteLotCode to set
	 */
	public void setQuoteLotCode(String quoteLotCode) {
		this.quoteLotCode = quoteLotCode;
	}


	
	

}