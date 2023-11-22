package org.meveo.apiv2.billing.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.meveo.model.billing.RatedTransactionStatusEnum.OPEN;
import static org.meveo.model.billing.RatedTransactionStatusEnum.REJECTED;
import static org.meveo.model.jobs.JobLauncherEnum.API;

import org.apache.commons.collections.MapUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.job.JobExecutionResultResponseDto;
import org.meveo.api.rest.exception.BadRequestException;
import org.meveo.apiv2.billing.DuplicateRTResult;
import org.meveo.apiv2.billing.ProcessCdrListResult.Statistics;
import org.meveo.apiv2.billing.ProcessingModeEnum;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class RatedTransactionApiService implements ApiService<RatedTransaction> {

	private static final Logger log = LoggerFactory.getLogger(RatedTransactionApiService.class);
	
	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private JobInstanceService jobInstanceService;
	
    @Inject
    private JobExecutionService jobExecutionService;
	
	@Inject
	private FinanceSettingsService financeSettingsService;
	
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
		return empty();
	}

	@Override
	public Optional<RatedTransaction> patch(Long id, RatedTransaction baseEntity) {
		// TODO Auto-generated method stub
		return empty();
	}

	@Override
	public Optional<RatedTransaction> delete(Long id) {
		return empty();
	}

	@Override
	public Optional<RatedTransaction> findByCode(String code) {
		return ofNullable(ratedTransactionService.findByCode(code));
	}

	/**
	 * @param id
	 */
	public void cancelRatedTransaction(Long id) {
		ratedTransactionService.cancelRatedTransactions(asList(id));
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
		return Collections.emptyList();
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
	 * @param usageDate usage date
	 */
	public void update(RatedTransaction ratedTransaction, String description, BigDecimal unitAmountWithoutTax,
					   BigDecimal quantity, String param1, String param2, String param3, String paramExtra, Date usageDate, String businessKey) {
		ratedTransactionService.updateRatedTransaction(ratedTransaction,
				description, unitAmountWithoutTax, quantity, param1, param2, param3, paramExtra, usageDate, businessKey);
	}

	public Object duplication(Map<String, Object> filters, ProcessingModeEnum mode, boolean negateAmount, boolean returnRts, boolean startJob) {
		DuplicateRTResult result = new DuplicateRTResult();
		
		FinanceSettings financeSettings = financeSettingsService.getFinanceSetting();
		int maxLimit = financeSettings.getSynchronousMassActionLimit(); 

		if(MapUtils.isEmpty(filters)) {
			throw new InvalidParameterException("filters is required");
		}
		Long countRatedTransaction = ratedTransactionService.count(filters);
		if(countRatedTransaction == 0) {
			log.warn("list of rated transaction to duplicate is empty for filters : {}", filters);
			result.getActionStatus().setMessage("list of rated transaction to duplicate is empty.");
			return result;
		}

		
		if (countRatedTransaction.intValue() > maxLimit) {
			log.info("filter for duplication has more than : {}, current rated transaction from filters are : {} . will job be lunched ? : {}",
					maxLimit, countRatedTransaction, startJob);
			if (!startJob) {
				throw new BusinessException(String.format(
						"Number of rated items to process exceeds synchronous limit (%d/%d). Please, use flag ‘startJob’ to force asynchronous execution.",
						countRatedTransaction.intValue(), maxLimit));
			} else {
				return duplicateRatedTransactionWithJob(filters, negateAmount);
			}
		}

		List<RatedTransaction> rtToDuplicate = ratedTransactionService.findByFilter(filters);
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

					duplicate.setTransactionalUnitAmountTax(duplicate.getTransactionalUnitAmountTax() != null ? duplicate.getTransactionalUnitAmountTax().negate() : null);
					duplicate.setTransactionalUnitAmountWithoutTax(duplicate.getTransactionalUnitAmountWithoutTax() != null ? duplicate.getTransactionalUnitAmountWithoutTax().negate() : null);
					duplicate.setTransactionalUnitAmountWithTax(duplicate.getTransactionalUnitAmountWithTax() != null ? duplicate.getTransactionalUnitAmountWithTax().negate() : null);
					duplicate.setTransactionalAmountTax(duplicate.getTransactionalAmountTax() != null ? duplicate.getTransactionalAmountTax().negate() : null);
					duplicate.setTransactionalAmountWithoutTax(duplicate.getTransactionalAmountWithoutTax() != null ? duplicate.getTransactionalAmountWithoutTax().negate() : null);
					duplicate.setTransactionalAmountWithTax(duplicate.getTransactionalAmountWithTax() != null ? duplicate.getTransactionalAmountWithTax().negate() : null);
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

	public JobExecutionResultResponseDto duplicateRatedTransactionWithJob(Map<String, Object> filters, boolean negateAmount) {
		JobExecutionResultResponseDto result = new JobExecutionResultResponseDto();
		JobInstance duplicateRatedTransactionJob = jobInstanceService.findByCode("DuplicateRatedTransactionJob");
		if (duplicateRatedTransactionJob != null) {
			if (!jobExecutionService.isAllowedToExecute(duplicateRatedTransactionJob)) {
				throw new BusinessException("Job DuplicateRatedTransactionJob is already running. Please, wait until it is done before trying again.");
			}

			if (duplicateRatedTransactionJob.getCfValues() == null) {
				duplicateRatedTransactionJob.setCfValues(new CustomFieldValues());
			}

			Map<String, Object> jobParams = new HashMap<>();
			jobParams.put("DuplicateRatedTransactionJob_negateAmount", negateAmount);
            jobParams.put("DuplicateRatedTransactionJob_advancedParameters", filters);
            duplicateRatedTransactionJob.setRunTimeValues(jobParams);
            
			jobExecutionService.executeJob(duplicateRatedTransactionJob, jobParams, API);
			result.getActionStatus().setMessage("Job DuplicateRatedTransactionJob launched");
		}
		return result;
	}

	public Entry<String, String> cancelRatedTransactions(Map<String, Object> filters,
														  boolean failOnIncorrectStatus, boolean returnRTs) {
		StringBuilder response = new StringBuilder();
		if (failOnIncorrectStatus) {
			List<RatedTransaction> validationResult = getRatedTransactionsList(filters, true);
			if(!validationResult.isEmpty()) {
				Map<RatedTransactionStatusEnum, List<RatedTransaction>> groupedRt
						= validationResult.stream().collect(Collectors.groupingBy(RatedTransaction::getStatus));
				response.append("Cancellation process stopped RT list : ");
				groupedRt.entrySet().forEach(entry -> response.append(entry.getKey())
						.append(": ")
						.append(entry.getValue().stream()
								.map(ratedTransaction -> ratedTransaction.getId().toString())
								.collect(joining(",")))
						.append("\n"));
				return new SimpleEntry<>("FAILS", response.toString());
			}
		}
		List<RatedTransaction> ratedTransactionToCancel = getRatedTransactionsList(filters, false);
		List<Long> rtIds = ratedTransactionToCancel.stream().map(RatedTransaction::getId).collect(Collectors.toList());
		if(rtIds.isEmpty()) {
			throw new NotFoundException("No rated transaction found to cancel");
		}
		ratedTransactionService.cancelRatedTransactions(rtIds);
		response.append(ratedTransactionToCancel.size()).append(" RTs cancelled");
		if(returnRTs) {
			response.append(", having ids : ")
					.append(
							ratedTransactionToCancel.stream()
									.map(ratedTransaction -> ratedTransaction.getId().toString())
									.collect(joining(",")));
		}
		return new SimpleEntry<>("SUCCESS", response.toString());
	}

	private List<RatedTransaction> getRatedTransactionsList(Map<String, Object> filters, boolean validation) {
		Map<String, Object> fetchFilters =  new HashMap<>(filters);
		if(fetchFilters.containsKey("status") && fetchFilters.get("status") instanceof String) {
			fetchFilters.put("status", RatedTransactionStatusEnum.valueOf((String) fetchFilters.get("status")));
		}
		if(validation) {
			fetchFilters.put("not-inList status", asList(OPEN, REJECTED));
		} else {
			fetchFilters.put("inList status", asList(OPEN, REJECTED));
		}
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(fetchFilters);
		QueryBuilder queryBuilder =
				ratedTransactionService.getQuery(paginationConfiguration);
		return queryBuilder.getQuery(ratedTransactionService.getEntityManager()).getResultList();
 	}
}
