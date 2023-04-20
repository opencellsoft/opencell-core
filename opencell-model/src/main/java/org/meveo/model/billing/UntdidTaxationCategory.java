package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;

@CustomFieldEntity(cftCodePrefix = "UntdidTaxationCategory")
@Entity
@Table(name = "untdid_5305_taxation_category")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_5305_taxation_category_seq"), })

public class UntdidTaxationCategory extends AuditableEntity { 
	
	private static final long serialVersionUID = 1L;

	@Column(name = "code", length = 255)
	private String code;
	
    @Column(name = "name", length = 500)
    @Size(max = 20)
    private String name;
  
    @Column(name = "semantic_model", length = 500)
    @Size(max = 20)
    private String semanticModel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSemanticModel() {
		return semanticModel;
	}

	public void setSemanticModel(String semanticModel) {
		this.semanticModel = semanticModel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
