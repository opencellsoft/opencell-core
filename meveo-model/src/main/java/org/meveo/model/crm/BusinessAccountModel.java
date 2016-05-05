package org.meveo.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.meveo.model.ObservableEntity;
import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ObservableEntity
@Table(name = "CRM_BUSINESS_ACCOUNT_MODEL")
public class BusinessAccountModel extends MeveoModule {

	private static final long serialVersionUID = 8664266331861722097L;

	@ManyToOne
	@JoinColumn(name = "SCRIPT_INSTANCE_ID")
	private AccountModelScript script;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", length = 20)
	private AccountHierarchyTypeEnum type;

	public AccountModelScript getScript() {
		return script;
	}

	public void setScript(AccountModelScript script) {
		this.script = script;
	}

	public AccountHierarchyTypeEnum getType() {
		return type;
	}

	public void setType(AccountHierarchyTypeEnum type) {
		this.type = type;
	}

}
