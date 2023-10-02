package org.meveo.model.cpq;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "cpq_product_version_attributes")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_version_attribute_seq"), })
@NamedQueries({
		@NamedQuery(name = "ProductVersionAttribute.findByAttributeAndProductVersion", query = "FROM ProductVersionAttribute pva where pva.attribute.id =:attributeId and pva.productVersion.id =:productVersionId")
})
public class ProductVersionAttribute extends AttributeBaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -5934892816847168643L;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "product_version_id", nullable = false)
   @NotNull
   private ProductVersion productVersion;

   public ProductVersionAttribute(){
	   super();
   }
   
   
   public ProductVersionAttribute(ProductVersionAttribute copy,ProductVersion productVersion) {
	   super(copy);
	   this.productVersion = productVersion;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProductVersionAttribute)) return false;
		if (!super.equals(o)) return false;
		ProductVersionAttribute that = (ProductVersionAttribute) o;
		return isMandatory() == that.isMandatory() && isDisplay() == that.isDisplay() && Objects.equals(getProductVersion(), that.getProductVersion()) && Objects.equals(getSequence(), that.getSequence()) && Objects.equals(getAttribute(), that.getAttribute()) && Objects.equals(getMandatoryWithEl(), that.getMandatoryWithEl()) && Objects.equals(getReadOnly(), that.getReadOnly()) && Objects.equals(getDefaultValue(), that.getDefaultValue()) && getValidationType() == that.getValidationType() && Objects.equals(getValidationPattern(), that.getValidationPattern()) && Objects.equals(getValidationLabel(), that.getValidationLabel());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getProductVersion(), getSequence(), getAttribute(), getMandatoryWithEl(), isMandatory(), isDisplay(), getReadOnly(), getDefaultValue(), getValidationType(), getValidationPattern(), getValidationLabel());
	}
}
