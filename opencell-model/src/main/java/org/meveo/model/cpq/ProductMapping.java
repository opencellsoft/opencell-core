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
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;

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
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_1",referencedColumnName = "id")
	private ServiceTemplate service1;
	
	
	
	/**
	 * value of the first service product.
	 */
	@Column(name = "service_1_value", length = 100)
	@Size(max = 100)
	private String service1Value;
	
	/**
	 * Name of the second service product.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_2",referencedColumnName = "id")
	private ServiceTemplate service2;
	
	
	/**
	 * value of the second service product.
	 */
	@Column(name = "service_2_value", length = 100)
	@Size(max = 100)
	private String service2Value;
	
	
	/**
	 * Name of the third service product.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_3",referencedColumnName = "id")
	private ServiceTemplate service3;
	
	
	/**
	 * value of the third service product.
	 */
	@Column(name = "service_3_value", length = 100)
	@Size(max = 100)
	private String service3Value;
	
	   /**
     * Accounting article
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_article_id",referencedColumnName = "id")
    private AccountingArticle accountingArticle;
	
	
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
	 * @return the service1Value
	 */
	public String getService1Value() {
		return service1Value;
	}


 


	  

	/**
	 * @param service2Value the service2Value to set
	 */
	public void setService2Value(String service2Value) {
		this.service2Value = service2Value;
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


	/**
	 * @return the service1
	 */
	public ServiceTemplate getService1() {
		return service1;
	}


	/**
	 * @param service1 the service1 to set
	 */
	public void setService1(ServiceTemplate service1) {
		this.service1 = service1;
	}


	/**
	 * @return the service2
	 */
	public ServiceTemplate getService2() {
		return service2;
	}


	/**
	 * @param service2 the service2 to set
	 */
	public void setService2(ServiceTemplate service2) {
		this.service2 = service2;
	}


	/**
	 * @return the service3
	 */
	public ServiceTemplate getService3() {
		return service3;
	}


	/**
	 * @param service3 the service3 to set
	 */
	public void setService3(ServiceTemplate service3) {
		this.service3 = service3;
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
	 * @return the service2Value
	 */
	public String getService2Value() {
		return service2Value;
	}


	/**
	 * @param service1Value the service1Value to set
	 */
	public void setService1Value(String service1Value) {
		this.service1Value = service1Value;
	}



 



}
