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
import org.meveo.model.article.AccountingArticle;
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
	 * Name of the first  product attribute.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_1",referencedColumnName = "id")
	private Attribute attribute1;
	
	
	
	/**
	 * value of the first product attribute.
	 */
	@Column(name = "attribute_1_value", length = 100)
	@Size(max = 100)
	private String attribute1Value;
	
	/**
	 * Name of the second product attribute.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_2",referencedColumnName = "id")
	private Attribute attribute2;
	
	
	/**
	 * value of the second product attribute.
	 */
	@Column(name = "attribute_2_value", length = 100)
	@Size(max = 100)
	private String attribute2Value;
	
	
	/**
	 * Name of the third product attribute.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_3",referencedColumnName = "id")
	private Attribute attribute3;
	
	
	/**
	 * value of the third product attribute.
	 */
	@Column(name = "attribute_3_value", length = 100)
	@Size(max = 100)
	private String attribute3Value;
	
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
	 * @return the attribute1Value
	 */
	public String getAttribute1Value() {
		return attribute1Value;
	}


 


	  

	/**
	 * @param attribute2Value the attribute2Value to set
	 */
	public void setAttribute2Value(String attribute2Value) {
		this.attribute2Value = attribute2Value;
	}


	 


	/**
	 * @return the attribute3Value
	 */
	public String getAttribute3Value() {
		return attribute3Value;
	}


	/**
	 * @param attribute3Value the attribute3Value to set
	 */
	public void setAttribute3Value(String attribute3Value) {
		this.attribute3Value = attribute3Value;
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
	 * @return the attribute1
	 */
	public Attribute getAttribute1() {
		return attribute1;
	}


	/**
	 * @param attribute1 the attribute1 to set
	 */
	public void setAttribute1(Attribute attribute1) {
		this.attribute1 = attribute1;
	}


	/**
	 * @return the attribute2
	 */
	public Attribute getAttribute2() {
		return attribute2;
	}


	/**
	 * @param attribute2 the attribute2 to set
	 */
	public void setAttribute2(Attribute attribute2) {
		this.attribute2 = attribute2;
	}


	/**
	 * @return the attribute3
	 */
	public Attribute getAttribute3() {
		return attribute3;
	}


	/**
	 * @param attribute3 the attribute3 to set
	 */
	public void setAttribute3(Attribute attribute3) {
		this.attribute3 = attribute3;
	}


	/**
	 * @return the attribute2Value
	 */
	public String getAttribute2Value() {
		return attribute2Value;
	}


	/**
	 * @param attribute1Value the attribute1Value to set
	 */
	public void setAttribute1Value(String attribute1Value) {
		this.attribute1Value = attribute1Value;
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
