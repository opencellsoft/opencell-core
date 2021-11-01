package org.meveo.apiv2.dunning.impl;

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
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.model.dunning.DunningInvoiceStatusContextEnum;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.service.payments.impl.DunningPolicyLevelService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

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

    @Before
    public void setUp() {
        DunningPolicy dunningPolicy = new DunningPolicy();
        dunningPolicy.setId(1L);
        dunningPolicy.setPolicyName("policyNAme");
        dunningPolicy.setPolicyDescription("description");
        dunningPolicy.setDefaultPolicy(Boolean.TRUE);
        dunningPolicy.setMinBalanceTrigger(0.5);
        dunningPolicy.setTotalDunningLevels(1);

        DunningLevel dunningLevel = new DunningLevel();
        dunningLevel.setId(1l);
        dunningLevel.setEndOfDunningLevel(Boolean.TRUE);

        CollectionPlanStatus collectionPlanStatus = new CollectionPlanStatus();
        collectionPlanStatus.setId(1L);
        collectionPlanStatus.setContext("Failed Dunning");
        collectionPlanStatus.setStatus("Failed Dunning");

        DunningInvoiceStatus invoiceDunningStatuses = new DunningInvoiceStatus();
        invoiceDunningStatuses.setId(1L);
        invoiceDunningStatuses.setContext(DunningInvoiceStatusContextEnum.ACTIVE_DUNNING);
        invoiceDunningStatuses.setStatus("Failed Dunning");

        when(dunningPolicyApiService.updateTotalLevels(any())).thenReturn(of(dunningPolicy));
        when(dunningPolicyApiService.create(any())).thenReturn(dunningPolicy);
        when(dunningPolicyApiService.findById(1L)).thenReturn(of(dunningPolicy));
        when(dunningPolicyApiService.update(anyLong(), any(DunningPolicy.class))).thenReturn(Optional.of(dunningPolicy));
    }

    @Test
    public void shouldCreateDunningPolicy() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1l)
                .dunningPolicyName("policyNAme")
                .sequence(1)
                .invoiceDunningStatusesId(1L)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningLevels(asList(dunningPolicyLevel))
                .build();
        Response response = dunningPolicyResource.create(resource);

        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldFailWhileCreatingDunningPolicyIfPolicyNameIsMissing() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1l)
                .sequence(1)
                .invoiceDunningStatusesId(1L)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningLevels(asList(dunningPolicyLevel))
                .build();
        Response response = dunningPolicyResource.create(resource);

        Assert.assertEquals(412, response.getStatus());
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailWhileCreatingDunningPolicyIfDunningLevelDoesNotExits() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .dunningLevelId(1l)
                .dunningPolicyName("policyNAme")
                .sequence(1)
                .invoiceDunningStatusesId(1L)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningLevels(asList(dunningPolicyLevel))
                .build();
        when(dunningPolicyApiService.refreshPolicyLevel(any(org.meveo.model.dunning.DunningPolicyLevel.class)))
                .thenThrow(new BadRequestException("Policy level creation fails dunning level does not exists"));
        dunningPolicyResource.create(resource);
    }

    @Test
    public void shouldUpdateDunningPolicy() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .id(1L)
                .invoiceDunningStatusesId(1L)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicyInput resource = ImmutableDunningPolicyInput.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningLevels(asList(dunningPolicyLevel))
                .build();

        Response response = dunningPolicyResource.update(1L, resource);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void shouldFailIfDunningPolicyDoesNotExits() {
        DunningPolicyLevel dunningPolicyLevel = ImmutableDunningPolicyLevel.builder()
                .id(1L)
                .invoiceDunningStatusesId(1L)
                .collectionPlanStatusId(1L)
                .build();
        org.meveo.apiv2.dunning.DunningPolicyInput resource = ImmutableDunningPolicyInput.builder()
                .policyName("policyNAme")
                .policyDescription("description")
                .isDefaultPolicy(Boolean.TRUE)
                .minBalanceTrigger(0.5)
                .dunningLevels(asList(dunningPolicyLevel))
                .build();
        when(dunningPolicyApiService.findById(1L)).thenReturn(Optional.empty());
        dunningPolicyResource.update(1L, resource);
    }
}
