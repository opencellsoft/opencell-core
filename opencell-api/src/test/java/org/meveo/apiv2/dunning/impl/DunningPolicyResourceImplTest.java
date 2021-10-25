package org.meveo.apiv2.dunning.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.dunning.DunningPolicyLevel;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;
import org.meveo.apiv2.dunning.ImmutableDunningPolicyLevel;
import org.meveo.apiv2.dunning.service.DunningPolicyApiService;
import org.meveo.apiv2.dunning.service.DunningPolicyLevelApiService;
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.model.dunning.InvoiceDunningStatuses;
import org.meveo.service.dunning.DunningLevelService;
import org.meveo.service.payments.impl.CollectionPlanStatusService;
import org.meveo.service.payments.impl.InvoiceDunningStatusesService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DunningPolicyResourceImplTest {

    @Spy
    @InjectMocks
    private DunningPolicyResourceImpl dunningPolicyResource;

    @Mock
    private DunningPolicyApiService dunningPolicyApiService;

    @Mock
    private DunningLevelService dunningLevelService;

    @Mock
    private InvoiceDunningStatusesService invoiceDunningStatusesService;

    @Mock
    private CollectionPlanStatusService collectionPlanStatusService;

    @Mock
    private DunningPolicyLevelApiService policyLevelApiService;

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

        InvoiceDunningStatuses invoiceDunningStatuses = new InvoiceDunningStatuses();
        invoiceDunningStatuses.setId(1L);
        invoiceDunningStatuses.setContext("Failed Dunning");
        invoiceDunningStatuses.setStatus("Failed Dunning");

        //when(dunningPolicyApiService.findByName(Mockito.anyString())).thenReturn(of(dunningPolicy));
        when(dunningLevelService.refreshOrRetrieve(any(DunningLevel.class))).thenReturn(dunningLevel);
        when(invoiceDunningStatusesService.refreshOrRetrieve(any(InvoiceDunningStatuses.class))).thenReturn(invoiceDunningStatuses);
        when(collectionPlanStatusService.refreshOrRetrieve(any(CollectionPlanStatus.class))).thenReturn(collectionPlanStatus);
        when(dunningPolicyApiService.updateTotalLevels(any())).thenReturn(of(dunningPolicy));
        when(dunningPolicyApiService.create(any())).thenReturn(dunningPolicy);
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

        Assert.assertEquals(201, response.getStatus());
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
        when(dunningLevelService.refreshOrRetrieve(any(DunningLevel.class))).thenReturn(null);
        dunningPolicyResource.create(resource);
    }

}
