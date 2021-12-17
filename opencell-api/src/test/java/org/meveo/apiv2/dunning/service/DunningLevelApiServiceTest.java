package org.meveo.apiv2.dunning.service;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.admin.Currency;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DunningLevelApiServiceTest {

    @InjectMocks
    DunningLevelApiService dunningLevelApiService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private DunningLevelService dunningLevelService;

    @Mock
    private AuditLogService auditLogService;

    @Before
    public void setUp() {
        Currency minBalanceCurrency = new Currency();
        minBalanceCurrency.setCurrencyCode("EUR");

        when(dunningLevelService.findByCode("NEW_DL")).thenReturn(null);
        when(currencyService.findByCode("EUR")).thenReturn(minBalanceCurrency);
    }

    @Test
    public void should_create_dunningLevel() {
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1L);
        dunningLevel.setCode("NEW_DL");
        dunningLevel.setEndOfDunningLevel(TRUE);
        dunningLevel.setActive(TRUE);
        dunningLevel.setReminder(FALSE);
        dunningLevel.setDaysOverdue(10);

        DunningLevel createdEntity = dunningLevelApiService.create(dunningLevel);
        assertEquals("NEW_DL", createdEntity.getCode());
        assertEquals(1L, createdEntity.getId().longValue());
    }

    @Test(expected = InvalidParameterException.class)
    public void should_not_create_reminder_level_with_positive_dayOverDue() {
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1L);
        dunningLevel.setCode("NEW_DL");
        dunningLevel.setEndOfDunningLevel(FALSE);
        dunningLevel.setActive(TRUE);
        dunningLevel.setReminder(TRUE);
        dunningLevel.setDaysOverdue(10);

        dunningLevelApiService.create(dunningLevel);
    }

    @Test(expected = InvalidParameterException.class)
    public void should_not_create_level_if_reminder_and_endOfLevel() {
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1L);
        dunningLevel.setCode("NEW_DL");
        dunningLevel.setEndOfDunningLevel(TRUE);
        dunningLevel.setActive(TRUE);
        dunningLevel.setReminder(TRUE);
        dunningLevel.setDaysOverdue(10);

        dunningLevelApiService.create(dunningLevel);
    }

    @Test(expected = InvalidParameterException.class)
    public void should_not_create_a_non_reminder_Level_with_negative_DayOverDue() {
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1L);
        dunningLevel.setCode("NEW_DL");
        dunningLevel.setEndOfDunningLevel(FALSE);
        dunningLevel.setActive(TRUE);
        dunningLevel.setReminder(FALSE);
        dunningLevel.setDaysOverdue(-1);

        dunningLevelApiService.create(dunningLevel);
    }
}