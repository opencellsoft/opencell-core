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
import org.meveo.apiv2.billing.ProcessCdrListResult.Statistics;
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
				input.getParameter3(), input.getParameterExtra(), input.getDescription(), input.getBusinessKey());
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
	public void update(RatedTransaction ratedTransaction, String description, BigDecimal unitAmountWithoutTax,  BigDecimal quantity, String param1, String param2, String param3, String paramExtra, String businessKey) {
		ratedTransactionService.updateRatedTransaction(ratedTransaction, description, unitAmountWithoutTax, quantity, param1, param2, param3, paramExtra, businessKey);
	}

	public DuplicateRTResult duplication(Map<String, Object> filters, ProcessingModeEnum mode, boolean negateAmount,
			boolean returnRts) {
		DuplicateRTResult result = new DuplicateRTResult();
		if(MapUtils.isEmpty(filters)) {
			throw new InvalidParameterException("filters is required");
		}
		Map<String, Object> modifiableFilters = new HashMap<>(filters);
		if(filters.get("status") == null) {
			modifiableFilters.put("status", RatedTransactionStatusEnum.BILLED.toString());
		}
		List<RatedTransaction> rtToDuplicate = ratedTransactionService.findByFilter(modifiableFilters);
		if(CollectionUtils.isEmpty(rtToDuplicate)) {
			log.warn("list of rated transaction to duplicate is empty for filters : " + filters);
			result.getActionStatus().setMessage("list of rated transaction to duplicate is empty.");
			return result;
		}

		List<Long> successList = new ArrayList<>();
		Statistics statics = result.getStatistics();
		statics.setTotal(rtToDuplicate.size());
		for (RatedTransaction ratedTransaction : rtToDuplicate) {
			try {
				RatedTransaction duplicate = new RatedTransaction(ratedTransaction);
				if(negateAmount) {
					duplicate.setUnitAmountTax(duplicate.getUnitAmountTax() != null ? duplicate.getUnitAmountTax().negate() : null);
					duplicate.setUnitAmountWithoutTax(duplicate.getUnitAmountWithoutTax() != null ? duplicate.getUnitAmountWithoutTax().negate() : null);
					duplicate.setUnitAmountWithTax(duplicate.getUnitAmountWithTax() != null ? duplicate.getUnitAmountWithTax().negate() : null);
					duplicate.setAmountTax(duplicate.getAmountTax() != null ? duplicate.getAmountTax().negate() : null);
					duplicate.setAmountWithoutTax(duplicate.getAmountWithoutTax() != null ? duplicate.getAmountWithoutTax().negate() : null);
					duplicate.setAmountWithTax(duplicate.getAmountWithTax() != null ? duplicate.getAmountWithTax().negate() : null);
					duplicate.setRawAmountWithTax(duplicate.getRawAmountWithTax() != null ? duplicate.getRawAmountWithTax().negate() : null);
					duplicate.setRawAmountWithoutTax(duplicate.getRawAmountWithoutTax() != null ? duplicate.getRawAmountWithoutTax().negate() : null);
				}
				duplicate.setOriginRatedTransaction(ratedTransaction);
				ratedTransactionService.create(duplicate);
				successList.add(duplicate.getId());
				statics.addSuccess();
			}catch(RuntimeException e) {
				log.error("Error while duplicate rated transaction id : " + ratedTransaction.getId());
				result.getFailIds().add(ratedTransaction.getId());
				statics.addFail();
				if(mode == ProcessingModeEnum.STOP_ON_FIRST_FAIL) {
					result.getActionStatus().setMessage(e.getMessage());
					if(statics.getFail() == statics.getTotal()) {
						result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
					}else
						result.getActionStatus().setStatus(ActionStatusEnum.WARNING);
					break;
				}else if(mode == ProcessingModeEnum.ROLLBACK_ON_ERROR) {
					throw new BadRequestException(result.getActionStatus());
				}
			}
			
		}
		if(returnRts) {
			   result.getCreatedRts().addAll(successList);
		}
		result.getActionStatus().setMessage(String.format("Created %d rated items, %d failed", successList.size(), result.getFailIds().size()));
		
		return result;
	}

}
