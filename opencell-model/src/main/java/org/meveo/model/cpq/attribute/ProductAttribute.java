package org.meveo.model.cpq.attribute;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.offer.CommercialOffer;
import org.meveo.model.cpq.tags.Tag;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_product_attribute", uniqueConstraints = @UniqueConstraint(columnNames = { "product_code", "attribute_name" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_attribute_seq"), })
public class ProductAttribute extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * product code associated to this attribute
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name =  "product_id", referencedColumnName = "id")
	private Product product;
	
	/**
	 * version of the product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name =  "product_version_id", nullable = false)
	private ProductVersion productVersion;
	

	/**
	 * commercial offer
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name =  "commercial_offer_id", referencedColumnName = "id")
	private CommercialOffer commercialOffer;
	
	/**
	 * attribute name : identifier for the attribute, must be unique for the same product
	 */
	@Column(name = "attribute_name", nullable = false, length = 20)
	@NotNull
	@Size(max = 20)
	private String attibuteName;
	
	/**
	 * Label : short description that explain the name of attribute
	 */
	@Column(name = "label", length = 255, nullable = false)
	@Size(max = 255)
	@NotNull
	private String lable;
	
	/**
	 * attribute type
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attribute_type_id", referencedColumnName = "id")
	private AttributeType attibuteType;
	
	/**
	 * mandatory : tell if the attribute is required 
	 */
	@Column(name = "mandatory")
	private boolean mandatory;

	/**
	 * priority : the higher the priority is the attribute must be display 
	 */
	@Column(name = "priority")
	private int priority;
	

	/**
	 * title
	 */
	@Column(name = "title", length = 50)
	@Size(max = 50)
	private String title;
	
	
	/**
	 * param : if the attribute is a list of value then param must store its information.
	 */
	@Column(name = "param")
	@Lob
	private String param;

	/**
	 *  list of tag associated to product
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(
			name = "cpq_product_tags",
			joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")				
		)
	private Set<Tag> tagsList = new HashSet<>();
	
	/**
	 * material : tell if the attribute is martial or not
	 */
	@Column(name = "material", nullable = false)
	@NotNull
	private Boolean material;

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
	 * @return the productVersion
	 */
	public ProductVersion getProductVersion() {
		return productVersion;
	}

	/**
	 * @param productVersion the productVersion to set
	 */
	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}

	/**
	 * @return the commercialOffer
	 */
	public CommercialOffer getCommercialOffer() {
		return commercialOffer;
	}

	/**
	 * @param commercialOffer the commercialOffer to set
	 */
	public void setCommercialOffer(CommercialOffer commercialOffer) {
		this.commercialOffer = commercialOffer;
	}

	/**
	 * @return the attibuteName
	 */
	public String getAttibuteName() {
		return attibuteName;
	}

	/**
	 * @param attibuteName the attibuteName to set
	 */
	public void setAttibuteName(String attibuteName) {
		this.attibuteName = attibuteName;
	}

	/**
	 * @return the lable
	 */
	public String getLable() {
		return lable;
	}

	/**
	 * @param lable the lable to set
	 */
	public void setLable(String lable) {
		this.lable = lable;
	}

	/**
	 * @return the attibuteType
	 */
	public AttributeType getAttibuteType() {
		return attibuteType;
	}

	/**
	 * @param attibuteType the attibuteType to set
	 */
	public void setAttibuteType(AttributeType attibuteType) {
		this.attibuteType = attibuteType;
	}

	/**
	 * @return the mandatory
	 */
	public boolean getMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the param
	 */
	public String getParam() {
		return param;
	}

	/**
	 * @param param the param to set
	 */
	public void setParam(String param) {
		this.param = param;
	}

	/**
	 * @return the tagsList
	 */
	public Set<Tag> getTagsList() {
		return tagsList;
	}

	/**
	 * @param tagsList the tagsList to set
	 */
	public void setTagsList(Set<Tag> tagsList) {
		this.tagsList = tagsList;
	}

	/**
	 * @return the material
	 */
	public Boolean getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(Boolean material) {
		this.material = material;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(attibuteName, attibuteType, commercialOffer, lable, material, param,
				priority, product, productVersion, tagsList, title);
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
		ProductAttribute other = (ProductAttribute) obj;
		return Objects.equals(attibuteName, other.attibuteName) && Objects.equals(attibuteType, other.attibuteType)
				&& Objects.equals(commercialOffer, other.commercialOffer) && Objects.equals(lable, other.lable)
				&& Objects.equals(material, other.material) && Objects.equals(param, other.param)
				&& priority == other.priority && Objects.equals(product, other.product)
				&& Objects.equals(productVersion, other.productVersion) && Objects.equals(tagsList, other.tagsList)
				&& Objects.equals(title, other.title);
	}

	
	
}
