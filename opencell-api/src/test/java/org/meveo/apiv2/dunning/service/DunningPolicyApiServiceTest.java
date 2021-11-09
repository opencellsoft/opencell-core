package org.meveo.apiv2.dunning.service;

import static java.lang.Boolean.FALSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.dunning.*;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningCollectionPlanStatusService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DunningPolicyApiServiceTest {

    @Spy
    @InjectMocks
    private DunningPolicyApiService dunningPolicyApiService;

    @Mock
    private DunningPolicyService dunningPolicyService;

    @Mock
    private DunningLevelService dunningLevelService;


    @Mock
    private DunningCollectionPlanStatusService collectionPlanStatusService;

    @Mock
    private MeveoUser currentUser;

    @Mock
    private AuditLogService auditLogService;

    private DunningPolicy dunningPolicy = new DunningPolicy();

    @Before
    public void setUp() {
        dunningPolicy.setId(1L);
        dunningPolicy.setPolicyName("policy");
        dunningPolicy.setDefaultPolicy(Boolean.TRUE);
        dunningPolicy.setPolicyDescription("Description");
        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1L);
        dunningLevel.setCode("L01");
        dunningLevel.setDescription("Level 01");
        dunningLevel.setEndOfDunningLevel(Boolean.TRUE);

        DunningLevel dunningLevel1 = new DunningLevel();
        dunningLevel1.setId(2L);
        dunningLevel1.setCode("L02");
        dunningLevel1.setDescription("Level 02");
        dunningLevel1.setReminder(Boolean.TRUE);

        DunningPolicyLevel dunningPolicyLevel = new DunningPolicyLevel();
        dunningPolicyLevel.setId(1L);
        dunningPolicyLevel.setSequence(2);
        dunningPolicyLevel.setDunningLevel(dunningLevel);

        DunningCollectionPlanStatus collectionPlanStatus = new DunningCollectionPlanStatus();
        collectionPlanStatus.setId(1L);
        collectionPlanStatus.setContext(DunningCollectionPlanStatusContextEnum.FAILED_DUNNING);
        dunningPolicyLevel.setCollectionPlanStatus(collectionPlanStatus);

        DunningPolicyLevel dunningPolicyLevel1 = new DunningPolicyLevel();
        dunningPolicyLevel1.setId(2L);
        dunningPolicyLevel1.setSequence(2);
        dunningPolicyLevel1.setDunningLevel(dunningLevel1);
        dunningPolicyLevel1.setCollectionPlanStatus(collectionPlanStatus);

        List<DunningPolicyLevel> dunningPolicyLevels = Arrays.asList(dunningPolicyLevel, dunningPolicyLevel1);
        dunningPolicy.setDunningLevels(dunningPolicyLevels);
        dunningPolicy.setTotalDunningLevels(1);

        when(dunningPolicyService.update(any())).thenReturn(dunningPolicy);
        when(dunningLevelService.refreshOrRetrieve(dunningLevel)).thenReturn(dunningLevel);
        when(dunningLevelService.refreshOrRetrieve(dunningLevel1)).thenReturn(dunningLevel1);
        when(collectionPlanStatusService.refreshOrRetrieve(any(DunningCollectionPlanStatus.class)))
                .thenReturn(collectionPlanStatus);
        when(currentUser.getUserName()).thenReturn("opencell.admin");
    }

    @Test
    public void shouldUpdateDunningPolicy() {
        Optional<DunningPolicy> dunningPolicyUpdated = dunningPolicyApiService.update(1L, dunningPolicy);
        assertTrue(dunningPolicyUpdated.isPresent());
        DunningPolicy dunningPolicy1 = dunningPolicyUpdated.get();
        assertEquals(1, dunningPolicy1.getTotalDunningLevels().intValue());
    }

    @Test
    public void shouldArchiveDunningPolicy() {
        DunningPolicy policy = new DunningPolicy();
        policy.setActivePolicy(FALSE);
        policy.setId(1L);
        when(dunningPolicyService.update(any())).thenReturn(policy);
        Optional<DunningPolicy> dunningPolicyArchived = dunningPolicyApiService.archiveDunningPolicy(dunningPolicy);
        assertTrue(dunningPolicyArchived.isPresent());
        DunningPolicy dunningPolicy1 = dunningPolicyArchived.get();
        assertEquals(FALSE, dunningPolicy1.getActivePolicy());
    }
}