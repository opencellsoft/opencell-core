package org.meveo.service.admin.impl;

import org.meveo.model.RegistrationNumber;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class RegistrationNumberService extends PersistenceService<RegistrationNumber> {

	public RegistrationNumber findByRegistrationNo(String registrationNo) {
		try{
			return (RegistrationNumber) getEntityManager().createQuery("from RegistrationNumber r where lower(r.registrationNo)= lower(:registrationNo) ")
					.setParameter("registrationNo", registrationNo).getSingleResult();
		}catch (NoResultException e) {
			log.info("No class found for registration number : {}", registrationNo);
			return null;
		}
	}
}
