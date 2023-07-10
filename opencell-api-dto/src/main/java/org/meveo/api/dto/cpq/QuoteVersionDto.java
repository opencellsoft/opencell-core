package org.meveo.api.dto.cpq;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.QuoteVersion;

import io.swagger.v3.oas.annotations.media.Schema;

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
    @Schema(description = "The short description")
	private String shortDescription;
	/** The product code. */
	@NotNull
	@XmlElement(required = true)
    @Schema(description = "The code quote", required = true)
	private String quoteCode;
	
	/** The currentVersion. */
    @Schema(description = "The current version")
	private int currentVersion;

	/** The status. */
    @Schema(description = "Status of quote version", example = "Possible value : DRAFT, PUBLISHED, CLOSED")
	private VersionStatusEnum status;

	/** The statusDate. */
    @Schema(description = "The status date, it set automaically when status is changed")
	private Date statusDate;
    
	/** The startDate */
    @Schema(description = "The start date")
	private Date startDate;
    
	/** The endDate */
    @Schema(description = "The end date")
	private Date endDate;
    
	/** billing code */
    @Schema(description = "The code of the billing plan")
	private String billingPlanCode;
    
	/** Discount plan code */
    @Schema(description = "The code of the discount plan")
	private String discountPlanCode;

    @Schema(description = "The code of the contract")
   	private String contractCode;
    
    private Set<String> mediaCodes = new HashSet<String>();

	/** The comment. */
	@XmlAttribute()
    @Schema(description = "The comment")
	private String comment;
	
	@Schema(description = "The custom fields")
    protected CustomFieldsDto customFields;

	@Schema(description = "The associated PriceList code")
	private String priceListCode;

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
		this.billingPlanCode = q.getInvoicingPlan()!=null?q.getInvoicingPlan().getCode():null;
		this.startDate = q.getStartDate();
		this.statusDate = q.getStatusDate();
		this.discountPlanCode=q.getDiscountPlan()!=null?q.getDiscountPlan().getCode():null;
		this.comment=q.getComment();
		this.priceListCode = q.getPriceList() != null ? q.getPriceList().getCode() : null;
		this.contractCode=q.getContract()!=null?q.getContract().getCode():null;
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
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	public String getDiscountPlanCode() {
		return discountPlanCode;
	}

	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	public Set<String> getMediaCodes() {
		return mediaCodes;
	}

	public void setMediaCodes(Set<String> mediaCodes) {
		this.mediaCodes = mediaCodes;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * PriceListCode Getter
	 * @return the priceListCode
	 */
	public String getPriceListCode() {
		return priceListCode;
	}

	/**
	 * PriceListCode Setter
	 * @param priceListCode the value to set
	 */
	public void setPriceListCode(String priceListCode) {
		this.priceListCode = priceListCode;
	}
}