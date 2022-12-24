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
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@Table(name = "untdid_vatex")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_vatex_seq"), })
public class UntdidInvoiceCodeType extends BaseEntity{
	
	@Column(name = "en16931_interpretation", length = 20)
	@Size(max = 20)
	private String interpretation16931;
	  
	@Column(name = "name", length = 500)
	@Size(max = 20)
	private String name;

	public String getInterpretation16931() {
		return interpretation16931;
	}

	public void setInterpretation16931(String interpretation16931) {
		this.interpretation16931 = interpretation16931;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

}
