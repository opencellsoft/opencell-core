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
     * the dirtable entities
     */
    private Set<BaseEntity> dirtyableEntities = new HashSet<>();

    /**
     * Check if a transaction is in progress
     */
    private boolean hasTransactionInProgress = false;

    /**
     * Adding the dirtable entity
     *
     * @param baseEntity the entity
     */
    public void addChange(BaseEntity baseEntity) {
        dirtyableEntities.add(baseEntity);
    }

    /**
     * Gets the dirtyable entities
     *
     * @return the dirtyable entities
     */
    public Set<BaseEntity> getDirtyableEntities() {
        return dirtyableEntities;
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
        return dirtyableEntities.isEmpty();
    }

    public void clear() {
        dirtyableEntities.clear();
    }
}
