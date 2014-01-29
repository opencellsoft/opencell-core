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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author anasseh
 */
@Stateless @LocalBean
public class DDRequestLotOpService extends PersistenceService<DDRequestLotOp> {
	
	@SuppressWarnings("unchecked")
	public List<DDRequestLotOp> getDDRequestOps() {
		List<DDRequestLotOp> ddrequestOps = new ArrayList<DDRequestLotOp>();
		try {
			ddrequestOps = (List<DDRequestLotOp>) getEntityManager().createQuery("from " + DDRequestLotOp.class.getSimpleName() + " where status=:status")
					.setParameter("status", DDRequestOpStatusEnum.WAIT).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ddrequestOps;
	}

}
