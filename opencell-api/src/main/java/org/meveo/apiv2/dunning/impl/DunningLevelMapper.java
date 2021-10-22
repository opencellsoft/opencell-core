package org.meveo.apiv2.dunning.impl;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.apiv2.dunning.ImmutableDunningLevel;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.admin.Currency;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningLevelChargeTypeEnum;
import org.meveo.model.payments.DunningAction;

public class DunningLevelMapper extends ResourceMapper<org.meveo.apiv2.dunning.DunningLevel, DunningLevel> {

	@Override
	protected org.meveo.apiv2.dunning.DunningLevel toResource(DunningLevel entity) {
		return ImmutableDunningLevel.builder()
				.id(entity.getId())
				.code(entity.getCode())
				.description(entity.getDescription())
				.isReminderLevel(entity.isReminder())
				.isActiveLevel(entity.isActive())
				.dunningLevelDaysOverdue(entity.getDaysOverdue())
				.isSoftDeclineLevel(entity.isSoftDecline())
				.dunningLevelMinBalance(entity.getMinBalance())
				.dunningLevelMinBalanceCurrency(entity.getMinBalanceCurrency() == null || entity.getMinBalanceCurrency() instanceof HibernateProxy ? "" : entity.getMinBalanceCurrency().getCurrencyCode())
				.dunningLevelChargeType(entity.getChargeType() == null ? null : entity.getChargeType().name())
				.dunningLevelChargeValue(entity.getChargeValue())
				.dunningLevelChargeCurrency(entity.getChargeCurrency() == null || entity.getChargeCurrency() instanceof HibernateProxy ? "" : entity.getChargeCurrency().getCurrencyCode())
				.isEndOfDunningLevel(entity.isEndOfDunningLevel())
				.dunningActions(getDunningActionsCodes(entity))
				.build();
    }

	List<String> getDunningActionsCodes(DunningLevel entity) {
		if (entity == null || entity.getDunningActions() == null ||  entity.getDunningActions() instanceof PersistentCollection) {
			return null;
		}
		return entity.getDunningActions().stream().map(dunningAction -> dunningAction.getCode()).collect(Collectors.toList());
	}

	List<DunningAction> getDunningActionsFromCodes(List<String> actionsCodes) {
		if (actionsCodes == null || actionsCodes.isEmpty()) {
			return null;
		}

		return actionsCodes.stream().map(code -> {
			DunningAction action = new DunningAction();
			action.setCode(code);
			return action;
		}).collect(Collectors.toList());
	}

    @Override
    protected DunningLevel toEntity(org.meveo.apiv2.dunning.DunningLevel resource) {
        var entity = new DunningLevel();
        if (resource.getDunningLevelChargeCurrency() != null) {
            var chargeCurrency = new Currency();
            chargeCurrency.setCurrencyCode(resource.getDunningLevelChargeCurrency());
            entity.setChargeCurrency(chargeCurrency);
        }
        if (resource.getDunningLevelMinBalanceCurrency() != null) {
        	var minBalanceCurrency = new Currency();
        	minBalanceCurrency.setCurrencyCode(resource.getDunningLevelMinBalanceCurrency());
        	entity.setMinBalanceCurrency(minBalanceCurrency);
        }
        entity.setCode(resource.getCode());
        entity.setDescription(resource.getDescription());
        entity.setReminder(resource.isReminderLevel());
        entity.setActive(resource.isActiveLevel());
        entity.setDaysOverdue(resource.getDunningLevelDaysOverdue());
        entity.setSoftDecline(resource.isSoftDeclineLevel());
        entity.setMinBalance(resource.getDunningLevelMinBalance());
        entity.setChargeType(resource.getDunningLevelChargeType() == null ? null : DunningLevelChargeTypeEnum.valueOf(resource.getDunningLevelChargeType()));
        entity.setChargeValue(resource.getDunningLevelChargeValue());
        entity.setEndOfDunningLevel(resource.isEndOfDunningLevel());
        entity.setDunningActions(getDunningActionsFromCodes(resource.getDunningActions()));
        entity.setId(resource.getId());
        return entity;
    }
}
