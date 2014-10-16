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
package org.meveo.service.billing.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.BoundedHashMap;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
@LocalBean
public class EdrService extends PersistenceService<EDR> {

	ParamBean paramBean=ParamBean.getInstance();
	

	static boolean useInMemoryDeduplication=true;
	static int maxDuplicateRecords = 100000;
	static BoundedHashMap<String,Integer> duplicateCache;
	
	private void loadCacheFromDB(){
		synchronized(duplicateCache){
			loadDeduplicationInfo(maxDuplicateRecords);
		}
	}
	
	@PostConstruct
	private void init(){
		useInMemoryDeduplication=paramBean.getProperty("mediation.deduplicateInMemory", "true").equals("true");
		if(useInMemoryDeduplication){
			int newMaxDuplicateRecords=Integer.parseInt(paramBean.getProperty("mediation.deduplicateCacheSize", "100000"));
			if(newMaxDuplicateRecords!=maxDuplicateRecords && duplicateCache!=null){
				duplicateCache.setMaxSize(newMaxDuplicateRecords);
			}
			maxDuplicateRecords=newMaxDuplicateRecords;
			if(duplicateCache==null){
				duplicateCache= new BoundedHashMap<String, Integer>(newMaxDuplicateRecords);
				loadCacheFromDB();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<EDR> getEDRToRate() {
		Query query = getEntityManager().createQuery("from EDR e where e.status=:status")
				.setParameter("status", EDRStatusEnum.OPEN);
		return query.getResultList();
	}

	public EDR findByBatchAndRecordId(String originBatch, String originRecord) {
		EDR result = null;
		try {
			Query query = getEntityManager()
					.createQuery(
							"from EDR e where e.originBatch=:originBatch and e.originRecord=:originRecord")
					.setParameter("originBatch", originBatch)
					.setParameter("originRecord", originRecord);
			result = (EDR) query.getSingleResult();
		} catch (Exception e) {
		}
		return result;
	}

	public void loadDeduplicationInfo(
			int maxDuplicateRecords) {
		duplicateCache.clear();
		Query query = getEntityManager().createQuery("select CONCAT(e.originBatch,e.originRecord) from EDR e where e.status=:status ORDER BY e.eventDate DESC")
				.setParameter("status", EDRStatusEnum.OPEN)
				.setMaxResults(maxDuplicateRecords);
		@SuppressWarnings("unchecked")
		List<String> results=query.getResultList();
		for(String edrHash:results){
			duplicateCache.put(edrHash,0);
		}
	}

	public boolean duplicateFound(String originBatch, String originRecord) {
		boolean result = false;
		if(useInMemoryDeduplication){
			result = duplicateCache.containsKey(originBatch+originRecord);
		} else {
			result = findByBatchAndRecordId( originBatch,  originRecord) != null;
		}
		return result;
	}
	

	public void create(EDR e) {
		super.create(e);
		if(useInMemoryDeduplication){
			synchronized(duplicateCache){
				duplicateCache.put(e.getOriginBatch()+e.getOriginRecord(),0);
			}
		}
	}

}
