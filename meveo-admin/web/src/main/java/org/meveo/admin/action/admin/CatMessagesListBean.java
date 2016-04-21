/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CatMessages;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

@Named
@ConversationScoped
public class CatMessagesListBean extends CatMessagesBean {

    private static final long serialVersionUID = -3037867704912788015L;
    
    public void onCellEdit(CellEditEvent event) throws BusinessException{
    	String oldDescription = (String) event.getOldValue();
    	String newDescription = (String) event.getNewValue();
    	boolean hasNewDescription = StringUtils.isBlank(oldDescription) && !StringUtils.isBlank(newDescription);
    	boolean isDifferentDescription = !StringUtils.isBlank(oldDescription) && !oldDescription.equals(newDescription);
    	if(hasNewDescription || isDifferentDescription){
    		DataTable o = (DataTable) event.getSource();
    		CatMessages catMsg = (CatMessages)o.getRowData();
    		if(StringUtils.isBlank(newDescription)){
    			this.delete(catMsg.getId());
    		} else {
        		catMsg.setDescription(newDescription);
        		this.saveOrUpdate(catMsg);
    		}
    	}
    }
    
}