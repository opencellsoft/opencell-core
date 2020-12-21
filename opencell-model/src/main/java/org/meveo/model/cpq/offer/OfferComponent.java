package org.meveo.model.cpq.offer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
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

	public OfferComponent() {
		
	}
	
	public OfferComponent(OfferComponent copy) {
		this.offerTemplate = copy.getCommercialOffer();
		this.product = copy.getProduct();
		this.tagsList = new HashSet<>(copy.getTagsList());
	}
	/**
	 * @return the commercialOffer
	 */
	public OfferTemplate getCommercialOffer() {
		return offerTemplate;
	}


	/**
	 * @param commercialOffer the commercialOffer to set
	 */
	public void setCommercialOffer(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
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

	
}
