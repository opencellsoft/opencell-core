package org.meveo.model.cpq;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "cpq_product_version_attributes")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_version_attribute_seq"), })
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
   

}
