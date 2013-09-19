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
package org.meveo.service.catalog.impl;

import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.model.admin.User;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.RatingService;

@Stateless @LocalBean
public class PricePlanMatrixService extends PersistenceService<PricePlanMatrix> {


	public void create(PricePlanMatrix e) {
		super.create(e);
		RatingService.setPricePlanDirty();
	}
	
	public void update(PricePlanMatrix e) {
		super.update(e);
		RatingService.setPricePlanDirty();
	}

	public void remove(Long id) {
		super.remove(id);
		RatingService.setPricePlanDirty();
	}

	public void disable(Long id) {
		super.disable(id);
		RatingService.setPricePlanDirty();
	}
	
	public void remove(PricePlanMatrix e) {
		super.remove(e);
		RatingService.setPricePlanDirty();
	}
	
	public void remove(Set<Long> ids) {
		super.remove(ids);
		RatingService.setPricePlanDirty();
	}
	
	public void update(PricePlanMatrix e, User updater) {
		super.update(e, updater);
		RatingService.setPricePlanDirty();
	}
	
	public void create(PricePlanMatrix e, User creator) {
		super.create(e, creator);
		RatingService.setPricePlanDirty();
	}

	public void create(PricePlanMatrix e, User creator, Provider provider) {
		super.create(e, creator, provider);
		RatingService.setPricePlanDirty();
	}
}
