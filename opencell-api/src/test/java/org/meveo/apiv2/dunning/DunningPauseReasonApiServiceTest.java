package org.meveo.apiv2.dunning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.service.DunningPauseReasonApiService;
import org.meveo.apiv2.dunning.service.GlobalSettingsVerifier;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.settings.GlobalSettings;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;
import org.meveo.service.settings.impl.GlobalSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DunningPauseReasonApiServiceTest {

	@Spy
	@InjectMocks
	private DunningPauseReasonApiService dunningPauseReasonApiService;

	@Mock
	private DunningSettingsService dunningSettingsService;

	@Mock
	private DunningPauseReasonsService dunningPauseReasonsService;

	@Mock
	private GlobalSettingsVerifier globalSettingsVerifier;

	private DunningPauseReason dunningPauseReason;

	@Before
	public void setup() {
		dunningPauseReason = new DunningPauseReason();
		//dunningPauseReasons.setLanguage("Language");
		dunningPauseReason.setDescription("Description");
		dunningPauseReason.setPauseReason("Pause reason");
		doNothing().when(globalSettingsVerifier).checkActivateDunning();
	}

	@Test
    public void shouldCreateNewDunningSettings() {
		doNothing().when(dunningPauseReasonsService).create(any());
		dunningPauseReasonApiService.create(dunningPauseReason);
		verify(dunningPauseReasonsService, times(1)).create(any());
	}

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenNoDunningSettingExist() {
		DunningSettings dunningSettings = new DunningSettings();
		dunningSettings.setId(-1L);
		dunningPauseReason.setDunningSettings(dunningSettings);
		when(dunningSettingsService.findById(anyLong())).thenReturn(null);
		dunningPauseReasonApiService.create(dunningPauseReason);

	}
    
    @Test
    public void shouldUpdateExitingDunningPauseReason() {
		when(dunningPauseReasonsService.findById(anyLong())).thenReturn(dunningPauseReason);
		var updateDunning = new DunningPauseReason("Pause reason", "Description");
		when(dunningPauseReasonsService.update(any())).thenReturn(updateDunning);

		dunningPauseReasonApiService.update(1L, dunningPauseReason);
		assertEquals("Assert pause reason", "Pause reason", updateDunning.getPauseReason());
	}
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
		when(dunningPauseReasonsService.findById(anyLong())).thenReturn(null);
		dunningPauseReasonApiService.update(1L, dunningPauseReason);

	}
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
    	when(dunningPauseReasonsService.findById(anyLong())).thenReturn(null);
		dunningPauseReasonApiService.delete(1L);
    }
    

    @Test
    public void shouldReturnDeletingExitingDunningSetting() {
		when(dunningPauseReasonsService.findById(anyLong())).thenReturn(dunningPauseReason);
		doNothing().when(dunningPauseReasonsService).remove(dunningPauseReason);
		dunningPauseReasonApiService.delete(1L);
	}

    
    
    
}
