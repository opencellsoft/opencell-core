package org.meveo.admin.wf;

import org.meveo.model.BusinessEntity;

public abstract class WorkflowType<E extends BusinessEntity> implements IWorkflowType<E> {
    protected E entity;

    public WorkflowType() {
    }

    public WorkflowType(E e) {
        entity = e;
    }

    /**
     * Get current entity
     * 
     * @return Current entity
     */
    public E getEntity() {
        return entity;
    }

    /**
     * Update current entity
     * 
     * @param entity Entity
     */
    public void setEntity(E entity) {
        this.entity = entity;
    }
}