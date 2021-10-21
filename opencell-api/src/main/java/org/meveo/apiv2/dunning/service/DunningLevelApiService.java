package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Currency;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningLevelChargeTypeEnum;
import org.meveo.model.payments.DunningAction;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.payments.impl.DunningActionService;
import org.meveo.service.payments.impl.DunningLevelService;

public class DunningLevelApiService implements ApiService<DunningLevel> {

	@Inject
	private CurrencyService currencyService;

	@Inject
	private DunningLevelService dunningLevelService;

	@Inject
	private DunningActionService dunningActionService;

	private static final BigDecimal HUNDRED = new BigDecimal(100);

	@Override
	public List<DunningLevel> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Lists.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningLevel> findById(Long id) {
		return empty();
	}

	@Override
	public DunningLevel create(DunningLevel baseEntity) {

		if (StringUtils.isBlank(baseEntity.getCode())) {
			throw new MissingParameterException("code");
		}
		if (baseEntity.getDaysOverdue() == null) {
			throw new MissingParameterException("dunningLevelDaysOverdue");
		}
		if (dunningLevelService.findByCode(baseEntity.getCode()) != null) {
			throw new EntityAlreadyExistsException(DunningLevel.class, baseEntity.getCode());
		}
		String minBalanceCurrencyCode = baseEntity.getMinBalanceCurrency() == null ? null : baseEntity.getMinBalanceCurrency().getCurrencyCode();
		Currency minBalanceCurrency = currencyService.findByCode(minBalanceCurrencyCode);
		if (minBalanceCurrencyCode != null && minBalanceCurrency == null) {
			throw new EntityDoesNotExistsException(Currency.class, minBalanceCurrencyCode);
		}
		baseEntity.setMinBalanceCurrency(minBalanceCurrency);

		String chargeCurrencyCode = baseEntity.getChargeCurrency() == null ? null : baseEntity.getChargeCurrency().getCurrencyCode();
		Currency chargeCurrency = currencyService.findByCode(chargeCurrencyCode);
		if (chargeCurrencyCode != null && chargeCurrency == null) {
			throw new EntityDoesNotExistsException(Currency.class, chargeCurrencyCode);
		}
		baseEntity.setChargeCurrency(chargeCurrency);

		if (baseEntity.getChargeType() == DunningLevelChargeTypeEnum.PERCENTAGE && HUNDRED.compareTo(baseEntity.getChargeValue()) < 0) {
			throw new InvalidParameterException("dunningLevelChargeValue shoud be less than or equal to 100");
		}
		if (baseEntity.getChargeType() == DunningLevelChargeTypeEnum.PERCENTAGE && HUNDRED.compareTo(baseEntity.getChargeValue()) < 0) {
			throw new InvalidParameterException("dunningLevelChargeValue shoud be less than or equal to 100");
		}
		if (baseEntity.isReminder() && baseEntity.getDaysOverdue() > 0) {
			throw new InvalidParameterException("dunningLevelDaysOverdue shoud be negative");
		}
		if (!baseEntity.isReminder() && baseEntity.getDaysOverdue() < 0) {
			throw new InvalidParameterException("dunningLevelDaysOverdue shoud be positive");
		}
		if (baseEntity.getDunningActions() != null) {
			Optional<DunningAction> unfoundAction = baseEntity.getDunningActions().stream().filter(action -> dunningActionService.findByCode(action.getCode()) == null).findFirst();
			if (unfoundAction.isPresent()) {
				throw new EntityDoesNotExistsException(DunningAction.class, unfoundAction.get().getCode());
			}
			baseEntity.setDunningActions(baseEntity.getDunningActions().stream().map(action -> dunningActionService.findByCode(action.getCode())).collect(Collectors.toList()));
		}
		
		dunningLevelService.create(baseEntity);
		return baseEntity;
	}

	@Override
	public Optional<DunningLevel> update(Long id, DunningLevel dunningLevel) {
		return empty();
	}

	@Override
	public Optional<DunningLevel> patch(Long id, DunningLevel baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningLevel> delete(Long id) {
		return empty();
	}

	@Override
	public Optional<DunningLevel> findByCode(String code) {
		return empty();
	}

}
