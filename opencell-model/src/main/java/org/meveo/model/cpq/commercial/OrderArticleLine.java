package org.meveo.model.cpq.commercial;

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

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "order_article_line", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "order_article_line_seq")})
public class OrderArticleLine extends BusinessEntity {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_customer_service_id")
	private OrderCustomerService orderCustomerService;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accounting_article_id", nullable = false)
	@NotNull
	private AccountingArticle accountingArticle;

	@Column(name = "quantity", nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal quantity;
	
	@Column(name = "quantity_service", nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal quantityService;

	@Column(name = "os_unit_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal osUnitPriceWithoutTax;

	@Column(name = "os_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal osPriceWithoutTax;
	
	@Column(name = "os_tax_code", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal osTaxCode;
	
	@Column(name = "os_tax_rate")
	private int osTaxRate;
	

	@Column(name = "os_price_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal osPriceWithTax;
	
	
	@Column(name = "rc_unit_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal rcUnitPriceWithoutTax;

	
	@Column(name = "rc_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal rcPriceWihoutTax;
	
	@Column(name = "rc_tax_code", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal rcTaCode;
	
	
	@Column(name = "rc_tax_rate")
	private int rcTaxRate;

	
	@Column(name = "rc_price_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal rcPriceWithTax;


	/**
	 * @return the order
	 */
	public CommercialOrder getOrder() {
		return order;
	}


	/**
	 * @param order the order to set
	 */
	public void setOrder(CommercialOrder order) {
		this.order = order;
	}


	/**
	 * @return the orderCustomerService
	 */
	public OrderCustomerService getOrderCustomerService() {
		return orderCustomerService;
	}


	/**
	 * @param orderCustomerService the orderCustomerService to set
	 */
	public void setOrderCustomerService(OrderCustomerService orderCustomerService) {
		this.orderCustomerService = orderCustomerService;
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
	 * @return the quantityService
	 */
	public BigDecimal getQuantityService() {
		return quantityService;
	}


	/**
	 * @param quantityService the quantityService to set
	 */
	public void setQuantityService(BigDecimal quantityService) {
		this.quantityService = quantityService;
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
	public int getOsTaxRate() {
		return osTaxRate;
	}


	/**
	 * @param osTaxRate the osTaxRate to set
	 */
	public void setOsTaxRate(int osTaxRate) {
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
	 * @return the rcPriceWihoutTax
	 */
	public BigDecimal getRcPriceWihoutTax() {
		return rcPriceWihoutTax;
	}


	/**
	 * @param rcPriceWihoutTax the rcPriceWihoutTax to set
	 */
	public void setRcPriceWihoutTax(BigDecimal rcPriceWihoutTax) {
		this.rcPriceWihoutTax = rcPriceWihoutTax;
	}


	/**
	 * @return the rcTaCode
	 */
	public BigDecimal getRcTaCode() {
		return rcTaCode;
	}


	/**
	 * @param rcTaCode the rcTaCode to set
	 */
	public void setRcTaCode(BigDecimal rcTaCode) {
		this.rcTaCode = rcTaCode;
	}


	/**
	 * @return the rcTaxRate
	 */
	public int getRcTaxRate() {
		return rcTaxRate;
	}


	/**
	 * @param rcTaxRate the rcTaxRate to set
	 */
	public void setRcTaxRate(int rcTaxRate) {
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
	
}
