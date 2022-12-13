package org.meveo.apiv2.billing.service;

import static java.util.Optional.ofNullable;

import javax.inject.Inject;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.service.billing.impl.RatedTransactionService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RatedTransactionApiService implements ApiService<RatedTransaction> {

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Override
	public Optional<RatedTransaction> findById(Long id) {
		return ofNullable(ratedTransactionService.findById(id));
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

	@Override
	public Optional<RatedTransaction> findByCode(String code) {
		return ofNullable(ratedTransactionService.findByCode(code));
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
		return ratedTransactionService.createRatedTransaction(input.getBillingAccountCode(), input.getUserAccountCode(),
				input.getSubscriptionCode(), input.getServiceInstanceCode(), input.getChargeInstanceCode(), input.getUsageDate(),
				input.getUnitAmountWithoutTax(), input.getQuantity(), input.getParameter1(), input.getParameter2(), input.getParameter3(), input.getParameterExtra());
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
	 * Update the rated transaction
	 *
	 * @param ratedTransaction the rated transaction
	 * @param description the description
	 * @param unitAmountWithoutTax the unit amount without tax
	 * @param quantity the quantity
	 * @param param1 the param1
	 * @param param2 the param2
	 * @param param3 the param3
	 * @param paramExtra the param extra
	 */
	public void update(RatedTransaction ratedTransaction, String description, BigDecimal unitAmountWithoutTax,  BigDecimal quantity, String param1, String param2, String param3, String paramExtra) {
		ratedTransactionService.updateRatedTransaction(ratedTransaction, description, unitAmountWithoutTax, quantity, param1, param2, param3, paramExtra);
	}

}
