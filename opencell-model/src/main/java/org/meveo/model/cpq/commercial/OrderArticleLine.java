package org.meveo.model.cpq.commercial;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "order_article_line", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "order_article_line_seq")})
@NamedQueries({
	@NamedQuery(name = "OrderArticleLine.findByOrderId", query = "FROM OrderArticleLine op WHERE op.order.id = :commercialOrderId")
})
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
	private OrderLot orderCustomerService;

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

	@OneToOne
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;

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
	public OrderLot getOrderCustomerService() {
		return orderCustomerService;
	}


	/**
	 * @param orderCustomerService the orderCustomerService to set
	 */
	public void setOrderCustomerService(OrderLot orderCustomerService) {
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

	public OrderProduct getOrderProduct() {
		return orderProduct;
	}

	public void setOrderProduct(OrderProduct orderProduct) {
		this.orderProduct = orderProduct;
	}
}
