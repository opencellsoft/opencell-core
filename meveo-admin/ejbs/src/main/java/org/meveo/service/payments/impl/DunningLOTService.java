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
import org.slf4j.Logger;

@Stateless
public class DunningLOTService extends PersistenceService<DunningLOT> {

	@Inject
	private Logger log;

	private static final String USER_SYSTEM_ID = "bayad.userSystemId";

	@Inject
	private ActionDunningService actionDunningService;

	@Inject
	private UserService userService;

	public void createDunningLOTAndCsvFile(
			List<ActionDunning> listActionDunning,
			DunningHistory dunningHistory, Provider provider) throws Exception {
		log.info("createDunningLOTAndCsvFile ...");

		User systemUser = userService.findById(Long.valueOf(ParamBean
				.getInstance().getProperty(USER_SYSTEM_ID, "1")));

		if (listActionDunning != null && !listActionDunning.isEmpty()) {
			for (DunningActionTypeEnum actionType : DunningActionTypeEnum
					.values()) {
				DunningLOT dunningLOT = new DunningLOT();
				dunningLOT.setActionType(actionType);
				dunningLOT.setAuditable(getAuditable(systemUser));
				dunningLOT.setProvider(provider);

				create(dunningLOT);
				log.info("createDunningLOTAndCsvFile persist dunningLOT ok");
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
						log.info("doCommit dunningLOT.setFileName ok");
						dunningLOT.setDunningHistory(dunningHistory);
						update(dunningLOT);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}

		log.info("createDunningLOTAndCsvFile done");
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
