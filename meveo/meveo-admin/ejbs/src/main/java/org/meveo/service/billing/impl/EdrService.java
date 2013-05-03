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
package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
@LocalBean
public class EdrService extends PersistenceService<EDR>  {

	@SuppressWarnings("unchecked")
	public List<EDR> getEDRToRate() {
		Query query=em.createQuery("from EDR e where e.status=:status").setParameter("status", EDRStatusEnum.OPEN);
		return query.getResultList();
	}

	public EDR findByBatchAndRecordId(String originBatch,String originRecord) {
		EDR result=null;
		try{
			Query query=em.createQuery("from EDR e where e.originBatch=:originBatch and e.originRecord=:originRecord")
					.setParameter("originBatch", originBatch)
					.setParameter("originRecord", originRecord);
			result = (EDR) query.getSingleResult();
		}catch(Exception e){}
		return result;
	}

}
