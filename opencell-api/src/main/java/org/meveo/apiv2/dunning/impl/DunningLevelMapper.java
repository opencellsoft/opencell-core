package org.meveo.apiv2.dunning.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.meveo.apiv2.dunning.ImmutableDunningLevel;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.admin.Currency;
import org.meveo.model.dunning.DunningAction;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningLevelChargeTypeEnum;

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
				.dunningLevelMinBalanceCurrency(entity.getMinBalanceCurrency() == null ? null : entity.getMinBalanceCurrency().getCurrencyCode())
				.dunningLevelChargeType(entity.getChargeType() == null ? null : entity.getChargeType().name())
				.dunningLevelChargeValue(entity.getChargeValue())
				.dunningLevelChargeCurrency(entity.getChargeCurrency() == null ? null : entity.getChargeCurrency().getCurrencyCode())
				.isEndOfDunningLevel(entity.isEndOfDunningLevel())
				.dunningActions(getDunningActionsCodes(entity))
				.build();
    }

	private List<String> getDunningActionsCodes(DunningLevel entity) {
		if (entity == null || entity.getDunningActions() == null) {
			return null;
		}
		return entity.getDunningActions().stream().map(dunningAction -> dunningAction.getCode()).collect(Collectors.toList());
	}

	private List<DunningAction> getDunningActionsFromCodes(List<String> actionsCodes) {
		if (actionsCodes == null) {
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
        if (resource.getDunningLevelMinBalanceCurrency() != null && !resource.getDunningLevelMinBalanceCurrency().isEmpty()) {
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
