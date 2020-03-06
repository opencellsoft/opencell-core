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
package org.meveo.model.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.meveo.model.AccountEntity;

public class AccountCodeGenerationListener {

    @PrePersist
    public void prePersist(AccountEntity account) {
        //TODO : remove this code for two reasons
        // 1 - the identifier is not created yet
        // 2 - to have a custom code, there is an "adm_custom_generic_entity_code" table which must be added
        if (account.getCode() == null) {
            account.setCode("A" + account.getId());
        }
        if (account.getCode() != null && account.isAppendGeneratedCode()) {
            account.setCode(account.getCode() + "_" + "A" + account.getId());
        }
    }

    @PreUpdate
    public void preUpdate(AccountEntity account) {
        if (account.getCode() == null) {
            account.setCode("A" + account.getId());
        }
        if (account.getCode() != null && account.isAppendGeneratedCode()) {
            account.setCode(account.getCode() + "_" + "A" + account.getId());
        }
    }

}
