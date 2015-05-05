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
package org.meveo.admin.job;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.InvoiceService;
import org.slf4j.Logger;

@Stateless
public class PDFFilesOutputProducer {

	@Inject
	private Logger log;

	@Inject
	private InvoiceService invoiceService;
	/**
	 * @see org.meveo.core.outputproducer.OutputProducer#produceOutput(java.util.List)
	 */
	@Asynchronous
	public Future<Boolean> producePdf(Map<String, Object> parameters,
			JobExecutionResultImpl result, User currentUser) throws Exception {
		try {
			invoiceService.producePdf(parameters, currentUser);
			return new AsyncResult<Boolean>(true);
		} catch (Exception e) { 
			result.registerError(e.getMessage()); 
			log.error(e.getMessage());
		}
		return new AsyncResult<Boolean>(false);
	}

}