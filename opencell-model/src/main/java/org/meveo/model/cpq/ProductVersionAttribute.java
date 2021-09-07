package org.meveo.model.cpq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

@Entity
@Table(name = "cpq_product_version_attributes")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_product_version_attributes_id_seq"), })
public class ProductVersionAttribute extends BaseEntity {    

	/**
	 *
	 */
	private static final long serialVersionUID = -5934892816847168643L;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "product_version_id", nullable = false)
   @NotNull
   private ProductVersion productVersion;
   /**
    * sequence for product version and attribute
    */
   @Column(name = "sequence")
   protected Integer sequence = 0;
   /**
    *
    */
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "attribute_id", nullable = false)
   @NotNull
   private Attribute attribute;
   public ProductVersionAttribute(){
   }
   public ProductVersionAttribute(ProductVersion productVersion, Attribute attribute, Integer sequence) {
       super();
       this.productVersion = productVersion;
       this.attribute = attribute;
       this.sequence = sequence;
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
   /**
    * @return the attribute
    */
   public Attribute getAttribute() {
       return attribute;
   }
   /**
    * @param attribute the attribute to set
    */
   public void setAttribute(Attribute attribute) {
       this.attribute = attribute;
   }

}
