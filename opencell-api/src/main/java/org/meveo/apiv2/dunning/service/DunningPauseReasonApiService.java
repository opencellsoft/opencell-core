package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;
import org.meveo.service.payments.impl.DunningSettingsService;

public class DunningPauseReasonApiService implements ApiService<DunningPauseReason> {

	@Inject
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private DunningPauseReasonsService dunningPauseReasonsService;
	@Inject
	private TradingLanguageService tradingLanguageService;

	private static final String NO_DUNNING_PAUSE_REASON_FOUND = "No Dunning pause reason found for id : ";
	private static final String NO_DUNNING_SETTING_FOUND = "No Dunning settings was found for the id : ";

	@Override
	public List<DunningPauseReason> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return new ArrayList<DunningPauseReason>();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningPauseReason> findById(Long id) {
		return Optional.ofNullable(dunningPauseReasonsService.findById(id));
	}

	@Override
	public DunningPauseReason create(DunningPauseReason dunningPauseReason) {
		globalSettingsVerifier.checkActivateDunning();
		if (dunningPauseReason.getDunningSettings() != null && dunningPauseReason.getDunningSettings().getId() != null) {
			var DunningSettings = dunningSettingsService.findById(dunningPauseReason.getDunningSettings().getId());
			if (DunningSettings == null) {
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningPauseReason.getDunningSettings().getId());
			}
			dunningPauseReason.setDunningSettings(DunningSettings);
		}
		dunningPauseReasonsService.create(dunningPauseReason);
		return dunningPauseReason;
	}

	@Override
	public Optional<DunningPauseReason> update(Long id, DunningPauseReason dunningPauseReason) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningPauseReasonUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_PAUSE_REASON_FOUND + id));
		if (dunningPauseReason.getDescription() != null) {
			dunningPauseReasonUpdate.setDescription(dunningPauseReason.getDescription());
		}
		if (dunningPauseReason.getPauseReason() != null) {
			dunningPauseReasonUpdate.setPauseReason(dunningPauseReason.getPauseReason());
		}
		dunningPauseReasonsService.update(dunningPauseReasonUpdate);
		return Optional.of(dunningPauseReasonUpdate);
	}

	public Optional<DunningPauseReason> update(String dunningSettingsCode, String pauseReason, DunningPauseReason dunningPauseReason) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningPauseReasonUpdate = findByCodeAndDunningSettingCode(dunningSettingsCode, pauseReason)
				.orElseThrow(() -> new BadRequestException(NO_DUNNING_PAUSE_REASON_FOUND + pauseReason));
		if (dunningPauseReason.getDescription() != null) {
			dunningPauseReasonUpdate.setDescription(dunningPauseReason.getDescription());
		}
		if (dunningPauseReason.getPauseReason() != null) {
			dunningPauseReasonUpdate.setPauseReason(dunningPauseReason.getPauseReason());
		}

		dunningPauseReasonsService.update(dunningPauseReasonUpdate);
		return Optional.of(dunningPauseReasonUpdate);
	}

	@Override
	public Optional<DunningPauseReason> patch(Long id, DunningPauseReason baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningPauseReason> delete(Long id) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningPauseReason = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_PAUSE_REASON_FOUND + id));
		dunningPauseReasonsService.remove(dunningPauseReason);
		return Optional.ofNullable(dunningPauseReason);
	}

	@Override
	public Optional<DunningPauseReason> findByCode(String code) {
		return empty();
	}

	public Optional<DunningPauseReason> delete(String dunningSettingsCode, String pauseReason) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningPauseReason = findByCodeAndDunningSettingCode(dunningSettingsCode, pauseReason)
				.orElseThrow(() -> new BadRequestException(NO_DUNNING_PAUSE_REASON_FOUND + pauseReason));
		dunningPauseReasonsService.remove(dunningPauseReason);
		return Optional.ofNullable(dunningPauseReason);

	}

	private Optional<DunningPauseReason> findByCodeAndDunningSettingCode(String dunningSettingsCode, String stopReason) {
		return Optional.ofNullable(dunningPauseReasonsService.findByCodeAndDunningSettingCode(dunningSettingsCode, stopReason));
	}
}
