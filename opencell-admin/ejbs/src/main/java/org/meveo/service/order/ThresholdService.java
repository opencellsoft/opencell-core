package org.meveo.service.order;

import org.meveo.model.ordering.Threshold;
import org.meveo.service.base.PersistenceService;

import jakarta.ejb.Stateless;

@Stateless
public class ThresholdService extends PersistenceService<Threshold> {


public void deleteThresholdsByOpenOrderTemplateId(Long id)
{
    getEntityManager().createNamedQuery("Threshold.deleteByOpenOrderTemplate")
				.setParameter("openOrderTemplateId", id)
				.executeUpdate();
}

	public void deleteThresholdsByOpenOrderId(Long id) {
		getEntityManager().createNamedQuery("Threshold.deleteByOpenOrder")
				.setParameter("openOrderId", id)
				.executeUpdate();
	}
}
