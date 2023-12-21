package org.meveo.apiv2.dunning;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.ws.rs.BadRequestException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.dunning.service.DunningSettingsApiService;
import org.meveo.apiv2.dunning.service.GlobalSettingsVerifier;
import org.meveo.model.dunning.DunningModeEnum;
import org.meveo.model.payments.CustomerBalance;
import org.meveo.service.payments.impl.CustomerBalanceService;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DunningSettingsApiServiceTest {
	
	@Spy
	@InjectMocks
	private DunningSettingsApiService dunningSettingsApiService;
	@Mock
	private DunningSettingsService dunningSettingsService;
	@Mock
	private DunningCollectionPlanService dunningCollectionPlanService;
	@Mock
	private CustomerBalanceService customerBalanceService;

	org.meveo.model.dunning.DunningSettings dunningSettings;
	
	CustomerBalance customerBalance;

	@Mock
	private GlobalSettingsVerifier globalSettingsVerifier;

    @Before
    public void setup() {
    	dunningSettings = new org.meveo.model.dunning.DunningSettings();
    	dunningSettings.setAllowDunningCharges(false);
    	dunningSettings.setAllowInterestForDelay(true);
    	dunningSettings.setApplyDunningChargeFxExchangeRate(true);
    	dunningSettings.setCode("CODD");
    	dunningSettings.setDunningMode(DunningModeEnum.CUSTOMER_LEVEL);
    	customerBalance = new CustomerBalance();
    	customerBalance.setId(1L);
    	customerBalance.setCode("CUSTOMER_BALANCE_1");
    	customerBalance.setDefaultBalance(true);
		doNothing().when(globalSettingsVerifier).checkActivateDunning();
	}
    
    @Test
    public void shouldCreateNewDunningSettings() {
    	doNothing().when(dunningSettingsService).create(any());
    	dunningSettingsService.create(dunningSettings);
    	verify(dunningSettingsService, times(1)).create(any());
    }
    
    @Test(expected = EntityAlreadyExistsException.class)
    public void shouldReturnEntityAlreadyExistsException() {
    	when(dunningSettingsService.findByCode(anyString())).thenReturn(dunningSettings);
    	dunningSettingsApiService.create(dunningSettings);
    }
    
    @Test
    public void shouldUpdateExitingDunningSetting() {
    	when(dunningSettingsService.findById(anyLong())).thenReturn(dunningSettings);
    	var updateDunning = new org.meveo.model.dunning.DunningSettings(DunningModeEnum.INVOICE_LEVEL, 20, 18, false, BigDecimal.ONE, false, true, null);
    	when(dunningSettingsService.update(any())).thenReturn(updateDunning);
    	when(dunningCollectionPlanService.getActiveDunningCollectionPlan(any())).thenReturn(new ArrayList<>());
    	when(customerBalanceService.getDefaultOne()).thenReturn(customerBalance);
    	
    	dunningSettingsApiService.update(1L, dunningSettings);
    	
        assertEquals("Dunning mode must be INVOICE_LEVEL", DunningModeEnum.INVOICE_LEVEL, updateDunning.getDunningMode());
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenUpdateExitingDunningSetting() {
    	when(dunningSettingsService.findById(anyLong())).thenReturn(null);
    	dunningSettingsApiService.update(1L, dunningSettings);
    	
    }
    

    @Test(expected = BadRequestException.class)
    public void shouldReturnBadRequestWhenDeletingExitingDunningSetting() {
    	when(dunningSettingsService.findById(anyLong())).thenReturn(null);
    	dunningSettingsApiService.delete(1L);
    }
    


    @Test
    public void shouldReturnDeletingExitingDunningSetting() {
    	when(dunningSettingsService.findById(anyLong())).thenReturn(dunningSettings);
    	doNothing().when(dunningSettingsService).remove(dunningSettings);
    	dunningSettingsApiService.delete(1L);
    }
    
    
    
    
    
}
