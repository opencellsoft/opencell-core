package org.meveo.apiv2.dunning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.service.DunningStopReasonApiService;
import org.meveo.apiv2.dunning.service.GlobalSettingsVerifier;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningStopReason;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningStopReasonsService;
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
public class DunningStopReasonApiServiceTest {

	@Spy
	@InjectMocks
	private DunningStopReasonApiService dunningStopReasonApiService;

	@Mock
	private DunningSettingsService dunningSettingsService;

	@Mock
	private DunningStopReasonsService dunningStopReasonsService;

	private DunningStopReason dunningStopReason;

	@Mock
	private GlobalSettingsVerifier globalSettingsVerifier;

	@Before
	public void setup() {
		dunningStopReason = new DunningStopReason();
		//dunningStopReasons.setLanguage("Language");
		dunningStopReason.setDescription("Description");
		dunningStopReason.setStopReason("Stop reason");
		doNothing().when(globalSettingsVerifier).checkActivateDunning();
	}

	@Test
    public void shouldCreateNewDunningSettings() {
		doNothing().when(dunningStopReasonsService).create(any());
		dunningStopReasonApiService.create(dunningStopReason);
		verify(dunningStopReasonsService, times(1)).create(any());
	}

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenNoDunningSettingExist() {
		DunningSettings dunningSettings = new DunningSettings();
		dunningSettings.setId(-1L);
		dunningStopReason.setDunningSettings(dunningSettings);
		when(dunningSettingsService.findById(anyLong())).thenReturn(null);
		dunningStopReasonApiService.create(dunningStopReason);

	}
    
    @Test
    public void shouldUpdateExitingDunningStopReason() {
		when(dunningStopReasonsService.findById(anyLong())).thenReturn(dunningStopReason);
		var updateDunning = new DunningStopReason("Stop reason", "Description", null);
		when(dunningStopReasonsService.update(any())).thenReturn(updateDunning);

		dunningStopReasonApiService.update(1L, dunningStopReason);
		assertEquals("Assert stop reason", "Stop reason", updateDunning.getStopReason());
	}
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
		when(dunningStopReasonsService.findById(anyLong())).thenReturn(null);
		dunningStopReasonApiService.update(1L, dunningStopReason);

	}
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
    	when(dunningStopReasonsService.findById(anyLong())).thenReturn(null);
		dunningStopReasonApiService.delete(1L);
    }
    

    @Test
    public void shouldReturnDeletingExitingDunningSetting() {
		when(dunningStopReasonsService.findById(anyLong())).thenReturn(dunningStopReason);
		doNothing().when(dunningStopReasonsService).remove(dunningStopReason);
		dunningStopReasonApiService.delete(1L);
	}

    
    
    
}
