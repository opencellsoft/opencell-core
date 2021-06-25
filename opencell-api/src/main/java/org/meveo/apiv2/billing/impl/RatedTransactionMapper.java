package org.meveo.apiv2.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.billing.ImmutableRatedTransactionInput;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.billing.RatedTransaction;

public class RatedTransactionMapper
		extends ResourceMapper<org.meveo.apiv2.billing.RatedTransactionInput, RatedTransaction> {

	@Override
	public org.meveo.apiv2.billing.RatedTransactionInput toResource(RatedTransaction entity) {
		try {
			ImmutableRatedTransactionInput resource = (ImmutableRatedTransactionInput) initResource(
					ImmutableRatedTransactionInput.class, entity);
			return ImmutableRatedTransactionInput.builder().from(resource).id(entity.getId()).build();
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public RatedTransaction toEntity(org.meveo.apiv2.billing.RatedTransactionInput resource) {
		try {
			RatedTransaction ratedTransaction = initEntity(resource, new RatedTransaction());
			ratedTransaction.setId(resource.getId());
			return ratedTransaction;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}
}
