package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@Table(name = "untdid_unit")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_unit_seq"), })
public class UntdidUnit extends AuditableEntity {
	
	@Column(name = "code", length = 5)
	@Size(max = 10)
	private String code;
	  
	@Column(name = "name", length = 500)
	@Size(max = 500)
	private String name;
	
	@Column(name = "source", length = 500)
	@Size(max = 10)
	private String source;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
