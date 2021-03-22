package org.meveo.model.cpq.offer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 */
@Entity
@Table(name = "cpq_offer_component")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_offer_component_seq"), })
@NamedQuery(name = "OfferComponent.findByOfferTEmplateAndProduct", query = "select o from OfferComponent o left join o.offerTemplate ot left join o.product op where ot.code=:offerCode  and op.code=:productCode")
public class OfferComponent extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7201295614290975063L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", nullable = false)
	@NotNull
	private OfferTemplate offerTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;
	

    @Column(name = "quantity_min")
	private Integer quantityMin = 0;

    @Column(name = "quantity_max")
	private Integer quantityMax = 0;

    @Column(name = "quantity_default")
	private Integer quantityDefault = 0;

	/**
	 *  list of tag associated to offer component
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "cpq_offer_component_tags",
			joinColumns = @JoinColumn(name = "offer_component_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")				
		)
	private Set<Tag> tagsList = new HashSet<>();
	

	/**
	* Mandatory
	*/
	@Type(type = "numeric_boolean")
	@Column(name = "mandatory")
	private boolean mandatory=Boolean.FALSE;
    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "sequence", columnDefinition = "int DEFAULT 0")
    private Integer sequence = 0;

    /*
     * display 
     */
    @Type(type = "numeric_boolean")
    @Column(name = "display")
    protected boolean display = Boolean.TRUE;

	public OfferComponent() {
		
	}
	
	public OfferComponent(OfferComponent copy) {
		this.product = copy.getProduct();
		this.offerTemplate = copy.offerTemplate;
		this.tagsList = new HashSet<>(copy.getTagsList());
		this.quantityMin = copy.quantityMin;
		this.quantityMax = copy.quantityMax;
		this.quantityDefault = copy.quantityDefault;
	}


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
	 * @return the offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	/**
	 * @param offerTemplate the offerTemplate to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(offerTemplate, product, tagsList);
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
		OfferComponent other = (OfferComponent) obj;
		return Objects.equals(offerTemplate, other.offerTemplate) && Objects.equals(product, other.product)
				&& Objects.equals(tagsList, other.tagsList);
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the quantityMin
	 */
	public Integer getQuantityMin() {
		return quantityMin;
	}

	/**
	 * @param quantityMin the quantityMin to set
	 */
	public void setQuantityMin(Integer quantityMin) {
		this.quantityMin = quantityMin;
	}

	/**
	 * @return the quantityMax
	 */
	public Integer getQuantityMax() {
		return quantityMax;
	}

	/**
	 * @param quantityMax the quantityMax to set
	 */
	public void setQuantityMax(Integer quantityMax) {
		this.quantityMax = quantityMax;
	}

	/**
	 * @return the quantityDefault
	 */
	public Integer getQuantityDefault() {
		return quantityDefault;
	}

	/**
	 * @param quantityDefault the quantityDefault to set
	 */
	public void setQuantityDefault(Integer quantityDefault) {
		this.quantityDefault = quantityDefault;
	}

	
}
