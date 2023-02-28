package org.meveo.apiv2.billing.service;

import static java.util.Optional.ofNullable;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.rest.exception.BadRequestException;
import org.meveo.apiv2.billing.DuplicateRTResult;
import org.meveo.apiv2.billing.ProcessingModeEnum;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.pro.packaged.mo;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class RatedTransactionApiService implements ApiService<RatedTransaction> {

	private static final Logger log = LoggerFactory.getLogger(RatedTransactionApiService.class);
	
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
				input.getUnitAmountWithoutTax(), input.getQuantity(), input.getParameter1(), input.getParameter2(),
				input.getParameter3(), input.getParameterExtra(), input.getDescription());
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

	@SuppressWarnings("unchecked")
	public DuplicateRTResult duplication(Map<String, Object> filters, ProcessingModeEnum mode, boolean negateAmount,
			boolean returnRts) {
		DuplicateRTResult result = new DuplicateRTResult();
		if(MapUtils.isEmpty(filters)) {
			throw new InvalidParameterException("filters is required");
		}
		Map<String, Object> modifiableFilters = new HashMap<>(filters);
		if(filters.get("status") == null) {
			modifiableFilters.put("status", RatedTransactionStatusEnum.BILLED);
		}
		List<RatedTransaction> rtToDuplicate = ratedTransactionService.findByFilter(modifiableFilters); 
		if(CollectionUtils.isEmpty(rtToDuplicate)) {
			log.warn("list of rated transaction to duplicate is empty for filters : " + filters);
			result.getActionStatus().setMessage("list of rated transaction to duplicate is empty.");
			return result;
		}

		List<Long> successList = new ArrayList<>();
		result.getStatistics().setTotal(rtToDuplicate.size());
		for (RatedTransaction ratedTransaction : rtToDuplicate) {
			try {
				RatedTransaction duplicate = new RatedTransaction(ratedTransaction);
				if(negateAmount) {
					duplicate.setUnitAmountTax(duplicate.getUnitAmountTax().negate());
					duplicate.setUnitAmountWithoutTax(duplicate.getUnitAmountWithoutTax().negate());
					duplicate.setUnitAmountWithTax(duplicate.getUnitAmountWithTax().negate());
					duplicate.setAmountTax(duplicate.getAmountTax().negate());
					duplicate.setAmountWithoutTax(duplicate.getAmountWithoutTax().negate());
					duplicate.setAmountWithTax(duplicate.getAmountWithTax().negate());
					duplicate.setRawAmountWithTax(duplicate.getRawAmountWithTax().negate());
					duplicate.setRawAmountWithoutTax(duplicate.getRawAmountWithoutTax().negate());
				}
				duplicate.setOriginRatedTransaction(ratedTransaction);
				ratedTransactionService.create(duplicate);
				successList.add(duplicate.getId());
				result.getStatistics().addSuccess();
			}catch(RuntimeException e) {
				log.error("Error while duplicate rated transaction id : " + ratedTransaction.getId());
				result.getFailIds().add(ratedTransaction.getId());
				result.getStatistics().addFail();
				if(mode == ProcessingModeEnum.STOP_ON_FIRST_FAIL) {
					result.getActionStatus().setMessage(e.getMessage());
					break;
				}else if(mode == ProcessingModeEnum.ROLLBACK_ON_ERROR) {
					throw new BadRequestException(result.getActionStatus());
				}
			}
			
		}
		if(returnRts) {
			result.getActionStatus().setMessage(String.format("%d RTs created with ids, %s", successList.size(), successList));
		}
		
		return result;
	}

}
