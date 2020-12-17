package org.meveo.model.cpq.tags;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.crm.Customer;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 */
@Entity
@Table(name = "cpq_tag", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_tag_seq"), })
@NamedQueries({
		@NamedQuery(name = "Tag.findByTagType", query = "select t from Tag t where t.tagType.id=:id"),
		@NamedQuery(name = "Tag.findByCode", query = "select t from Tag t where t.code.id=:code"),
		@NamedQuery(name = "Tag.findByRequestedTagType", query = "select tag from ProductVersion p LEFT JOIN p.tags as tag left join tag.tagType tp where tp.code IN (:requestedTagType)") 
})
public class Tag extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * seller associated to the entity
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "seller_id")
	private Seller seller;
	
	
	/**
	 * translate the code of the tag to different language
	 */
	@Column(name = "name", length = 20, nullable = false)
	@Size(max = 20)
	@NotNull
	private String name;
	
	/**
	 * type of the tag, the seller of the type must be the same seller of the current tag
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tag_type_id", nullable = false)
	@NotNull
	private TagType tagType;
	
	/**
	 * link to parent tag, the seller of the parent must be the same as the current tag
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_tag_id")
	private Tag parentTag;
	
	/**
	 * the expression of language used on this entity are from {@link Customer} and {@link Subscription}
	 * 
	 */
	@Size(max = 2000)
    @Column(name = "filter_el", columnDefinition = "TEXT") 
	private String filterEl;
 
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TagType getTagType() {
		return tagType;
	}

	public void setTagType(TagType tagType) {
		this.tagType = tagType;
	}

	public Tag getParentTag() {
		return parentTag;
	}

	public void setParentTag(Tag parentTag) {
		this.parentTag = parentTag;
	}

	public String getFilterEl() {
		return filterEl;
	}

	public void setFilterEl(String filterEl) {
		this.filterEl = filterEl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(filterEl, name, parentTag, seller, tagType);
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
		Tag other = (Tag) obj;
		return Objects.equals(filterEl, other.filterEl) && Objects.equals(name, other.name)
				&& Objects.equals(parentTag, other.parentTag) && Objects.equals(seller, other.seller)
				&& Objects.equals(tagType, other.tagType);
	}

	/**
	 * @return the seller
	 */
	public Seller getSeller() {
		return seller;
	}

	/**
	 * @param seller the seller to set
	 */
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
 
 
	
}
