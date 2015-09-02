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
package org.meveo.service.communication.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.communication.InboundCommunicationEvent;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.base.BusinessService;

/**
 * MeveoInstance service implementation.
 */
@Stateless
public class MeveoInstanceService extends BusinessService<MeveoInstance> {

	@Inject
	private Event<InboundCommunicationEvent> event;

	public MeveoInstance findByCode(String meveoInstanceCode) {
		QueryBuilder qb = new QueryBuilder(MeveoInstance.class, "c");
		qb.addCriterion("code", "=", meveoInstanceCode, true);
		
		try {
			return (MeveoInstance) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			log.warn("failed to find MeveoInstance", e.getMessage());
			return null;
		}
	}

	public MeveoInstance getThis() {
		List<MeveoInstance> meveoInstances = list();
		if (meveoInstances == null || meveoInstances.isEmpty()) {
			return null;
		}
		return meveoInstances.get(0);
	}

	public void fireInboundCommunicationEvent(CommunicationRequestDto communicationRequestDto) {
		InboundCommunicationEvent inboundCommunicationEvent = new InboundCommunicationEvent();
		inboundCommunicationEvent.setCommunicationRequestDto(communicationRequestDto);
		event.fire(inboundCommunicationEvent);
	}

}
