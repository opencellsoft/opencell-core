package org.meveo.apiv2.dunning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.service.DunningPaymentRetryApiService;
import org.meveo.apiv2.dunning.service.GlobalSettingsVerifier;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningPaymentRetry;
import org.meveo.model.dunning.PayRetryFrequencyUnitEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.DunningPaymentRetriesService;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.ws.rs.BadRequestException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DunningPaymentRetryApiServiceTest {

	@Spy
	@InjectMocks
	private DunningPaymentRetryApiService dunningPaymentRetryApiService;

	@Mock
	private DunningSettingsService dunningSettingsService;

	@Mock
	private DunningPaymentRetriesService dunningPaymentRetryService;

	private DunningPaymentRetry dunningPaymentRetry;

	@Mock
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Before
	public void setup() {
		dunningPaymentRetry = new DunningPaymentRetry();
		dunningPaymentRetry.setPaymentMethod(PaymentMethodEnum.CASH);
		dunningPaymentRetry.setPsp("PSP");
		dunningPaymentRetry.setNumPayRetries(2);
		dunningPaymentRetry.setPayRetryFrequencyUnit(PayRetryFrequencyUnitEnum.DAY);
		dunningPaymentRetry.setPayRetryFrequency(5);
		doNothing().when(globalSettingsVerifier).checkActivateDunning();
	}

	@Test
	public void shouldCreateNewDunningSettings() {
		doNothing().when(dunningPaymentRetryService).create(any());
		dunningPaymentRetryApiService.create(dunningPaymentRetry);
		verify(dunningPaymentRetryService, times(1)).create(any());
	}

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenNoDunningSettingExist() {
		DunningSettings dunningSettings = new DunningSettings();
		dunningSettings.setId(-1L);
		dunningPaymentRetry.setDunningSettings(dunningSettings);
		when(dunningSettingsService.findById(anyLong())).thenReturn(null);
		dunningPaymentRetryApiService.create(dunningPaymentRetry);

	}

	@Test
	public void shouldUpdateExitingDunningPaymentRetry() {
		when(dunningPaymentRetryService.findById(anyLong())).thenReturn(dunningPaymentRetry);
		var updateDunning = new DunningPaymentRetry(PaymentMethodEnum.CARD, "PSP", 3, PayRetryFrequencyUnitEnum.DAY, 5, null);
		when(dunningPaymentRetryService.update(any())).thenReturn(updateDunning);

		dunningPaymentRetryApiService.update(1L, dunningPaymentRetry);
		assertEquals("Assert payment methode", PaymentMethodEnum.CARD, updateDunning.getPaymentMethod());
	}

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
		when(dunningPaymentRetryService.findById(anyLong())).thenReturn(null);
		dunningPaymentRetryApiService.update(1L, dunningPaymentRetry);

	}

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
		when(dunningPaymentRetryService.findById(anyLong())).thenReturn(null);
		dunningPaymentRetryApiService.delete(1L);
	}

	@Test
	public void shouldReturnDeletingExitingDunningSetting() {
		when(dunningPaymentRetryService.findById(anyLong())).thenReturn(dunningPaymentRetry);
		doNothing().when(dunningPaymentRetryService).remove(dunningPaymentRetry);
		dunningPaymentRetryApiService.delete(1L);
	}

}
