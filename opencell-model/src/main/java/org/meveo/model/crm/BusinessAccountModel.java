package org.meveo.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "crm_business_account_model")
public class BusinessAccountModel extends MeveoModule {

    private static final long serialVersionUID = 8664266331861722097L;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "hierarchy_type", length = 20)
    private AccountHierarchyTypeEnum hierarchyType;

	public AccountHierarchyTypeEnum getHierarchyType() {
		return hierarchyType;
	}

	public void setHierarchyType(AccountHierarchyTypeEnum hierarchyType) {
		this.hierarchyType = hierarchyType;
	}

}