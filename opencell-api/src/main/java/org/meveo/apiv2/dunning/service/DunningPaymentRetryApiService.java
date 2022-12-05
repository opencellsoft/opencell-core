package org.meveo.apiv2.dunning.service;

import org.meveo.admin.util.ResourceBundle;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningPaymentRetry;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.DunningPaymentRetriesService;
import org.meveo.service.payments.impl.DunningSettingsService;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

import java.util.ArrayList;

public class DunningPaymentRetryApiService implements ApiService<DunningPaymentRetry> {

	@Inject
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private DunningPaymentRetriesService dunningPaymentRetriesService;
	@Inject
	protected ResourceBundle resourceMessages;

	private static final String NO_DUNNING_STOP_REASON_FOUND = "No Dunning payment retry found for id : ";
	private static final String NO_DUNNING_SETTING_FOUND = "No Dunning settings was found for the id : ";

	@Override
	public List<DunningPaymentRetry> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return new ArrayList<DunningPaymentRetry>();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningPaymentRetry> findById(Long id) {
		return Optional.ofNullable(dunningPaymentRetriesService.findById(id));
	}

	@Override
	public DunningPaymentRetry create(DunningPaymentRetry dunningPaymentRetry) {
		globalSettingsVerifier.checkActivateDunning();
		if (dunningPaymentRetry.getDunningSettings() != null && dunningPaymentRetry.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(dunningPaymentRetry.getDunningSettings().getId());
			if (dunningSettings == null) {
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningPaymentRetry.getDunningSettings().getId());
			}
			dunningPaymentRetry.setDunningSettings(dunningSettings);
		}
		DunningPaymentRetry dunningPaymentRetryAlreadyExist = dunningPaymentRetriesService.findByPaymentMethodAndPsp(dunningPaymentRetry);
		if (dunningPaymentRetryAlreadyExist != null) {
			throw new BadRequestException(
					resourceMessages.getString("error.dunningPaymentRetries.duplication", dunningPaymentRetry.getPaymentMethod(), dunningPaymentRetry.getPsp()));

		}
		dunningPaymentRetriesService.create(dunningPaymentRetry);
		return dunningPaymentRetry;
	}

	@Override
	public Optional<DunningPaymentRetry> update(Long id, DunningPaymentRetry dunningPaymentRetry) {
		globalSettingsVerifier.checkActivateDunning();
		if (dunningPaymentRetry.getDunningSettings() != null && dunningPaymentRetry.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(dunningPaymentRetry.getDunningSettings().getId());
			if (dunningSettings == null) {
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningPaymentRetry.getDunningSettings().getId());
			}
			dunningPaymentRetry.setDunningSettings(dunningSettings);
		}
		var dunningPaymentRetryUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_STOP_REASON_FOUND + id));
		if (dunningPaymentRetry.getPaymentMethod() != null) {
			dunningPaymentRetryUpdate.setPaymentMethod(dunningPaymentRetry.getPaymentMethod());
		}
		if (dunningPaymentRetry.getPsp() != null) {
			dunningPaymentRetryUpdate.setPsp(dunningPaymentRetry.getPsp());
		}
		if (dunningPaymentRetry.getNumPayRetries() != null) {
			dunningPaymentRetryUpdate.setNumPayRetries(dunningPaymentRetry.getNumPayRetries());
		}
		if (dunningPaymentRetry.getPayRetryFrequencyUnit() != null) {
			dunningPaymentRetryUpdate.setPayRetryFrequencyUnit(dunningPaymentRetry.getPayRetryFrequencyUnit());
		}
		if (dunningPaymentRetry.getPayRetryFrequency() != null) {
			dunningPaymentRetryUpdate.setPayRetryFrequency(dunningPaymentRetry.getPayRetryFrequency());
		}
		DunningPaymentRetry dunningPaymentRetryAlreadyExist = dunningPaymentRetriesService.findByPaymentMethodAndPsp(dunningPaymentRetry);
		if (dunningPaymentRetryAlreadyExist != null && !dunningPaymentRetryAlreadyExist.getId().equals(dunningPaymentRetryUpdate.getId())) {
			throw new BadRequestException(
					resourceMessages.getString("error.dunningPaymentRetries.duplication", dunningPaymentRetry.getPaymentMethod(), dunningPaymentRetry.getPsp()));

		}
		dunningPaymentRetriesService.update(dunningPaymentRetryUpdate);
		return Optional.of(dunningPaymentRetryUpdate);
	}

	@Override
	public Optional<DunningPaymentRetry> patch(Long id, DunningPaymentRetry baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningPaymentRetry> delete(Long id) {
		globalSettingsVerifier.checkActivateDunning();
		var dunningPaymentRetry = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_STOP_REASON_FOUND + id));
		dunningPaymentRetriesService.remove(dunningPaymentRetry);
		return Optional.ofNullable(dunningPaymentRetry);
	}

	@Override
	public Optional<DunningPaymentRetry> findByCode(String code) {
		return empty();
	}

}
