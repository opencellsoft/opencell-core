/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

