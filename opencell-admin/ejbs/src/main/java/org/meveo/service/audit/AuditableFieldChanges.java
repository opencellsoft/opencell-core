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

package org.meveo.service.audit;

import org.meveo.model.BaseEntity;

import javax.enterprise.context.RequestScoped;
import java.util.HashSet;
import java.util.Set;

/**
 * keep the dirtable entities for being audited
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
@RequestScoped
public class AuditableFieldChanges {

    /**
     * the changed entities
     */
    private Set<BaseEntity> changedEntities = new HashSet<>();

    /**
     * Check if a transaction is in progress
     */
    private boolean hasTransactionInProgress = false;

    /**
     * Adding the changed entity
     *
     * @param baseEntity the entity
     */
    public void addChange(BaseEntity baseEntity) {
        changedEntities.add(baseEntity);
    }

    /**
     * Gets the changed entities
     *
     * @return the changed entities
     */
    public Set<BaseEntity> getChangedEntities() {
        return changedEntities;
    }

    /**
     * Gets true if does a transaction in progress
     *
     * @return true if does a transaction in progress
     */
    public boolean isHasTransactionInProgress() {
        return hasTransactionInProgress;
    }

    /**
     * Sets true if does a transaction in progress.
     *
     * @param hasTransactionInProgress true if does a transaction in progress
     */
    public void setHasTransactionInProgress(boolean hasTransactionInProgress) {
        this.hasTransactionInProgress = hasTransactionInProgress;
    }

    public boolean isEmpty() {
        return changedEntities.isEmpty();
    }

    public void clear() {
        changedEntities.clear();
    }
}
