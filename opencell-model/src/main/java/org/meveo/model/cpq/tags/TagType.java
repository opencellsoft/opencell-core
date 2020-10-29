package org.meveo.model.cpq.tags;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
/**
 * 
 * @author Tarik F.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_tag_type", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_tag_type_seq"), })
@NamedNativeQuery(name = "TagType.findByCode", query = "select t from TagType t where t.code=:code")
public class TagType extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * seller associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;


	public Seller getSeller() {
		return seller;
	}


	public void setSeller(Seller seller) {
		this.seller = seller;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(seller);
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
		TagType other = (TagType) obj;
		return Objects.equals(seller, other.seller);
	}
	
	
	
	

	
	
	

}
