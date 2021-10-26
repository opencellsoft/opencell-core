package org.meveo.apiv2.dunning.service;

import org.assertj.core.util.Lists;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningStopReasonsService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

public class DunningStopReasonApiService implements ApiService<DunningStopReason> {

	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private DunningStopReasonsService dunningStopReasonsService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	private static final String NO_DUNNING_STOP_REASON_FOUND = "No Dunning stop reason found for id : ";
	private static final String NO_DUNNING_SETTING_FOUND = "No Dunning settings was found for the id : ";
	private static final String NO_LANGUAGE_FOUND = "No Trading language was found for the id : ";

	@Override
	public List<DunningStopReason> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Lists.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningStopReason> findById(Long id) {
		return Optional.ofNullable(dunningStopReasonsService.findById(id));
	}

	@Override
	public DunningStopReason create(DunningStopReason dunningStopReason) {

		if (dunningStopReason.getDunningSettings() != null && dunningStopReason.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(dunningStopReason.getDunningSettings().getId());
			if (dunningSettings == null) {
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningStopReason.getDunningSettings().getId());
			}
			dunningStopReason.setDunningSettings(dunningSettings);
		}
		if (dunningStopReason.getLanguage() != null && dunningStopReason.getLanguage().getId() != null) {
			var language = tradingLanguageService.findById(dunningStopReason.getLanguage().getId());
			if (language == null) {
				throw new BadRequestException(NO_LANGUAGE_FOUND + dunningStopReason.getLanguage().getId());
			}
			dunningStopReason.setLanguage(language);
		}
		dunningStopReasonsService.create(dunningStopReason);
		return dunningStopReason;
	}

	@Override
	public Optional<DunningStopReason> update(Long id, DunningStopReason dunningStopReason) {
		var dunningStopReasonUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_STOP_REASON_FOUND + id));
		if (dunningStopReason.getDescription() != null) {
			dunningStopReasonUpdate.setDescription(dunningStopReason.getDescription());
		}
		if (dunningStopReason.getStopReason() != null) {
			dunningStopReasonUpdate.setStopReason(dunningStopReason.getStopReason());
		}
		if (dunningStopReason.getLanguage() != null && dunningStopReason.getLanguage().getId() != null) {
			var language = tradingLanguageService.findById(dunningStopReason.getLanguage().getId());
			if (language == null) {
				throw new BadRequestException(NO_LANGUAGE_FOUND + dunningStopReason.getLanguage().getId());
			}
			dunningStopReasonUpdate.setLanguage(language);
		}
		dunningStopReasonUpdate = dunningStopReasonsService.update(dunningStopReasonUpdate);
		return Optional.of(dunningStopReasonUpdate);
	}

	private Optional<DunningStopReason> findByCodeAndDunningSettingCode(String dunningSettingsCode, String stopReason) {
		return Optional.ofNullable(dunningStopReasonsService.findByCodeAndDunningSettingCode(dunningSettingsCode, stopReason));
	}

	@Override
	public Optional<DunningStopReason> patch(Long id, DunningStopReason baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningStopReason> delete(Long id) {
		var dunningStopReason = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_STOP_REASON_FOUND + id));
		dunningStopReasonsService.remove(dunningStopReason);
		return Optional.ofNullable(dunningStopReason);
	}

	public Optional<DunningStopReason> delete(String dunningSettingsCode, String stopReason) {
		var dunningStopReason = findByCodeAndDunningSettingCode(dunningSettingsCode, stopReason)
				.orElseThrow(() -> new BadRequestException(NO_DUNNING_STOP_REASON_FOUND + stopReason));
		dunningStopReasonsService.remove(dunningStopReason);
		return Optional.ofNullable(dunningStopReason);
	}

	@Override
	public Optional<DunningStopReason> findByCode(String code) {
		return empty();
	}

}
