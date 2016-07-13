package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 */
@Entity
@Table(name = "CAT_DIGITAL_RESOURCE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DIGITAL_RESOURCE_SEQ")
public class DigitalResource extends BaseEntity {

	private static final long serialVersionUID = -7528761006943581984L;

	@Column(name = "URI", length = 255)
	private String uri;

	@Column(name = "MIME_TYPE", length = 50)
	@Size(max = 50)
	private String mimeType;

	@ManyToOne
	@JoinColumn(name = "PRODUCT_OFFERING_ID")
	private ProductOffering productOffering;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public ProductOffering getProductOffering() {
		return productOffering;
	}

	public void setProductOffering(ProductOffering productOffering) {
		this.productOffering = productOffering;
	}

}
