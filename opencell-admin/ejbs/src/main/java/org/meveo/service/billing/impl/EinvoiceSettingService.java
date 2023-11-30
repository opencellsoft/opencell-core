package org.meveo.service.billing.impl;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.billing.ElectronicInvoiceSetting;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.job.JobInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Stateless
public class EinvoiceSettingService extends PersistenceService<ElectronicInvoiceSetting> {
	
	@Inject
	private JobInstanceService jobInstanceService;
	
	public ElectronicInvoiceSetting findEinvoiceSetting(){
		try {
			TypedQuery<ElectronicInvoiceSetting> query = getEntityManager().createQuery("from ElectronicInvoiceSetting g order by g.id desc", entityClass).setMaxResults(1);
			return query.getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} found", getEntityClass().getSimpleName());
			return null;
		}
	}
	public void chainToNextJob(ElectronicInvoiceSetting electronicInvoiceSetting) {
		JobInstance jobInstance = find(electronicInvoiceSetting.getInvoicingJob());
		JobInstance nextJobInstance = setChain(jobInstance, electronicInvoiceSetting.isForceXmlGeneration(), electronicInvoiceSetting.getXmlGenerationJob());
		nextJobInstance = setChain(nextJobInstance, electronicInvoiceSetting.isForcePDFGeneration(), electronicInvoiceSetting.getPdfGenerationJob());
		setChain(nextJobInstance, electronicInvoiceSetting.isForceUBLGeneration(), electronicInvoiceSetting.getUblGenerationJob());
	}
	
	private JobInstance setChain(JobInstance jobInstance, boolean isForcingGeneration, String generationNextJob) {
		JobInstance nextJobInstance = getNext(jobInstance);
		if(isForcingGeneration) {
			nextJobInstance.setFollowingJob(find(generationNextJob));
			jobInstanceService.update(nextJobInstance);
		}
		return nextJobInstance;
	}
	private JobInstance find(String instanceJobCode) {
		JobInstance jobInstance = jobInstanceService.findByCode(instanceJobCode);
		if(jobInstance == null) {
			throw new EntityDoesNotExistsException(JobInstance.class, instanceJobCode);
		}
		return jobInstance;
	}
	private JobInstance getNext(JobInstance jobInstance) {
		if(jobInstance.getFollowingJob() == null) return jobInstance;
		return getNext(jobInstanceService.findByCode(jobInstance.getFollowingJob().getCode()));
	}
}
