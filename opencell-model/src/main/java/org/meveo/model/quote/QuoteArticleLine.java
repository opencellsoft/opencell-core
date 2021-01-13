package org.meveo.model.quote;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.CpqAccountingArticle;


/**
 * @author Khairi
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_article_line", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_article_line_seq"), })
public class QuoteArticleLine extends BusinessEntity {


    /**
     * quote customer service
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_customer_service_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private QuoteLot quoteLot;

    /**
     * billable account
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billable_account_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private BillingAccount billableAccount;

    /**
     * account article
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_article_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private AccountingArticle accountingArticle;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "service_quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    private BigDecimal serviceQuantity;

    @Column(name = "os_unit_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal osUnitPriceWithoutTax;

    @Column(name = "os_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal osPriceWithoutTax;

    @Column(name = "os_tax_code", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal osTaxCode;

    @Column(name = "os_tax_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private Integer osTaxRate;

    @Column(name = "os_price_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal osPriceWithTax;

    @Column(name = "rc_unit_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rcUnitPriceWithoutTax;

    @Column(name = "rc_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rcPriceWithoutTax;

    @Column(name = "rc_tax_code", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rcTaxCode;

    @Column(name = "rc_tax_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private Integer rcTaxRate;

    @Column(name = "rc_price_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rcPriceWithTax;


	/**
	 * @return the billableAccount
	 */
	public BillingAccount getBillableAccount() {
		return billableAccount;
	}

	/**
	 * @param billableAccount the billableAccount to set
	 */
	public void setBillableAccount(BillingAccount billableAccount) {
		this.billableAccount = billableAccount;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the osUnitPriceWithoutTax
	 */
	public BigDecimal getOsUnitPriceWithoutTax() {
		return osUnitPriceWithoutTax;
	}

	/**
	 * @param osUnitPriceWithoutTax the osUnitPriceWithoutTax to set
	 */
	public void setOsUnitPriceWithoutTax(BigDecimal osUnitPriceWithoutTax) {
		this.osUnitPriceWithoutTax = osUnitPriceWithoutTax;
	}

	/**
	 * @return the osPriceWithoutTax
	 */
	public BigDecimal getOsPriceWithoutTax() {
		return osPriceWithoutTax;
	}

	/**
	 * @param osPriceWithoutTax the osPriceWithoutTax to set
	 */
	public void setOsPriceWithoutTax(BigDecimal osPriceWithoutTax) {
		this.osPriceWithoutTax = osPriceWithoutTax;
	}

	/**
	 * @return the osTaxCode
	 */
	public BigDecimal getOsTaxCode() {
		return osTaxCode;
	}

	/**
	 * @param osTaxCode the osTaxCode to set
	 */
	public void setOsTaxCode(BigDecimal osTaxCode) {
		this.osTaxCode = osTaxCode;
	}

	/**
	 * @return the osTaxRate
	 */
	public Integer getOsTaxRate() {
		return osTaxRate;
	}

	/**
	 * @param osTaxRate the osTaxRate to set
	 */
	public void setOsTaxRate(Integer osTaxRate) {
		this.osTaxRate = osTaxRate;
	}

	/**
	 * @return the osPriceWithTax
	 */
	public BigDecimal getOsPriceWithTax() {
		return osPriceWithTax;
	}

	/**
	 * @param osPriceWithTax the osPriceWithTax to set
	 */
	public void setOsPriceWithTax(BigDecimal osPriceWithTax) {
		this.osPriceWithTax = osPriceWithTax;
	}

	/**
	 * @return the rcUnitPriceWithoutTax
	 */
	public BigDecimal getRcUnitPriceWithoutTax() {
		return rcUnitPriceWithoutTax;
	}

	/**
	 * @param rcUnitPriceWithoutTax the rcUnitPriceWithoutTax to set
	 */
	public void setRcUnitPriceWithoutTax(BigDecimal rcUnitPriceWithoutTax) {
		this.rcUnitPriceWithoutTax = rcUnitPriceWithoutTax;
	}

	/**
	 * @return the rcPriceWithoutTax
	 */
	public BigDecimal getRcPriceWithoutTax() {
		return rcPriceWithoutTax;
	}

	/**
	 * @param rcPriceWithoutTax the rcPriceWithoutTax to set
	 */
	public void setRcPriceWithoutTax(BigDecimal rcPriceWithoutTax) {
		this.rcPriceWithoutTax = rcPriceWithoutTax;
	}

	/**
	 * @return the rcTaxCode
	 */
	public BigDecimal getRcTaxCode() {
		return rcTaxCode;
	}

	/**
	 * @param rcTaxCode the rcTaxCode to set
	 */
	public void setRcTaxCode(BigDecimal rcTaxCode) {
		this.rcTaxCode = rcTaxCode;
	}

	/**
	 * @return the rcTaxRate
	 */
	public Integer getRcTaxRate() {
		return rcTaxRate;
	}

	/**
	 * @param rcTaxRate the rcTaxRate to set
	 */
	public void setRcTaxRate(Integer rcTaxRate) {
		this.rcTaxRate = rcTaxRate;
	}

	/**
	 * @return the rcPriceWithTax
	 */
	public BigDecimal getRcPriceWithTax() {
		return rcPriceWithTax;
	}

	/**
	 * @param rcPriceWithTax the rcPriceWithTax to set
	 */
	public void setRcPriceWithTax(BigDecimal rcPriceWithTax) {
		this.rcPriceWithTax = rcPriceWithTax;
	}

	/**
	 * @return the serviceQuantity
	 */
	public BigDecimal getServiceQuantity() {
		return serviceQuantity;
	}

	/**
	 * @param serviceQuantity the serviceQuantity to set
	 */
	public void setServiceQuantity(BigDecimal serviceQuantity) {
		this.serviceQuantity = serviceQuantity;
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

	/**
	 * @return the accountingArticle
	 */
	public AccountingArticle getAccountingArticle() {
		return accountingArticle;
	}

	/**
	 * @param accountingArticle the accountingArticle to set
	 */
	public void setAccountingArticle(AccountingArticle accountingArticle) {
		this.accountingArticle = accountingArticle;
	}
    
    
    
}
