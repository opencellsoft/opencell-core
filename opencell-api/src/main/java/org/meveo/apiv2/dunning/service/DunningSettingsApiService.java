package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningModeEnum;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class DunningSettingsApiService implements ApiService<DunningSettings> {
	@Inject
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private AccountingArticleService accountingArticleService;
	
	private static final String No_DUNNING_FOUND = "No Dunning found for id : ";
	private static final String NO_ACCOUNTING_ARTICLE_FOUND = "No Accounting article was found for the id : ";

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
		if(dunningSettingsService.findByCode(baseEntity.getCode()) != null)
			throw new EntityAlreadyExistsException(DunningSettings.class, baseEntity.getCode());
		if(baseEntity.getDunningMode() == null)
			baseEntity.setDunningMode(DunningModeEnum.INVOICE_LEVEL);
		if(baseEntity.getMaxDunningLevels() == null)
			baseEntity.setMaxDunningLevels(15);
		if(baseEntity.getAccountingArticle() != null && baseEntity.getAccountingArticle().getId() != null) {
			var accountingArticle = accountingArticleService.findById(baseEntity.getAccountingArticle().getId());
			if(accountingArticle == null)
				throw new BadRequestException(NO_ACCOUNTING_ARTICLE_FOUND + baseEntity.getAccountingArticle().getId());
			baseEntity.setAccountingArticle(accountingArticle);
		}
		dunningSettingsService.create(baseEntity);
		return baseEntity;
	}

	@Override
	public Optional<DunningSettings> update(Long id, DunningSettings dunningSettings) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningSettingsUpdate = findById(id).orElseThrow(() -> new BadRequestException(No_DUNNING_FOUND + id));
		if(dunningSettings.getAccountingArticle() != null) {
			var accountingArticle = accountingArticleService.findById(dunningSettings.getAccountingArticle().getId());
			if(accountingArticle == null)
				throw new BadRequestException(NO_ACCOUNTING_ARTICLE_FOUND + dunningSettings.getAccountingArticle().getId());
			dunningSettingsUpdate.setAccountingArticle(accountingArticle);
		}else
			dunningSettingsUpdate.setAccountingArticle(null);
		dunningSettingsUpdate.setCode(dunningSettings.getCode());
		dunningSettingsUpdate.setAllowDunningCharges(dunningSettings.isAllowDunningCharges());
		dunningSettingsUpdate.setAllowInterestForDelay(dunningSettings.isAllowInterestForDelay());
		dunningSettingsUpdate.setApplyDunningChargeFxExchangeRate(dunningSettings.isApplyDunningChargeFxExchangeRate());
		dunningSettingsUpdate.setDunningMode(dunningSettings.getDunningMode());
		dunningSettingsUpdate.setInterestForDelayRate(dunningSettings.getInterestForDelayRate());
		dunningSettingsUpdate.setMaxDaysOutstanding(dunningSettings.getMaxDaysOutstanding());
		dunningSettingsUpdate.setMaxDunningLevels(dunningSettings.getMaxDunningLevels());
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
		var dunningSettings = findById(id).orElseThrow(() -> new BadRequestException(No_DUNNING_FOUND + id));
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

}
