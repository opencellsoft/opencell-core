package org.meveo.model.cpq;

import java.sql.Types;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.admin.Seller;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Class entity product line for CPQ module which describe family of the product
 * @author Tarik F.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "ProductLine")
@Table(name = "cpq_product_line", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_line_seq"), })
@NamedQueries({
	@NamedQuery(name = "ProductLine.findByCode", query = "select p from ProductLine p where p.code =:code")
})
public class ProductLine extends BusinessCFEntity  {

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
	@JdbcTypeCode(Types.LONGVARCHAR)
	@Column(name = "long_description")
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
