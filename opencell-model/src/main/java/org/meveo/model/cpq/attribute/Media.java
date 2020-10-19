package org.meveo.model.cpq.attribute;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.MediaTypeEnum;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_media", uniqueConstraints = @UniqueConstraint(columnNames = { "product_code", "media_name" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_media_seq"), })
public class Media extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * product code
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
	@NotNull
	private Product product;
	
	/**
	 * name of the attribute if the product is associated to attribute
	 */
	@Column(name = "attribute_name", length = 20)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_attribute_id", referencedColumnName = "id" )
	private ProductAttribute productAttribute;

	/**
	 * short name of the media
	 */
	@Column(name = "media_name", length = 20, nullable = false)
	@Size(max = 20)
	@NotNull
	private String mediaName;
	
	/**
	 * label : short description for this type of attribute
	 */
	@Column(name = "label", length = 255, nullable = false)
	@NotNull
	private String label;
	
	/**
	 * flag indicate if the media is for current product or attribute of the product 
	 */
	@Column(name = "main", nullable = false)
	@NotNull
	private Boolean main;
	
	/**
	 * type of the media : 2 options available (image / video)
	 */
	@Column(name = "media_type", nullable = false)
	@NotNull
	@Enumerated(EnumType.ORDINAL)
	private MediaTypeEnum mediaType;
	
	/**
	 * media path : current location for the media 
	 */
	@Column(name = "media_path", length = 255)
	@Size(max = 255)
	private String mediaPath;

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
	 * @return the productAttribute
	 */
	public ProductAttribute getProductAttribute() {
		return productAttribute;
	}

	/**
	 * @param productAttribute the productAttribute to set
	 */
	public void setProductAttribute(ProductAttribute productAttribute) {
		this.productAttribute = productAttribute;
	}

	/**
	 * @return the mediaName
	 */
	public String getMediaName() {
		return mediaName;
	}

	/**
	 * @param mediaName the mediaName to set
	 */
	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the main
	 */
	public Boolean getMain() {
		return main;
	}

	/**
	 * @param main the main to set
	 */
	public void setMain(Boolean main) {
		this.main = main;
	}

	/**
	 * @return the mediaType
	 */
	public MediaTypeEnum getMediaType() {
		return mediaType;
	}

	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(MediaTypeEnum mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * @return the mediaPath
	 */
	public String getMediaPath() {
		return mediaPath;
	}

	/**
	 * @param mediaPath the mediaPath to set
	 */
	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(label, main, mediaName, mediaPath, mediaType, product, productAttribute);
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
		Media other = (Media) obj;
		return Objects.equals(label, other.label) && Objects.equals(main, other.main)
				&& Objects.equals(mediaName, other.mediaName) && Objects.equals(mediaPath, other.mediaPath)
				&& mediaType == other.mediaType && Objects.equals(product, other.product)
				&& Objects.equals(productAttribute, other.productAttribute);
	}
	
	

	
}
