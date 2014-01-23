/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	     
	        User systemUser=userService.findById(Long.valueOf(ParamBean.getInstance().getProperty(USER_SYSTEM_ID)));

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
