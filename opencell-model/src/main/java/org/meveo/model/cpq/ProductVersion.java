package org.meveo.model.cpq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.tags.Tag;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay.
 * @version 10.0
 */
@Entity
@Table(name = "cpq_product_version",uniqueConstraints = @UniqueConstraint(columnNames = { "product_id", "current_version" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_version_seq"), })
@NamedQueries({ 
	@NamedQuery(name = "ProductVersion.findByProductAndVersion", query = "SELECT pv FROM ProductVersion pv left join pv.product where pv.product.code=:productCode and pv.currentVersion=:currentVersion"),
	@NamedQuery(name = "ProductVersion.findByTags", query = "select p from ProductVersion p LEFT JOIN p.tags as tag WHERE p.status='PUBLISHED' and tag.code IN (:tagCodes)"),
	@NamedQuery(name = "ProductVersion.getProductVerionsByStatusAndProduct", query = "SELECT pv FROM ProductVersion pv  left join pv.product as p where pv.status=:status and p.code=:productCode"),
	@NamedQuery(name = "ProductVersion.findTagsByTagType", query = "select tag from ProductVersion p LEFT JOIN p.tags as tag left join tag.tagType tp where tp.code IN (:tagTypeCodes)") 
})
public class ProductVersion extends AuditableEntity{


	private static final long serialVersionUID = 1L;
	
	/**
     * Record/entity identifier
     */
   /* @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    @JsonProperty
    protected Long id;*/
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private Product product;
    
    /**
     * version of the product<br />
     * this value is auto increment, do not use its method setVersion
     */
    @Column(name = "current_version", nullable = false)
    @Min(1)
    private int currentVersion;
    
    /**
     * status . it can be DRAFT / PUBLIED / CLOSED  
     */
    @Column(name = "status", nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private VersionStatusEnum status;
    
    /**
     * date of status : it set automatically when ever the status of product is changed
     */
    @Column(name = "status_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull    
    private Date statusDate;
    
    /**
     * short description. must not be null
     */
    @Column(name = "short_description", nullable = true, length = 255)
    @Size(max = 255)
    @NotNull
    private String shortDescription;
    
    /**
     * long description
     */
    @Size(max = 2000)
    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;
    
    /**
     * validity dates
     */
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod validity = new DatePeriod();
     
    
    /**
     * list of tag attached
     */   
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_product_version_tags", joinColumns = @JoinColumn(name = "product_version_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<Tag>();
    

	/**
	 * list of attributes attached to this product version
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
				name = "cpq_product_version_attributes",
				joinColumns = @JoinColumn(name = "product_version_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id")				
			)
    private List<Attribute> attributes = new ArrayList<Attribute>();
	
	

	/**
	 * list of grouped attribute attached to this product version
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
				name = "cpq_product_version_grouped_attributes",
				joinColumns = @JoinColumn(name = "product_version_id", referencedColumnName = "id"),
				inverseJoinColumns = @JoinColumn(name = "grouped_attributes_id", referencedColumnName = "id")				
			)
    private List<GroupedAttributes> groupedAttributes = new ArrayList<GroupedAttributes>();
    
    
	public ProductVersion() {}
	
	public ProductVersion(ProductVersion copy, Product product) {
		this.setId(null);
		this.setStatus(VersionStatusEnum.DRAFT);
		this.setCurrentVersion(1);
		this.setStatusDate(Calendar.getInstance().getTime());
		if(product != null)
			this.setProduct(product);
		else
			this.setProduct(copy.getProduct());
		this.setTags(new HashSet<>());
		this.setAttributes(new ArrayList<>());
		this.setShortDescription(copy.getShortDescription());
		this.setValidity(copy.getValidity()); 
	}

	public void setId(Long id) {
		this.id = id;
	}


	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public VersionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(VersionStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	 

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}



	/**
	 * @return the attributes
	 */
	public List<Attribute> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the tags
	 */
	public Set<Tag> getTags() {
		return tags;
	}


	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	 

	/**
	 * @return the groupedAttributes
	 */
	public List<GroupedAttributes> getGroupedAttributes() {
		return groupedAttributes;
	}

	/**
	 * @param groupedAttributes the groupedAttributes to set
	 */
	public void setGroupedAttributes(List<GroupedAttributes> groupedAttributes) {
		this.groupedAttributes = groupedAttributes;
	}
	
	

	/**
	 * @return the validity
	 */
	public DatePeriod getValidity() {
		return validity;
	}

	/**
	 * @param validity the validity to set
	 */
	public void setValidity(DatePeriod validity) {
		this.validity = validity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, longDescription, product, shortDescription, validity, status, statusDate,
				tags, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductVersion other = (ProductVersion) obj;
		return Objects.equals(validity, other.validity) && Objects.equals(id, other.id)
				&& Objects.equals(longDescription, other.longDescription) && Objects.equals(product, other.product)
				&& Objects.equals(shortDescription, other.shortDescription) && status == other.status
				&& Objects.equals(statusDate, other.statusDate) && Objects.equals(tags, other.tags)
				&& version == other.version;
	}

	
	
}
