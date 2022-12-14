package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@Table(name = "untdid_4451_invoice_subject_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_5189_allowance_code_seq"), })
public class invoiceSubject {
	
	@Column(name = "code_name", length = 500)
	@Size(max = 20)
	private String codeName;
	  
	@Column(name = "usage_in_EN16931", length = 500)
	@Size(max = 20)
	private String usageEN16931;

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getUsageEN16931() {
		return usageEN16931;
	}

	public void setUsage_in_EN16931(String usageEN16931) {
		this.usageEN16931 = usageEN16931;
	}
	
}
