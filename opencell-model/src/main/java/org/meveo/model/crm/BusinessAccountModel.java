/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.crm;

import org.meveo.model.module.MeveoModule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Business account model used for account creation customization
 * 
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "crm_business_account_model")
public class BusinessAccountModel extends MeveoModule {

    private static final long serialVersionUID = 8664266331861722097L;

    /**
     * Account hierarchy levels to create
     */
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