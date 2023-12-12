package org.meveo.apiv2.dunning.impl;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.DunningPolicyLevel;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;
import org.meveo.apiv2.dunning.ImmutableDunningPolicyInput;
import org.meveo.apiv2.dunning.ImmutableDunningPolicyLevel;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.payments.impl.DunningLevelService;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.meveo.service.payments.impl.DunningSettingsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DunningPolicyResourceImplTest {

    @Spy
    @InjectMocks
    private DunningPolicyResourceImpl dunningPolicyResource;

    @Mock
    private DunningPolicyApiService dunningPolicyApiService;

    @Mock
    private DunningPolicyLevelApiService policyLevelApiService;

    @Mock
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Mock
    private DunningSettingsService dunningSettingsService;

    @Mock
    private DunningPolicyService dunningPolicyService;

    @Mock
    private DunningLevelService levelService;
    
    @Mock
    private AuditLogService auditLogService;

    @Before
    public void setUp() {
        DunningPolicy dunningPolicy = new DunningPolicy();
        dunningPolicy.setId(1L);
        dunningPolicy.setPolicyName("policyNAme");
        dunningPolicy.setPolicyDescription("description");
        dunningPolicy.setIsDefaultPolicy(Boolean.TRUE);
        dunningPolicy.setMinBalanceTrigger(0.5);
        dunningPolicy.setTotalDunningLevels(1);

        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1l);
        dunningLevel.setEndOfDunningLevel(Boolean.TRUE);
        dunningPolicy.setDunningLevels(new ArrayList<>());

        DunningCollectionPlanStatus collectionPlanStatus = new DunningCollectionPlanStatus();
        collectionPlanStatus.setId(1L);
        collectionPlanStatus.setStatus(DunningCollectionPlanStatusEnum.FAILED);

        when(dunningPolicyApiService.updateTotalLevels(any())).thenReturn(of(dunningPolicy));
        when(dunningPolicyApiService.create(any())).thenReturn(dunningPolicy);
        when(dunningPolicyService.findById(anyLong(), Mockito.anyList())).thenReturn(dunningPolicy);
        when(dunningPolicyApiService.update(anyLong(), any(DunningPolicy.class))).thenReturn(Optional.of(dunningPolicy));
        when(levelService.findById(anyLong())).thenReturn(dunningLevel);
    }

    @Test
    public void shouldCreateDunningPolicy() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1l)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningPolicyLevels(asList(dunningPolicyLevel))
                .build();
        when(dunningSettingsService.getMaxNumberOfDunningLevels()).thenReturn(5);
        Response response = dunningPolicyResource.create(resource);

        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    public void shouldFailWhileCreatingDunningPolicyIfPolicyNameIsMissing() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1l)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningPolicyLevels(asList(dunningPolicyLevel))
                .build();
        Response response = dunningPolicyResource.create(resource);

        Assert.assertEquals(412, response.getStatus());
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailWhileCreatingDunningPolicyIfDunningLevelDoesNotExits() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1l)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningPolicyLevels(asList(dunningPolicyLevel))
                .build();
        when(dunningSettingsService.getMaxNumberOfDunningLevels()).thenReturn(5);
        when(dunningPolicyApiService.refreshPolicyLevel(any(org.meveo.model.dunning.DunningPolicyLevel.class)))
                .thenThrow(new BadRequestException("Policy level creation fails dunning level does not exists"));
        dunningPolicyResource.create(resource);
    }


    @Test
    public void shouldUpdateDunningPolicy() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicyInput resource = ImmutableDunningPolicyInput.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningPolicyLevels(asList(dunningPolicyLevel))
                .build();
        when(dunningSettingsService.getMaxNumberOfDunningLevels()).thenReturn(5);
        Response response = dunningPolicyResource.update(1L, resource);
        Assert.assertEquals(200, response.getStatus());
    }


    @Test(expected = NotFoundException.class)
    public void shouldFailIfDunningPolicyDoesNotExits() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .id(1L)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicyInput resource = ImmutableDunningPolicyInput.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningPolicyLevels(asList(dunningPolicyLevel))
                .build();
        when(dunningPolicyService.findById(anyLong(), Mockito.anyList())).thenReturn(null);
        dunningPolicyResource.update(1L, resource);
    }
}
