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
package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;

import org.jboss.seam.international.status.builder.BundleKey;
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
				boolean result=pricePlanMatrixService.updateCellEdit(entity, getCurrentUser());
				if(result){
					messages.info(new BundleKey("messages", "update.successful"));
				}
			}catch(EntityExistsException e){
				log.error("Fail to update Price plan {}. Reason {}",entity.getCode(),(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
				messages.info(new BundleKey("messages", "pricePlanMatrix.codeExistedFail"),entity.getCode());
			}catch(Exception e){
				log.error("Fail to update Price plan {}. Reason {}",entity.getCode(),(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
				messages.info(new BundleKey("messages", "pricePlanMatrix.updateCellFail"),entity.getCode(),(e.getMessage()!=null?e.getClass().getSimpleName():e.getMessage()));
			}
		}
	}
}
