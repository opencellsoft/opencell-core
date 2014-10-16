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
package org.meveo.service.payments.impl;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.dunning.DunningLotBuilder;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Auditable;
import org.meveo.model.admin.DunningHistory;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.DunningActionTypeEnum;
import org.meveo.model.payments.DunningLOT;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;


@Stateless @LocalBean
public class DunningLOTService extends PersistenceService<DunningLOT> {

	
	 private static final Logger logger = Logger.getLogger(DunningLOTService.class.getName());
	    
	  private static final String USER_SYSTEM_ID = "bayad.userSystemId";
	  
	 @Inject
	 ActionDunningService actionDunningService;
	
	    
	 @Inject
	 UserService userService;
	    
	
	 public void createDunningLOTAndCsvFile(List<ActionDunning> listActionDunning,DunningHistory dunningHistory,Provider provider) throws Exception {
	        logger.info("createDunningLOTAndCsvFile ...");        
	     
	        User systemUser=userService.findById(Long.valueOf(ParamBean.getInstance().getProperty(USER_SYSTEM_ID,"1")));

	        if (listActionDunning != null && !listActionDunning.isEmpty()) {
	            for (DunningActionTypeEnum actionType : DunningActionTypeEnum.values()) {
	                DunningLOT dunningLOT = new DunningLOT();
	                dunningLOT.setActionType(actionType);
	                dunningLOT.setAuditable(getAuditable(systemUser));
	                dunningLOT.setProvider(provider);

	                create(dunningLOT);
	                logger.info("createDunningLOTAndCsvFile persist dunningLOT ok");
	                for (ActionDunning actionDunning : listActionDunning) {
	                    if (actionDunning.getTypeAction() == actionType) {
	                        actionDunning.setDunningLOT(dunningLOT);
	                        actionDunning.setAuditable(getAuditable(systemUser));
	                        actionDunningService.create(actionDunning);
	                        dunningLOT.getActions().add(actionDunning);
	                        update(dunningLOT);
	                    }
	                }
	                if (dunningLOT.getActions().isEmpty()) {
	                	remove(dunningLOT);
	                } else {
	                    try {
	                        dunningLOT.setFileName(buildFile(dunningLOT));
	                        logger.info("doCommit dunningLOT.setFileName ok");
	                        dunningLOT.setDunningHistory(dunningHistory);
	                        update(dunningLOT);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }
	       
	        logger.info("createDunningLOTAndCsvFile done");
	    }

	    private String buildFile(DunningLOT dunningLOT) throws Exception {
	        DunningLotBuilder bunningLotBuilder = new DunningLotBuilder(dunningLOT);
	        bunningLotBuilder.exportToFile();
	        return bunningLotBuilder.getFileName();
	    }
	    
	    public static Auditable getAuditable(User user) throws Exception {
	        Auditable auditable = new Auditable();
	        auditable.setCreated(new Date());
	        auditable.setCreator(user);
	        return auditable;
	    }
	
}
