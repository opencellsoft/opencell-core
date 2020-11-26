package org.meveo.model.cpq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.crm.CustomerBrand;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_product", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_seq"), })
@NamedQuery(name = "Product.getProductLine", query = "select p from Product p where p.productLine.id=:id")
@NamedQuery(name = "Product.findByCode", query = "select p from Product p where p.code=:code")
@NamedQuery(name = "Product.findByTags", query = "select p from Product p LEFT JOIN p.tags as tag WHERE tag.code IN (:tagCodes)")
public class Product extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * status of product type of {@link ProductStatusEnum}
	 */
	@Column(name = "status", nullable = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	private ProductStatusEnum status;
	
	/**
	 * status date : modified automatically when the status change
	 */
	@Column(name = "status_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date statusDate;
	
	
	/**
	 * family of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_line_id", referencedColumnName = "id")
	private ProductLine productLine;
	
	/**
	 * brand of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_brand_id", referencedColumnName = "id")
	private CustomerBrand brand;
	
	/**
	 * reference: unique for product if it has a reference
	 */
	@Column(name = "reference", length = 50)
	@Size(max = 50)
	private String reference;
	
	/**
	 * model : it is an upgrade for the product
	 */
	@Column(name = "model", length = 50)
	@Size(max = 20)
	private String model;
	
	/**
	 * model children : display all older model 
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "model_chlidren")
	@CollectionTable(name = "cpq_product_model_children", joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
	private Set<String> modelChlidren=new HashSet<String>();
	

	/**
	 * list of discount attached to this product
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
				name = "cpq_product_discount_plan",
				joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "discount_id", referencedColumnName = "id")				
			)
	private Set<DiscountPlan> discountList = new HashSet<>();
	
	
	/**
	 * flag that indicate if true  discount list will have a specific 
	 * list otherwise all available discount attached to this product will be displayed
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "discount_flag", nullable = false)
	@NotNull
	private boolean discountFlag;
	
	
    /**
     * indicates whether or not the product detail should be displayed in the quote.
     */
	@Type(type = "numeric_boolean")
    @Column(name = "package_flag", nullable = false)
    @NotNull
    private boolean packageFlag;
    
	
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ProductVersion> productVersions = new ArrayList<>();
    
    
    
    
    /**
     * offer component
     */  
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true) 
    private List<OfferComponent> offerComponents = new ArrayList<>();



	/**
	 * @return the status
	 */
	public ProductStatusEnum getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(ProductStatusEnum status) {
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
	 * @return the productLine
	 */
	public ProductLine getProductLine() {
		return productLine;
	}


	/**
	 * @param productLine the productLine to set
	 */
	public void setProductLine(ProductLine productLine) {
		this.productLine = productLine;
	}


	/**
	 * @return the brand
	 */
	public CustomerBrand getBrand() {
		return brand;
	}


	/**
	 * @param brand the brand to set
	 */
	public void setBrand(CustomerBrand brand) {
		this.brand = brand;
	}


	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}


	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}


	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}


	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}


	/**
	 * @return the modelChlidren
	 */
	public Set<String> getModelChlidren() {
		return modelChlidren;
	}


	/**
	 * @param modelChlidren the modelChlidren to set
	 */
	public void setModelChlidren(Set<String> modelChlidren) {
		this.modelChlidren = modelChlidren;
	}


	/**
	 * @return the discountList
	 */
	public Set<DiscountPlan> getDiscountList() {
		return discountList;
	}


	/**
	 * @param discountList the discountList to set
	 */
	public void setDiscountList(Set<DiscountPlan> discountList) {
		this.discountList = discountList;
	}


	/**
	 * @return the discountFlag
	 */
	public boolean isDiscountFlag() {
		return discountFlag;
	}


	/**
	 * @param discountFlag the discountFlag to set
	 */
	public void setDiscountFlag(boolean discountFlag) {
		this.discountFlag = discountFlag;
	}

	

	/**
	 * @return the packageFlag
	 */
	public boolean isPackageFlag() {
		return packageFlag;
	}


	/**
	 * @param packageFlag the packageFlag to set
	 */
	public void setPackageFlag(boolean packageFlag) {
		this.packageFlag = packageFlag;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(brand, discountFlag, discountList, model, modelChlidren, productLine,
				reference, status, statusDate);
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
		
		Product other = (Product) obj;
		 if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
	            return true;
	        }
		return Objects.equals(brand, other.brand) && discountFlag == other.discountFlag
				&& Objects.equals(discountList, other.discountList) && Objects.equals(model, other.model)
				&& Objects.equals(modelChlidren, other.modelChlidren) && Objects.equals(productLine, other.productLine)
				&& Objects.equals(reference, other.reference) && status == other.status
				&& Objects.equals(statusDate, other.statusDate)&& Objects.equals(packageFlag, other.packageFlag);
	}


	/**
	 * @return the productVersions
	 */
	public List<ProductVersion> getProductVersions() {
		return productVersions;
	}


	/**
	 * @param productVersions the productVersions to set
	 */
	public void setProductVersions(List<ProductVersion> productVersions) {
		this.productVersions = productVersions;
	}




	/**
	 * @return the offerComponents
	 */
	public List<OfferComponent> getOfferComponents() {
		return offerComponents;
	}


	/**
	 * @param offerComponents the offerComponents to set
	 */
	public void setOfferComponents(List<OfferComponent> offerComponents) {
		this.offerComponents = offerComponents;
	}


	
	
	




}
