package org.meveo.model.mediation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
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
public class ImportedFile extends BusinessEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3069673614890906206L;
	@Column(name = "URI", length=2000)
	@Size(max=2000,min=1)
	private String uri;
	@Column(name = "SIZE")
	private Long size;

	@Column(name = "LAST_MODIFIED")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModified;
	
	private static Calendar calendar=null;
	static{
		calendar=Calendar.getInstance();
	}
	public ImportedFile(){}
	public ImportedFile(String uri,Long size){
		this.uri=uri;
		this.size=size;
	}
	public ImportedFile(String uri,Long size,Long lastModified){
		this.uri=uri;
		this.size=size;
		calendar.setTimeInMillis(lastModified);
		this.lastModified=calendar.getTime();
	}
	public String getOriginHash(){
		return String.format("%s:%d:%d", this.uri,this.size,this.lastModified.getTime());
	}
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
	
	@Override
	public String toString() {
		return "ImportedFile [uri=" + uri + ", size=" + size + ", lastModified=" + lastModified +"]";
	}

}
