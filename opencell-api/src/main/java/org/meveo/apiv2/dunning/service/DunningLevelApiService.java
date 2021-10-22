package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.hibernate.collection.spi.PersistentCollection;
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
		return Optional.ofNullable(dunningLevelService.findById(id));
	}

	@Override
	public DunningLevel create(DunningLevel newDunningLevel) {
		
		if (dunningLevelService.findByCode(newDunningLevel.getCode()) != null) {
			throw new EntityAlreadyExistsException(DunningLevel.class, newDunningLevel.getCode());
		}
		
		setDefaultValues(newDunningLevel);
		validateParameters(newDunningLevel);
		dunningLevelService.create(newDunningLevel);
		return newDunningLevel;
	}

	@Override
	public Optional<DunningLevel> update(Long id, DunningLevel dunningLevel) {
		DunningLevel dunningLevelToUpdate = findById(id).orElseThrow(() -> new EntityDoesNotExistsException(DunningLevel.class, id));
		
		if (StringUtils.isNotBlank(dunningLevel.getCode())) {
			if (dunningLevelService.findByCode(dunningLevel.getCode()) != null) {
				throw new EntityAlreadyExistsException(DunningLevel.class, dunningLevel.getCode());
			}
			dunningLevelToUpdate.setCode(dunningLevel.getCode());
		}
		if (dunningLevel.getDescription() != null) {
			dunningLevelToUpdate.setDescription(dunningLevel.getDescription());
		}
		if (dunningLevel.isReminder() != null) {
			dunningLevelToUpdate.setReminder(dunningLevel.isReminder());
		}
		if (dunningLevel.isActive() != null) {
			dunningLevelToUpdate.setActive(dunningLevel.isActive());
		}
		if (dunningLevel.getDaysOverdue() != null) {
			dunningLevelToUpdate.setDaysOverdue(dunningLevel.getDaysOverdue());
		}
		if (dunningLevel.isSoftDecline() != null) {
			dunningLevelToUpdate.setSoftDecline(dunningLevel.isSoftDecline());
		}
		if (dunningLevel.getMinBalance() != null) {
			dunningLevelToUpdate.setMinBalance(dunningLevel.getMinBalance());
		}
		if (dunningLevel.getMinBalanceCurrency() != null) {
			dunningLevelToUpdate.setMinBalanceCurrency(dunningLevel.getMinBalanceCurrency());
		}
		if (dunningLevel.getChargeType() != null) {
			dunningLevelToUpdate.setChargeType(dunningLevel.getChargeType());
		}
		if (dunningLevel.getChargeValue() != null) {
			dunningLevelToUpdate.setChargeValue(dunningLevel.getChargeValue());
		}
		if (dunningLevel.getChargeValue() != null) {
			dunningLevelToUpdate.setChargeValue(dunningLevel.getChargeValue());
		}
		if (dunningLevel.getChargeCurrency() != null) {
			dunningLevelToUpdate.setChargeCurrency(dunningLevel.getChargeCurrency());
		}
		if (dunningLevel.isEndOfDunningLevel() != null) {
			dunningLevelToUpdate.setEndOfDunningLevel(dunningLevel.isEndOfDunningLevel());
		}
		if (dunningLevel.getDunningActions() != null) {
			dunningLevelToUpdate.setDunningActions(dunningLevel.getDunningActions());
		}
        
		validateParameters(dunningLevelToUpdate);
		dunningLevelToUpdate = dunningLevelService.update(dunningLevelToUpdate);
		return Optional.of(dunningLevelToUpdate);
	}

	private void validateParameters(DunningLevel baseEntity) {
		
		if (StringUtils.isBlank(baseEntity.getCode())) {
			throw new MissingParameterException("code");
		}
		if (baseEntity.getDaysOverdue() == null) {
			throw new MissingParameterException("dunningLevelDaysOverdue");
		}
		if (baseEntity.getMinBalanceCurrency() != null) {
			Currency currency = null;
			Long currencyId = baseEntity.getMinBalanceCurrency().getId();
			if(currencyId != null) {
				currency = findCurrencyByIdOrByCode(currencyId, null).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyId));
			} else {
				String currencyCode = baseEntity.getMinBalanceCurrency() == null ? null : baseEntity.getMinBalanceCurrency().getCurrencyCode();
				currency = findCurrencyByIdOrByCode(null, currencyCode).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyCode));
			}
			baseEntity.setMinBalanceCurrency(currency);
		}

		if (baseEntity.getChargeCurrency() != null) {
			Currency currency = null;
			Long currencyId = baseEntity.getChargeCurrency().getId();
			if(currencyId != null) {
				currency = findCurrencyByIdOrByCode(currencyId, null).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyId));
			} else {
				String currencyCode = baseEntity.getChargeCurrency() == null ? null : baseEntity.getChargeCurrency().getCurrencyCode();
				currency = findCurrencyByIdOrByCode(null, currencyCode).orElseThrow(() -> new EntityDoesNotExistsException(Currency.class, currencyCode));
			}
			baseEntity.setChargeCurrency(currency);
		}
		
		String chargeCurrencyCode = baseEntity.getChargeCurrency() == null ? null : baseEntity.getChargeCurrency().getCurrencyCode();
		if (chargeCurrencyCode != null) {
			Currency chargeCurrency = currencyService.findByCode(chargeCurrencyCode);
			if(chargeCurrency == null) {
				throw new EntityDoesNotExistsException(Currency.class, chargeCurrencyCode);
			}
			baseEntity.setChargeCurrency(chargeCurrency);
		}
		
		if (baseEntity.getChargeValue() != null) {
			if (baseEntity.getChargeType() == DunningLevelChargeTypeEnum.PERCENTAGE && HUNDRED.compareTo(baseEntity.getChargeValue()) < 0) {
				throw new InvalidParameterException("dunningLevelChargeValue shoud be less than or equal to 100");
			}
		}
		
		if (baseEntity.getDaysOverdue() != null) {
			if (baseEntity.isReminder() && baseEntity.getDaysOverdue() > 0) {
				throw new InvalidParameterException("dunningLevelDaysOverdue shoud be negative");
			}
			if (!baseEntity.isReminder() && baseEntity.getDaysOverdue() < 0) {
				throw new InvalidParameterException("dunningLevelDaysOverdue shoud be positive");
			}
		}
		if (!(baseEntity.getDunningActions() instanceof PersistentCollection)) {
			Optional<DunningAction> unfoundAction = baseEntity.getDunningActions().stream().filter(action -> dunningActionService.findByCode(action.getCode()) == null).findFirst();
			if (unfoundAction.isPresent()) {
				throw new EntityDoesNotExistsException(DunningAction.class, unfoundAction.get().getCode());
			}
			baseEntity.setDunningActions(baseEntity.getDunningActions().stream().map(action -> dunningActionService.findByCode(action.getCode())).collect(Collectors.toList()));
		}
	}
	
	private void setDefaultValues(DunningLevel newDunningLevel) {
		if (newDunningLevel.isActive() == null) {
			newDunningLevel.setActive(Boolean.TRUE);
		}
		if (newDunningLevel.isReminder() == null) {
			newDunningLevel.setReminder(Boolean.FALSE);
		}
		if (newDunningLevel.isSoftDecline() == null) {
			newDunningLevel.setSoftDecline(Boolean.FALSE);
		}
		if (newDunningLevel.isEndOfDunningLevel() == null) {
			newDunningLevel.setEndOfDunningLevel(Boolean.FALSE);
		}
		if (newDunningLevel.getMinBalanceCurrency() == null) {
			Currency minBalanceCurrency = new Currency();
			minBalanceCurrency.setCurrencyCode("EUR");
			newDunningLevel.setMinBalanceCurrency(minBalanceCurrency);
		}
	}

	private Optional<Currency> findCurrencyByIdOrByCode(Long id, String code) {
		if (id != null) {
			return Optional.ofNullable(currencyService.findById(id));
		}
		return Optional.ofNullable(currencyService.findByCode(code));
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
