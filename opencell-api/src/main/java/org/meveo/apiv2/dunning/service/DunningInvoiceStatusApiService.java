package org.meveo.apiv2.dunning.service;

import org.assertj.core.util.Lists;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningInvoiceStatusContextEnum;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.payments.impl.DunningInvoiceStatusService;
import org.meveo.service.payments.impl.DunningSettingsService;


import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

public class DunningInvoiceStatusApiService implements ApiService<DunningInvoiceStatus> {

	@Inject
	private DunningSettingsService dunningSettingsService;
	@Inject
	private DunningInvoiceStatusService dunningInvoiceStatusService;
	@Inject
	private TradingLanguageService tradingLanguageService;

	private static final String NO_DUNNING_INVOICE_STATUS_FOUND = "No Dunning invoice status wa found for id : ";
	private static final String NO_DUNNING_SETTING_FOUND = "No Dunning settings was found for the id : ";
	private static final String NO_LANGUAGE_FOUND = "No Trading language was found for the id : ";

	@Override
	public List<DunningInvoiceStatus> list(Long offset, Long limit, String sort, String orderBy, String filter) {
		return Lists.emptyList();
	}

	@Override
	public Long getCount(String filter) {
		return null;
	}

	@Override
	public Optional<DunningInvoiceStatus> findById(Long id) {
		return Optional.ofNullable(dunningInvoiceStatusService.findById(id));
	}

	@Override
	public DunningInvoiceStatus create(DunningInvoiceStatus dunningInvoiceStatus) {
		if (!isValidDunningInvoiceStatus(dunningInvoiceStatus, "create")) {
			throw new BadRequestException(
					"The status " + dunningInvoiceStatus.getStatus() + " already exists for the context " + dunningInvoiceStatus.getContext() + " and the language "
							+ dunningInvoiceStatus.getLanguage().getId());
		}

		if (dunningInvoiceStatus.getDunningSettings() != null && dunningInvoiceStatus.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(dunningInvoiceStatus.getDunningSettings().getId());
			if (dunningSettings == null) {
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningInvoiceStatus.getDunningSettings().getId());
			}
			dunningInvoiceStatus.setDunningSettings(dunningSettings);
		}
		if (dunningInvoiceStatus.getLanguage() != null && dunningInvoiceStatus.getLanguage().getId() != null) {
			var language = tradingLanguageService.findById(dunningInvoiceStatus.getLanguage().getId());
			if (language == null) {
				throw new BadRequestException(NO_LANGUAGE_FOUND + dunningInvoiceStatus.getLanguage().getId());
			}
			dunningInvoiceStatus.setLanguage(language);
		}
		dunningInvoiceStatusService.create(dunningInvoiceStatus);
		return dunningInvoiceStatus;
	}

	private boolean isValidDunningInvoiceStatus(DunningInvoiceStatus dunningInvoiceStatus, String action) {
		if (dunningInvoiceStatus.getContext().equals(DunningInvoiceStatusContextEnum.PAUSED_DUNNING) || dunningInvoiceStatus.getContext()
				.equals(DunningInvoiceStatusContextEnum.STOPPED_DUNNING) || dunningInvoiceStatus.getContext().equals(DunningInvoiceStatusContextEnum.EXCLUDED_FROM_DUNNING)) {
			List<DunningInvoiceStatus> dunningInvoiceStatusList = dunningInvoiceStatusService.findByStatusAndLanguage(dunningInvoiceStatus);
			if ("create".equals(action)) {
				return dunningInvoiceStatusList.isEmpty();
			} else {
				return dunningInvoiceStatusList.size() <= 1;
			}
		}
		return true;
	}

	@Override
	public Optional<DunningInvoiceStatus> update(Long id, DunningInvoiceStatus dunningInvoiceStatus) {
		if (!isValidDunningInvoiceStatus(dunningInvoiceStatus, "update")) {
			throw new BadRequestException(
					"The status " + dunningInvoiceStatus.getStatus() + " already exists for the context " + dunningInvoiceStatus.getContext() + " and the language "
							+ dunningInvoiceStatus.getLanguage().getId());
		}
		var dunningInvoiceStatusUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_INVOICE_STATUS_FOUND + id));
		if (dunningInvoiceStatus.getContext() != null) {
			dunningInvoiceStatusUpdate.setContext(dunningInvoiceStatus.getContext());
		}
		if (dunningInvoiceStatus.getStatus() != null) {
			dunningInvoiceStatusUpdate.setStatus(dunningInvoiceStatus.getStatus());
		}
		if (dunningInvoiceStatus.getLanguage() != null && dunningInvoiceStatus.getLanguage().getId() != null) {
			var language = tradingLanguageService.findById(dunningInvoiceStatus.getLanguage().getId());
			if (language == null) {
				throw new BadRequestException(NO_LANGUAGE_FOUND + dunningInvoiceStatus.getLanguage().getId());
			}
			dunningInvoiceStatus.setLanguage(language);
		}

		dunningInvoiceStatusService.update(dunningInvoiceStatusUpdate);
		return Optional.of(dunningInvoiceStatusUpdate);
	}

	public Optional<DunningInvoiceStatus> update(String dunningSettingsCode, String status, DunningInvoiceStatus dunningInvoiceStatus) {
		var dunningInvoiceStatusUpdate = findByCodeAndDunningSettingCode(dunningSettingsCode, status).orElseThrow(() -> new BadRequestException(NO_DUNNING_INVOICE_STATUS_FOUND + status));
		if(dunningInvoiceStatus.getContext() != null){
			dunningInvoiceStatusUpdate.setContext(dunningInvoiceStatus.getContext());
		}
		if(dunningInvoiceStatus.getStatus() != null){
			dunningInvoiceStatusUpdate.setStatus(dunningInvoiceStatus.getStatus());
		}

		dunningInvoiceStatusService.update(dunningInvoiceStatusUpdate);
		return Optional.of(dunningInvoiceStatusUpdate);
	}

	@Override
	public Optional<DunningInvoiceStatus> patch(Long id, DunningInvoiceStatus baseEntity) {
		return empty();
	}

	@Override
	public Optional<DunningInvoiceStatus> delete(Long id) {
		var dunningInvoiceStatus = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_INVOICE_STATUS_FOUND + id));
		dunningInvoiceStatusService.remove(dunningInvoiceStatus);
		return Optional.ofNullable(dunningInvoiceStatus);
	}

	public Optional<DunningInvoiceStatus> delete(String dunningSettingsCode, String status) {
		var dunningInvoiceStatus = findByCodeAndDunningSettingCode(dunningSettingsCode, status).orElseThrow(() -> new BadRequestException(NO_DUNNING_INVOICE_STATUS_FOUND + status));
		dunningInvoiceStatusService.remove(dunningInvoiceStatus);
		return Optional.ofNullable(dunningInvoiceStatus);
	}

	@Override
	public Optional<DunningInvoiceStatus> findByCode(String code) {
		return empty();
	}

	private Optional<DunningInvoiceStatus> findByCodeAndDunningSettingCode(String dunningSettingsCode, String status) {
		return Optional.ofNullable(dunningInvoiceStatusService.findByCodeAndDunningSettingCode(dunningSettingsCode, status));
	}
}
