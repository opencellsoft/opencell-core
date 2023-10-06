package org.meveo.service.billing.impl;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.Channel;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.billing.ElectronicInvoiceSetting;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.job.JobInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

@Stateless
public class EinvoiceSettingService extends PersistenceService<ElectronicInvoiceSetting> {
	
	@Inject
	private JobInstanceService jobInstanceService;
	
	public ElectronicInvoiceSetting findEinvoiceSetting() {
		try {
			TypedQuery<ElectronicInvoiceSetting> query = getEntityManager().createQuery("from ElectronicInvoiceSetting g order by g.id desc", entityClass).setMaxResults(1);
			return query.getSingleResult();
		} catch (NoResultException e) {
			log.debug("No {} found", getEntityClass().getSimpleName());
			return null;
		}
	}
	
	public void chainToNextJob(ElectronicInvoiceSetting electronicInvoiceSetting) {
		JobInstance invoiceJobInstance = find(electronicInvoiceSetting.getInvoicingJob());
		JobInstance xmlJobInstance = setChain(invoiceJobInstance, electronicInvoiceSetting.isForceXmlGeneration(), electronicInvoiceSetting.getXmlGenerationJob());
		JobInstance pdfJobInstance = setChain(xmlJobInstance, electronicInvoiceSetting.isForcePDFGeneration(), electronicInvoiceSetting.getPdfGenerationJob());
		setChain(pdfJobInstance, electronicInvoiceSetting.isForceUBLGeneration(), electronicInvoiceSetting.getUblGenerationJob());
	}
	
	private JobInstance setChain(JobInstance jobInstance, boolean isForcingGeneration, String generationNextJob) {
		List<JobInstance> chainingJobs = new ArrayList<>();
		JobInstance nextJobInstance = getNext(jobInstance, chainingJobs, generationNextJob);
		if (isForcingGeneration && !nextJobInstance.getCode().equals(generationNextJob)) {
			nextJobInstance.setFollowingJob(find(generationNextJob));
			jobInstanceService.update(nextJobInstance);
		}else{
			return jobInstance;
		}
		return nextJobInstance;
	}
	
	private JobInstance find(String instanceJobCode) {
		JobInstance jobInstance = jobInstanceService.findByCode(instanceJobCode);
		if (jobInstance == null) {
			throw new EntityDoesNotExistsException(JobInstance.class, instanceJobCode);
		}
		return jobInstance;
	}
	
	private JobInstance getNext(JobInstance jobInstance, List<JobInstance> chainingJobs, String generationNextJob) {
		if (jobInstance.getFollowingJob() == null) return jobInstance;
		if(jobInstance.getCode().equals(generationNextJob)){
			return jobInstanceService.findByCode(jobInstance.getCode());
		}
		chainingJobs.add(jobInstance.getFollowingJob());
		return getNext(jobInstanceService.findByCode(jobInstance.getFollowingJob().getCode()), chainingJobs, generationNextJob);
	}
}
	
	
