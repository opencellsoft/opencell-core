package org.meveo.apiv2.dunning.service;

import org.assertj.core.util.Lists;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningPauseReasons;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

public class DunningPauseReasonApiService implements ApiService<DunningPauseReasons> {
	
	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private DunningPauseReasonsService dunningPauseReasonsService;
	
	private static final String NO_DUNNING_PAUSE_REASON_FOUND = "No Dunning pause reason found for id : ";
	private static final String NO_DUNNING_SETTING_FOUND = "No Dunning settings was found for the id : ";

	@Override
	public List<DunningPauseReasons> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Lists.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningPauseReasons> findById(Long id) {
		return Optional.ofNullable(dunningPauseReasonsService.findById(id));
	}

	@Override
	public DunningPauseReasons create(DunningPauseReasons dunningPauseReason) {

		if(dunningPauseReason.getDunningSettings() != null && dunningPauseReason.getDunningSettings().getId() != null) {
			var DunningSettings = dunningSettingsService.findById(dunningPauseReason.getDunningSettings().getId());
			if(DunningSettings == null)
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningPauseReason.getDunningSettings().getId());
			dunningPauseReason.setDunningSettings(DunningSettings);
		}
		dunningPauseReasonsService.create(dunningPauseReason);
		return dunningPauseReason;
	}

	@Override
	public Optional<DunningPauseReasons> update(Long id, DunningPauseReasons dunningPauseReason) {
		var dunningPauseReasonUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_PAUSE_REASON_FOUND + id));
		if(dunningPauseReason.getLanguage() != null){
			dunningPauseReasonUpdate.setLanguage(dunningPauseReason.getLanguage());
		}
		if(dunningPauseReason.getDescription() != null){
			dunningPauseReasonUpdate.setDescription(dunningPauseReason.getDescription());
		}
		if(dunningPauseReason.getPauseReason() != null){
			dunningPauseReasonUpdate.setPauseReason(dunningPauseReason.getPauseReason());
		}

		dunningPauseReasonsService.update(dunningPauseReasonUpdate);
		return Optional.of(dunningPauseReasonUpdate);
	}

	@Override
	public Optional<DunningPauseReasons> patch(Long id, DunningPauseReasons baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningPauseReasons> delete(Long id) {
		var dunningPauseReason = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_PAUSE_REASON_FOUND + id));
		dunningPauseReasonsService.remove(dunningPauseReason);
		return Optional.ofNullable(dunningPauseReason);
	}

	@Override
	public Optional<DunningPauseReasons> findByCode(String code) {
		return empty();
	}
	
}
