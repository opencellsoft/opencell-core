package org.meveo.apiv2.billing.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.admin.exception.ValidationException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.service.billing.impl.RatedTransactionService;

public class RatedTransactionApiService implements ApiService<RatedTransaction> {

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Override
	public Optional<RatedTransaction> findById(Long id) {
		return Optional.ofNullable(ratedTransactionService.findById(id));
	}

	@Override
	public RatedTransaction create(RatedTransaction ratedTransaction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<RatedTransaction> update(Long id, RatedTransaction baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<RatedTransaction> patch(Long id, RatedTransaction baseEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<RatedTransaction> delete(Long id) {
		return null;
	}

	/**
	 * @param id
	 */
	public void cancelRatedTransaction(Long id) {
		ratedTransactionService.cancelRatedTransactions(Arrays.asList(id));
	}

	/**
	 * @param input
	 * @return
	 */
	public RatedTransaction create(org.meveo.apiv2.billing.RatedTransactionInput input) {
		String errors = "";
		if (input.getBillingAccountCode() == null) {
			errors = errors + " billingAccountCode,";
		}

		if (input.getSubscriptionCode() == null) {
			errors = errors + " subscriptionCode,";
		}

		if (input.getServiceInstanceCode() == null) {
			errors = errors + " sericeInstanceCode,";
		}

		if (input.getChargeInstanceCode() == null) {
			errors = errors + " chargeInstanceCode,";
		}
		if (!errors.isBlank()) {
			throw new ValidationException("Missing fields to create RatedTransaction : " + errors);
		}
		return ratedTransactionService.createRatedTransaction(input.getBillingAccountCode(), input.getUserAccountCode(),
				input.getSubscriptionCode(), input.getServiceInstanceCode(), input.getChargeInstanceCode(),
				input.getUnitAmountWithoutTax(), input.getQuantity());
	}


	@Override
	public List<RatedTransaction> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getCount(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param ratedTransaction
	 * @param unitAmountWithoutTax
	 * @param quantity
	 */
	public void update(RatedTransaction ratedTransaction, BigDecimal unitAmountWithoutTax, BigDecimal quantity) {
		ratedTransactionService.updateRatedTransaction(ratedTransaction, unitAmountWithoutTax, quantity);
	}

}
