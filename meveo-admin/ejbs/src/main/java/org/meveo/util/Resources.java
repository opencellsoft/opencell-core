/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.util;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.jboss.solder.core.ExtensionManaged;

public class Resources {

    @ExtensionManaged
    @ConversationScoped
    @Produces
    @PersistenceUnit(unitName = "MeveoAdmin")
    @MeveoJpa
    private EntityManagerFactory emf;

     //@ExtensionManaged
     //@Produces
     //@PersistenceUnit(unitName = "MeveoAdmin")
     //@MeveoJpaForJobs
     //private EntityManagerFactory emfForJobs;
    @Produces
    @PersistenceContext(unitName = "MeveoAdmin")
    @MeveoJpaForJobs
    private EntityManager emfForJobs;


    @Produces
//    @PersistenceContext(unitName = "MeveoAdminTarget")
    @PersistenceContext(unitName = "MeveoAdmin")
    @MeveoJpaForTarget
    static EntityManager emfForTarget;

    /*
     * @ExtensionManaged
     *
     * @ConversationScoped
     *
     * @Produces
     *
     * @PersistenceUnit(unitName = "MeveoDWH")
     *
     * @MeveoDWHJpa private EntityManagerFactory emfDwh;
     */

    // @Produces
    // @MeveoJpa
    // @PersistenceContext(unitName = "MeveoAdmin", type =
    // PersistenceContextType.EXTENDED)
    // private EntityManager em;

    // @Produces
    // @MeveoDWHJpa
    // @PersistenceContext(unitName = "MeveoDWH", type =
    // PersistenceContextType.EXTENDED)
    // private EntityManager emDwh;

}
