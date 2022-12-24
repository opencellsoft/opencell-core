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
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;

@CustomFieldEntity(cftCodePrefix = "UntdidTaxationCategory")
@Entity
@Table(name = "untdid_5305_taxation_category")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_5305_taxation_category_seq"), })

public class UntdidTaxationCategory extends BaseEntity{
	
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
}
