package org.meveo.admin.wf;

import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BusinessEntity;

/**
 * @author phung
 *
 * @param <E> entity
 */
public interface IWorkflowType<E extends BusinessEntity> {

    /**
     * Get a list of statuses for the workflow.
     * @return list of status.
     */
    List<String> getStatusList();

    /**
     * Change status on a current entity.
     * 
     * @param newStatus New status
     * @throws BusinessException business exception.
     */
    void changeStatus(String newStatus) throws BusinessException;

    /**
     * Get current status of current entity.
     * @return actual status.
     */
    String getActualStatus();

    /**
     * Get current entity.
     * 
     * @return Current entity
     */
    E getEntity();

    /**
     * Update current entity
     * 
     * @param entity Entity
     */
    public void setEntity(E entity);
}
