package org.meveo.model.cpq;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;

/**
 * Class entity product line for CPQ module which describe family of the product
 * @author Tarik F.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_product_line", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_line_seq"), })
public class ProductLine extends BusinessEntity {

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
	
	/**
	 * long description
	 */
	@Column(name = "long_description")
	@Lob
	private String longDescription;
	
	/**
	 * if this family has a parent line then it will be considerate  as child<br/>
	 * Attention : this child must have the same seller as its parent 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_line")
	private ProductLine parentLine;

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public ProductLine getParentLine() {
		return parentLine;
	}

	public void setParentLine(ProductLine parentLine) {
		this.parentLine = parentLine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(longDescription, parentLine, seller);
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
		ProductLine other = (ProductLine) obj;
		return Objects.equals(longDescription, other.longDescription) && Objects.equals(parentLine, other.parentLine)
				&& Objects.equals(seller, other.seller);
	}
}
