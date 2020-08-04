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

package org.meveo.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * A wrapper for Entity manager injection and its manipulation in application manager persistence context. Only one level of depth is supported. That is, can not nest two
 * interceptors.
 * 
 * @author Andrius Karpavicius
 * @since 5.1
 */
public class EntityManagerWrapper implements Serializable {

    private static final long serialVersionUID = 7592343627179864979L;

    /**
     * Current entity manager
     */
    private EntityManager entityManager;

    /**
     * Previous entity manager
     */
    private EntityManager previousEntityManager;

    /**
     * Is application manager persistence context
     */
    private boolean amp = false;

    /**
     * Entity manager wrapper
     */
    public EntityManagerWrapper() {

    }

    /**
     * Wrap an entity manager for injection
     * 
     * @param entityManager Entity manager
     * @param amp Is application manager persistence context
     */
    public EntityManagerWrapper(EntityManager entityManager, boolean amp) {
        this.entityManager = entityManager;
        this.amp = amp;
    }

    /**
     * @return Current entity manager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * @return Is persistence context manager by application
     */
    public boolean isAmp() {
        return amp;
    }

    /**
     * Use a new entity manager
     * 
     * @param newEntityManager New entity manager
     */
    public void newEntityManager(EntityManager newEntityManager) {
        if (!amp || previousEntityManager != null) {
            return;
        }

        previousEntityManager = entityManager;
        entityManager = newEntityManager;
    }

    /**
     * Return to use a previous entity manager
     */
    public void popEntityManager() {
        if (!amp || previousEntityManager == null) {
            return;
        }

        entityManager.close();
        entityManager = previousEntityManager;
    }

    /**
     * Terminate the lifecycle of Entity manager wrapper
     */
    public void dispose() {
        if (!amp) {
            return;
        }

        entityManager.close();
    }

    /**
     * Check if allowed to nest a new Entity manager
     * 
     * @return True if allowed to instantiate a new entity manager
     */
    public boolean isNestingAllowed() {
        return previousEntityManager == null;
    }
}