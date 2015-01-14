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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class DDRequestLotOpService extends PersistenceService<DDRequestLotOp> {

	@SuppressWarnings("unchecked")
	public List<DDRequestLotOp> getDDRequestOps() {
		List<DDRequestLotOp> ddrequestOps = new ArrayList<DDRequestLotOp>();

		try {
			ddrequestOps = (List<DDRequestLotOp>) getEntityManager()
					.createQuery("from " + DDRequestLotOp.class.getSimpleName() + " where status=:status")
					.setParameter("status", DDRequestOpStatusEnum.WAIT).getResultList();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return ddrequestOps;
	}

}
