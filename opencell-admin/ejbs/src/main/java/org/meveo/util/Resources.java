/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class Resources {

    @PersistenceUnit(unitName = "MeveoAdmin")
    private EntityManagerFactory emf;

    @Produces
    @MeveoJpa
    @RequestScoped
    public EntityManager create() {
        return this.emf.createEntityManager();
    }

    public void dispose(@Disposes @MeveoJpa EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    // For some reason this causes issue in GUI with lazy loading:
    // @PersistenceContext(unitName = "MeveoAdmin")
    // private EntityManager em;
    //
    // @Produces
    // @RequestScoped
    // @MeveoJpa
    // public EntityManager getEntityManager() {
    // return em;
    // }
    // end of "For some reason this causes issue in GUI with lazy loading"

    //
    // @Produces
    // @PersistenceContext(unitName = "MeveoAdmin")
    // @MeveoJpaForJobs
    // private EntityManager emfForJobs;

}