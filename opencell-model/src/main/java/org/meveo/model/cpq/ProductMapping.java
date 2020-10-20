package org.meveo.model.cpq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.ChargeTemplate;

/** 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_product_mapping", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_mapping_seq"), })
public class ProductMapping extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * product code associated to product mapping
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false, referencedColumnName = "id")
	@NotNull
	private Product product;
	
	
	
	/**
	 * Name of the first service product.
	 */
	@Column(name = "service_1", length = 20)
	@Size(max = 20)
	private String service1;
	
	
	/**
	 * value of the first service product.
	 */
	@Column(name = "service_1_value", length = 100)
	@Size(max = 100)
	private String service1Value;
	
	/**
	 * Name of the second service product.
	 */
	@Column(name = "service_2", length = 20)
	@Size(max = 20)
	private String service2;
	
	
	/**
	 * value of the second service product.
	 */
	@Column(name = "service_2_value", length = 100)
	@Size(max = 100)
	private String service2Value;
	
	
	/**
	 * Name of the third service product.
	 */
	@Column(name = "service_3", length = 20)
	@Size(max = 20)
	private String service3;
	
	
	/**
	 * value of the third service product.
	 */
	@Column(name = "service_3_value", length = 100)
	@Size(max = 100)
	private String service3Value;
	
	   /**
     * Accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_id",referencedColumnName = "id")
    private AccountingCode accountingCode;
	
	
    /**
     * charge instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id",referencedColumnName = "id")
    private ChargeTemplate chargeTemplate;


	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}


	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}


	/**
	 * @return the service1
	 */
	public String getService1() {
		return service1;
	}


	/**
	 * @param service1 the service1 to set
	 */
	public void setService1(String service1) {
		this.service1 = service1;
	}


	/**
	 * @return the service1Value
	 */
	public String getService1Value() {
		return service1Value;
	}


	/**
	 * @param service1Value the service1Value to set
	 */
	public void setService1Value(String service1Value) {
		this.service1Value = service1Value;
	}


	/**
	 * @return the service2
	 */
	public String getService2() {
		return service2;
	}


	/**
	 * @param service2 the service2 to set
	 */
	public void setService2(String service2) {
		this.service2 = service2;
	}


	/**
	 * @return the service2Value
	 */
	public String getService2Value() {
		return service2Value;
	}


	/**
	 * @param service2Value the service2Value to set
	 */
	public void setService2Value(String service2Value) {
		this.service2Value = service2Value;
	}


	/**
	 * @return the service3
	 */
	public String getService3() {
		return service3;
	}


	/**
	 * @param service3 the service3 to set
	 */
	public void setService3(String service3) {
		this.service3 = service3;
	}


	/**
	 * @return the service3Value
	 */
	public String getService3Value() {
		return service3Value;
	}


	/**
	 * @param service3Value the service3Value to set
	 */
	public void setService3Value(String service3Value) {
		this.service3Value = service3Value;
	}


	/**
	 * @return the accountingCode
	 */
	public AccountingCode getAccountingCode() {
		return accountingCode;
	}


	/**
	 * @param accountingCode the accountingCode to set
	 */
	public void setAccountingCode(AccountingCode accountingCode) {
		this.accountingCode = accountingCode;
	}


	/**
	 * @return the chargeTemplate
	 */
	public ChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}


	/**
	 * @param chargeTemplate the chargeTemplate to set
	 */
	public void setChargeTemplate(ChargeTemplate chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}



 



}
