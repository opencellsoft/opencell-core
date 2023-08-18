package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.CustomerBalance;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningModeEnum;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.payments.impl.CustomerBalanceService;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DunningSettingsApiService implements ApiService<DunningSettings> {
	
	@Inject
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Inject
	private DunningSettingsService dunningSettingsService;
	
	@Inject
	private AccountingArticleService accountingArticleService;
	
	@Inject
	private CustomerBalanceService customerBalanceService;
	
	@Inject
	private DunningCollectionPlanService dunningCollectionPlanService;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String NO_DUNNING_FOUND = "No Dunning found for id : ";
	private static final String NO_ACCOUNTING_ARTICLE_FOUND = "No Accounting article was found for the id : ";
	private static final String NO_CUSTOMER_BALANCE_FOUND = "No Customer Balance was found for the id : ";
	private static final String NO_DEFAULT_CUSTOMER_BALANCE_FOUND = "No default Customer Balance was found";
	private static final String CUSTOMER_BALANCE_IS_MANDATORY = "Customer balance is mandatory to create a dunning settings with mode INVOICE_LEVEL";
	private static final String MANY_DEFAULT_CUSTOMER_BALANCE_FOUND = "Many Customer Balance are configured as default";
	private static final String ACTIVE_OR_PAUSED_DUNNING_COLLECTION_PLAN_FOUND = "One or many Active/Paused Dunning Collection Plan was found";

	@Override
	public List<DunningSettings> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return new ArrayList<DunningSettings>();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningSettings> findById(Long id) {
		return Optional.ofNullable(dunningSettingsService.findById(id));
	}

	@Override
	public DunningSettings create(DunningSettings baseEntity) {
		globalSettingsVerifier.checkActivateDunning();

		if(dunningSettingsService.findByCode(baseEntity.getCode()) != null) {
			throw new EntityAlreadyExistsException(DunningSettings.class, baseEntity.getCode());
		}

		//if the dunning mode is empty then select INVOICE_LEVEL by default
		//If the dunning mode is set to CUSTOMER_LEVEL for the first setting then return a functional exception
		if(baseEntity.getDunningMode() == null) {
			baseEntity.setDunningMode(DunningModeEnum.INVOICE_LEVEL);
		}

		if(baseEntity.getMaxDunningLevels() == null)
			baseEntity.setMaxDunningLevels(15);
		if(baseEntity.getAccountingArticle() != null && baseEntity.getAccountingArticle().getId() != null) {
			var accountingArticle = accountingArticleService.findById(baseEntity.getAccountingArticle().getId());
			if(accountingArticle == null)
				throw new BadRequestException(NO_ACCOUNTING_ARTICLE_FOUND + baseEntity.getAccountingArticle().getId());
			baseEntity.setAccountingArticle(accountingArticle);
		}

		//Check customer balance if not filled then get the default one, in create case we pass the second param as null (no entity exist to update)
		checkAndApplyDefaultCustomerBalance(baseEntity, null);
		
		dunningSettingsService.create(baseEntity);
		return baseEntity;
	}

	@Override
	public Optional<DunningSettings> update(Long id, DunningSettings dunningSettings) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningSettingsUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_FOUND + id));

		if(dunningSettings.getAccountingArticle() != null) {
			var accountingArticle = accountingArticleService.findById(dunningSettings.getAccountingArticle().getId());
			if(accountingArticle == null)
				throw new BadRequestException(NO_ACCOUNTING_ARTICLE_FOUND + dunningSettings.getAccountingArticle().getId());
			dunningSettingsUpdate.setAccountingArticle(accountingArticle);
		} else {
			dunningSettingsUpdate.setAccountingArticle(null);
		}

		dunningSettingsUpdate.setCode(dunningSettings.getCode());
		dunningSettingsUpdate.setAllowDunningCharges(dunningSettings.isAllowDunningCharges());
		dunningSettingsUpdate.setAllowInterestForDelay(dunningSettings.isAllowInterestForDelay());
		dunningSettingsUpdate.setApplyDunningChargeFxExchangeRate(dunningSettings.isApplyDunningChargeFxExchangeRate());
		dunningSettingsUpdate.setDunningMode(dunningSettings.getDunningMode());
		dunningSettingsUpdate.setInterestForDelayRate(dunningSettings.getInterestForDelayRate());
		dunningSettingsUpdate.setMaxDaysOutstanding(dunningSettings.getMaxDaysOutstanding());
		dunningSettingsUpdate.setMaxDunningLevels(dunningSettings.getMaxDunningLevels());
		
		List<DunningCollectionPlan> activeDunningCollectionPlans = dunningCollectionPlanService.getActiveDunningCollectionPlan(dunningSettingsUpdate.getId());
		List<DunningCollectionPlan> pausedDunningCollectionPlans = dunningCollectionPlanService.getPausedDunningCollectionPlan(dunningSettingsUpdate.getId());
		
		//Check if active and paused dunning collection are not empty
		if((activeDunningCollectionPlans != null && activeDunningCollectionPlans.size() > 0) || (pausedDunningCollectionPlans != null && pausedDunningCollectionPlans.size() > 0)) {
			throw new ForbiddenException(ACTIVE_OR_PAUSED_DUNNING_COLLECTION_PLAN_FOUND);
		}

		//Check customer balance if not filled then get the default one, in update case pass the dunning settings to update
		checkAndApplyDefaultCustomerBalance(dunningSettings, dunningSettingsUpdate);
		
		dunningSettingsService.update(dunningSettingsUpdate);
		return Optional.of(dunningSettingsUpdate);
	}

	@Override
	public Optional<DunningSettings> patch(Long id, DunningSettings baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningSettings> delete(Long id) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningSettings = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_FOUND + id));
		dunningSettingsService.remove(dunningSettings);
		return Optional.ofNullable(dunningSettings);
	}

	@Override
	public Optional<DunningSettings> findByCode(String code) {
		var dunningSettings = dunningSettingsService.findByCode(code);
		if(dunningSettings == null)
			throw new BadRequestException("No Dunning settings with code : " + code);
		return Optional.of(dunningSettings);
	}
	
	public DunningSettings duplicate(String dunningCode) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningSettings = findByCode(dunningCode).get();
		return dunningSettingsService.duplicate(dunningSettings);
	}

	/**
	 * Check customer balance if not apply the default one when dunning settings is CUSTOMER_LEVEL
	 * @param dunningSettings {@link DunningSettings}
	 * @param dunningSettingsUpdate {@link DunningSettings}
	 */
	private void checkAndApplyDefaultCustomerBalance(DunningSettings dunningSettings, DunningSettings dunningSettingsUpdate) {
		if(dunningSettings.getCustomerBalance() != null && dunningSettings.getCustomerBalance().getId() != null) {
			var customerBalance = customerBalanceService.findById(dunningSettings.getCustomerBalance().getId());

			if(customerBalance == null) {
				throw new BadRequestException(NO_CUSTOMER_BALANCE_FOUND + dunningSettings.getCustomerBalance().getId());
			} else if(dunningSettingsUpdate != null){
				dunningSettingsUpdate.setCustomerBalance(customerBalance);
			} else {
				dunningSettings.setCustomerBalance(customerBalance);
			}
		}

		//Customer Balance not selected and DunningMode is CUSTOMER_LEVEL -> Set the default CustomerBalance
		if(dunningSettings.getCustomerBalance() == null && dunningSettings.getDunningMode().equals(DunningModeEnum.CUSTOMER_LEVEL)) {
			CustomerBalance customerBalance = null;
			try {
				customerBalance = customerBalanceService.getDefaultOne();
			} catch (BusinessException e) {
				log.debug("Many Customer Balance are configured as default");
				throw new BadRequestException(MANY_DEFAULT_CUSTOMER_BALANCE_FOUND);
			}

			if(customerBalance != null) {
				if(dunningSettingsUpdate != null){
					dunningSettingsUpdate.setCustomerBalance(customerBalance);
				} else {
					dunningSettings.setCustomerBalance(customerBalance);
				}
			} else {
				log.debug("Many Customer Balance are configured as default");
				throw new BadRequestException(NO_DEFAULT_CUSTOMER_BALANCE_FOUND);
			}
		}
	}

}
