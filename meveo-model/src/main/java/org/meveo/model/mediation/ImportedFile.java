package org.meveo.model.mediation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Imported File
 * 
 * @author Tyshan Shi
 *
 */

@Entity
@ExportIdentifier({ "code", "provider" })
@ObservableEntity
@Table(name = "MEDINA_IMPORTEDFILE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_IMPORTEDFILE_SEQ")
public class ImportedFile extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3069673614890906206L;
	@Column(name = "URI", length=2000)
	private String uri;
	@Column(name = "SIZE")
	private Long size;

	@Column(name = "LAST_MODIFIED")
	@Temporal(TemporalType.DATE)
	private Date lastModified;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
