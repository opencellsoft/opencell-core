package org.meveo.apiv2.dunning;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.dunning.service.DunningSettingsApiService;
import org.meveo.apiv2.dunning.service.DunningStopReasonApiService;
import org.meveo.model.dunning.DunningModeEnum;
import org.meveo.model.dunning.DunningSettings;
import org.meveo.model.dunning.DunningStopReasons;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.meveo.service.payments.impl.DunningStopReasonsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

	 private org.meveo.model.dunning.DunningStopReasons dunningStopReasons;

    @Before
    public void setup() {
		dunningStopReasons = new org.meveo.model.dunning.DunningStopReasons();
		//dunningStopReasons.setLanguage("Language");
		dunningStopReasons.setDescription("Description");
		dunningStopReasons.setStopReason("Stop reason");
    }
    
    @Test
    public void shouldCreateNewDunningSettings() {
    	doNothing().when(dunningStopReasonsService).create(any());
		dunningStopReasonApiService.create(dunningStopReasons);
    	verify(dunningStopReasonsService, times(1)).create(any());
    }

	@Test(expected = BadRequestException.class)
	public void shouldReturnBadRequestWhenNoDunningSettingExist() {
		DunningSettings dunningSettings = new DunningSettings();
		dunningSettings.setId(-1L);
		dunningStopReasons.setDunningSettings(dunningSettings);
		when(dunningSettingsService.findById(anyLong())).thenReturn(null);
		dunningStopReasonApiService.create(dunningStopReasons);

	}
    
    @Test
    public void shouldUpdateExitingDunningStopReason() {
    	when(dunningStopReasonsService.findById(anyLong())).thenReturn(dunningStopReasons);
    	var updateDunning = new org.meveo.model.dunning.DunningStopReasons(null, "Stop reason", "Description",null);
    	when(dunningStopReasonsService.update(any())).thenReturn(updateDunning);

		dunningStopReasonApiService.update(1L, dunningStopReasons);
        assertEquals("Assert stop reason", "Stop reason", updateDunning.getStopReason());
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
    	when(dunningStopReasonsService.findById(anyLong())).thenReturn(null);
		dunningStopReasonApiService.update(1L, dunningStopReasons);
    	
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
    	when(dunningStopReasonsService.findById(anyLong())).thenReturn(null);
		dunningStopReasonApiService.delete(1L);
    }
    

    @Test
    public void shouldReturnDeletingExitingDunningSetting() {
    	when(dunningStopReasonsService.findById(anyLong())).thenReturn(dunningStopReasons);
    	doNothing().when(dunningStopReasonsService).remove(dunningStopReasons);
		dunningStopReasonApiService.delete(1L);
    }

    
    
    
}
