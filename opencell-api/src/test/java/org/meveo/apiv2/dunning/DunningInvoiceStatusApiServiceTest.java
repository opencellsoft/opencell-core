package org.meveo.apiv2.dunning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.service.DunningInvoiceStatusApiService;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningInvoiceStatusService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DunningInvoiceStatusApiServiceTest {
	
	@Spy
	@InjectMocks
	private DunningInvoiceStatusApiService dunningInvoiceStatusApiService;

	@Mock
	private DunningSettingsService dunningSettingsService;

	@Mock
	private DunningInvoiceStatusService dunningInvoiceStatusService;

	 private DunningInvoiceStatus dunningInvoiceStatus;

    @Before
    public void setup() {
		dunningInvoiceStatus = new DunningInvoiceStatus();
		dunningInvoiceStatus.setLanguage("Language");
		dunningInvoiceStatus.setContext("Context");
		dunningInvoiceStatus.setStatus("Status");
    }
    
    @Test
    public void shouldCreateNewDunningSettings() {
    	doNothing().when(dunningInvoiceStatusService).create(any());
		dunningInvoiceStatusApiService.create(dunningInvoiceStatus);
    	verify(dunningInvoiceStatusService, times(1)).create(any());
    }

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenNoDunningSettingExist() {
		DunningSettings dunningSettings = new DunningSettings();
		dunningSettings.setId(-1L);
		dunningInvoiceStatus.setDunningSettings(dunningSettings);
		when(dunningSettingsService.findById(anyLong())).thenReturn(null);
		dunningInvoiceStatusApiService.create(dunningInvoiceStatus);

	}
    
    @Test
    public void shouldUpdateExitingDunningInvoiceStatus() {
    	when(dunningInvoiceStatusService.findById(anyLong())).thenReturn(dunningInvoiceStatus);
    	var updateDunning = new DunningInvoiceStatus("Lang", "Status", "Context",null);
    	when(dunningInvoiceStatusService.update(any())).thenReturn(updateDunning);

		dunningInvoiceStatusApiService.update(1L, dunningInvoiceStatus);
        assertEquals("Assert status", "Status", updateDunning.getStatus());
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
    	when(dunningInvoiceStatusService.findById(anyLong())).thenReturn(null);
		dunningInvoiceStatusApiService.update(1L, dunningInvoiceStatus);
    	
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
    	when(dunningInvoiceStatusService.findById(anyLong())).thenReturn(null);
		dunningInvoiceStatusApiService.delete(1L);
    }
    

    @Test
    public void shouldReturnDeletingExitingDunningSetting() {
    	when(dunningInvoiceStatusService.findById(anyLong())).thenReturn(dunningInvoiceStatus);
    	doNothing().when(dunningInvoiceStatusService).remove(dunningInvoiceStatus);
		dunningInvoiceStatusApiService.delete(1L);
    }

    
    
    
}
