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
package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

@Named
@ConversationScoped
public class PricePlanMatrixListBean extends PricePlanMatrixBean {

	private static final long serialVersionUID = -3037867704912788027L;
	
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	
	public void onCellEdit(CellEditEvent event) {
		PricePlanMatrix entity=(PricePlanMatrix)(((DataTable)event.getComponent()).getRowData());
		if(entity!=null&&!entity.isTransient()){
			try{
				boolean result=pricePlanMatrixService.updateCellEdit(entity);
				if(result){
					messages.info(new BundleKey("messages", "update.successful"));
				}
			}catch(BusinessException e){
				log.error("Fail to update Price plan {}. Reason {}",entity.getCode(),(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
				messages.info(new BundleKey("messages", "pricePlanMatrix.codeExistedFail"),entity.getCode());
			}catch(Exception e){
				log.error("Fail to update Price plan {}. Reason {}",entity.getCode(),(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
				messages.info(new BundleKey("messages", "pricePlanMatrix.updateCellFail"),entity.getCode(),(e.getMessage()!=null?e.getClass().getSimpleName():e.getMessage()));
			}
		}
	}

	public String getPricePlanEventCode(PricePlanMatrix item) {
		if(!ListUtils.isEmtyCollection(item.getChargeTemplates())) {
			return item.getChargeTemplates().stream().findFirst().map(ChargeTemplate::getCode).orElse(null);
		}
		return null;
	}
}
