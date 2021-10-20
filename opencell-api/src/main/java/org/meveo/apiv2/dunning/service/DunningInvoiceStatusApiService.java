package org.meveo.apiv2.dunning.service;

import org.assertj.core.util.Lists;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.model.dunning.DunningPauseReasons;
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
	
	private static final String NO_DUNNING_INVOICE_STATUS_FOUND = "No Dunning invoice status wa found for code : ";
	private static final String NO_DUNNING_SETTING_FOUND = "No Dunning settings was found for the code : ";

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

		if(dunningInvoiceStatus.getDunningSettings() != null && dunningInvoiceStatus.getDunningSettings().getId() != null) {
			var dunningSettings = dunningSettingsService.findById(dunningInvoiceStatus.getDunningSettings().getId());
			if(dunningSettings == null) {
				throw new BadRequestException(NO_DUNNING_SETTING_FOUND + dunningInvoiceStatus.getDunningSettings().getId());
			}
			dunningInvoiceStatus.setDunningSettings(dunningSettings);
		}
		dunningInvoiceStatusService.create(dunningInvoiceStatus);
		return dunningInvoiceStatus;
	}

	@Override
	public Optional<DunningInvoiceStatus> update(Long id, DunningInvoiceStatus dunningInvoiceStatus) {
		var dunningInvoiceStatusUpdate = findById(id).orElseThrow(() -> new BadRequestException(NO_DUNNING_INVOICE_STATUS_FOUND + id));
		if(dunningInvoiceStatus.getLanguage() != null){
			dunningInvoiceStatusUpdate.setLanguage(dunningInvoiceStatus.getLanguage());
		}
		if(dunningInvoiceStatus.getContext() != null){
			dunningInvoiceStatusUpdate.setContext(dunningInvoiceStatus.getContext());
		}
		if(dunningInvoiceStatus.getStatus() != null){
			dunningInvoiceStatusUpdate.setStatus(dunningInvoiceStatus.getStatus());
		}

		dunningInvoiceStatusService.update(dunningInvoiceStatusUpdate);
		return Optional.of(dunningInvoiceStatusUpdate);
	}

	public Optional<DunningInvoiceStatus> update(String dunningSettingsCode, String status, DunningInvoiceStatus dunningInvoiceStatus) {
		var dunningInvoiceStatusUpdate = findByCodeAndDunningSettingCode(dunningSettingsCode, status).orElseThrow(() -> new BadRequestException(NO_DUNNING_INVOICE_STATUS_FOUND + status));
		if(dunningInvoiceStatus.getLanguage() != null){
			dunningInvoiceStatusUpdate.setLanguage(dunningInvoiceStatus.getLanguage());
		}
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
