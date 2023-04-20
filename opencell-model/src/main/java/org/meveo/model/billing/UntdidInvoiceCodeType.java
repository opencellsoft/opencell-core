package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@Table(name = "untdid_1001_invoice_code_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_1001_invoice_code_type_seq"), })
public class UntdidInvoiceCodeType extends AuditableEntity {
	
	private static final long serialVersionUID = 7294273360931646550L;

	@Column(name = "code", length = 10)
	private String code;
	
	@Column(name = "en16931_interpretation", length = 20)
	private String interpretation16931;
	  
	@Column(name = "name", length = 500)
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}	
}
