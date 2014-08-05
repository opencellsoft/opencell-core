/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

	// @ExtensionManaged
	// @Produces
	// @PersistenceUnit(unitName = "MeveoAdmin")
	// @MeveoJpaForJobs
	// private EntityManagerFactory emfForJobs;
	@Produces
	@PersistenceContext(unitName = "MeveoAdmin")
	@MeveoJpaForJobs
	private EntityManager emfForJobs;

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
