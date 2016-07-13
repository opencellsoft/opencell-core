package org.meveo.model.catalog.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_DIGITAL_RESOURCE", uniqueConstraints = { @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DIGITAL_RESOURCE_SEQ")
public class DigitalResource extends BaseEntity {

	private static final long serialVersionUID = -7528761006943581984L;

	@Column(name = "URI", length = 255)
	private String uri;

	@Column(name = "MIME_TYPE", length = 50)
	@Size(max = 50)
	private String mimeType;

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

}
