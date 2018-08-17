package org.meveo.service.intcrm.impl;

import java.util.List;

import javax.persistence.Query;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AdditionalDetails;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomerService;

public class AdditionalDetailsService extends PersistenceService<AdditionalDetails>{
	@Inject
	CustomerService customerService;
	
	public AdditionalDetails findByCompany(String company) {
		Query query = getEntityManager().createQuery("from " + AdditionalDetails.class.getSimpleName() + " where companyName=:company").setParameter("companyName", company);
        if (query.getResultList().size() == 0) {
            return null;
        }
        return (AdditionalDetails) query.getResultList().get(0);
  	}
	
	public void createAll() throws BusinessException {
		List<Customer> customers = customerService.list();
		
		for(Customer customer : customers) {
			if(customer.getAdditionalDetails() == null) {
				AdditionalDetails additionalDetails = new AdditionalDetails();
				this.create(additionalDetails);
				customer.setAdditionalDetails(additionalDetails);
				customerService.update(customer);
			}
		}
	}
}

