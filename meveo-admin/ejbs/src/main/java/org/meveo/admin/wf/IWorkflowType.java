package org.meveo.admin.wf;

import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.User;

public interface IWorkflowType<E extends BaseEntity> {

    /**
     * Get a list of statuses for the workflow
     */
    public List<String> getStatusList();

    /**
     * Change status on a current entity
     * 
     * @param newStatus New status
     * @param currentUser User performing status change
     */
    public void changeStatus(String newStatus, User currentUser) throws BusinessException;

    /**
     * Get current status of current entity
     */
    public String getActualStatus();

    /**
     * Get current entity
     * 
     * @return Current entity
     */
    public E getEntity();

    /**
     * Update current entity
     * 
     * @param entity Entity
     */
    public void setEntity(E entity);
}
