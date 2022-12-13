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
package org.meveo.service.billing.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceTypeEnum;
import org.meveo.model.billing.LinkedInvoice;
import org.meveo.service.base.PersistenceService;

/**
 * The Class InvoiceService.
 *
 * @author Khairi
 * @lastModifiedVersion 14.0
 */
@Stateless
public class LinkedInvoiceService extends PersistenceService<LinkedInvoice> {
    
	public void deleteByIdInvoiceAndLinkedInvoice(Long invoiceId, List<Long> linkedInvoice) {
		if (CollectionUtils.isEmpty(linkedInvoice)) {
			return;
		}
		getEntityManager().createNamedQuery("LinkedInvoice.deleteByIdInvoiceAndLinkedInvoice")
				.setParameter("invoiceId", invoiceId)
				.setParameter("linkedInvoiceId", linkedInvoice)
				.executeUpdate();
	}


	public void deleteByInvoiceIdAndType(Long invoiceId, InvoiceTypeEnum type) {
		getEntityManager().createNamedQuery("LinkedInvoice.deleteByInvoiceIdAndType")
        .setParameter("invoiceId", invoiceId)
        .setParameter("type", type)
        .executeUpdate();
		
	}
	public void removeLinkedAdvances(List<Long> advInvoices) {
	    getEntityManager().createNamedQuery("LinkedInvoice.removeLinkedAdvances").setParameter("invoiceIds", advInvoices).executeUpdate();
	}
    
}