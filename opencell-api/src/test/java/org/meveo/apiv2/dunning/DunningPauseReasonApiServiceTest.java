package org.meveo.apiv2.dunning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.service.DunningPauseReasonApiService;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningPauseReasons;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;
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
public class DunningPauseReasonApiServiceTest {
	
	@Spy
	@InjectMocks
	private DunningPauseReasonApiService dunningPauseReasonApiService;

	@Mock
	private DunningSettingsService dunningSettingsService;

	@Mock
	private DunningPauseReasonsService dunningPauseReasonsService;

	 private DunningPauseReasons dunningPauseReasons;

    @Before
    public void setup() {
		dunningPauseReasons = new DunningPauseReasons();
		//dunningPauseReasons.setLanguage("Language");
		dunningPauseReasons.setDescription("Description");
		dunningPauseReasons.setPauseReason("Pause reason");
    }
    
    @Test
    public void shouldCreateNewDunningSettings() {
    	doNothing().when(dunningPauseReasonsService).create(any());
		dunningPauseReasonApiService.create(dunningPauseReasons);
    	verify(dunningPauseReasonsService, times(1)).create(any());
    }

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenNoDunningSettingExist() {
		DunningSettings dunningSettings = new DunningSettings();
		dunningSettings.setId(-1L);
		dunningPauseReasons.setDunningSettings(dunningSettings);
		when(dunningSettingsService.findById(anyLong())).thenReturn(null);
		dunningPauseReasonApiService.create(dunningPauseReasons);

	}
    
    @Test
    public void shouldUpdateExitingDunningPauseReason() {
    	when(dunningPauseReasonsService.findById(anyLong())).thenReturn(dunningPauseReasons);
    	var updateDunning = new DunningPauseReasons(null, "Pause reason", "Description");
    	when(dunningPauseReasonsService.update(any())).thenReturn(updateDunning);

		dunningPauseReasonApiService.update(1L, dunningPauseReasons);
        assertEquals("Assert pause reason", "Pause reason", updateDunning.getPauseReason());
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
    	when(dunningPauseReasonsService.findById(anyLong())).thenReturn(null);
		dunningPauseReasonApiService.update(1L, dunningPauseReasons);
    	
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
    	when(dunningPauseReasonsService.findById(anyLong())).thenReturn(null);
		dunningPauseReasonApiService.delete(1L);
    }
    

    @Test
    public void shouldReturnDeletingExitingDunningSetting() {
    	when(dunningPauseReasonsService.findById(anyLong())).thenReturn(dunningPauseReasons);
    	doNothing().when(dunningPauseReasonsService).remove(dunningPauseReasons);
		dunningPauseReasonApiService.delete(1L);
    }

    
    
    
}
