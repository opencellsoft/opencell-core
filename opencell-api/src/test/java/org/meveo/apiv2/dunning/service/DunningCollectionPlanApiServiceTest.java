package org.meveo.apiv2.dunning.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.service.payments.impl.DunningCollectionPlanService;
import org.meveo.service.payments.impl.DunningPolicyService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DunningCollectionPlanApiServiceTest {

    @InjectMocks
    private DunningCollectionPlanApiService collectionPlanApiService;

    @Mock
    private DunningCollectionPlanService dunningCollectionPlanService;

    @Mock
    private DunningPolicyService dunningPolicyService;

    @Mock
    private ResourceBundle resourceMessages;

    @Before
    public void setUp() {
        DunningPolicy policy = new DunningPolicy();
        policy.setId(1L);
        policy.setPolicyName("PolicyName");
        when(resourceMessages.getString(anyString(), anyLong())).thenReturn("Collection plan with does not exits");
        when(dunningPolicyService.availablePoliciesForSwitch(any())).thenReturn(Arrays.asList(policy));

    }

    @Test
    public void shouldReturnAvailablePoliciesForACollectionPlan() {
        DunningCollectionPlan collectionPlan = new DunningCollectionPlan();
        collectionPlan.setId(1L);
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        collectionPlan.setCollectionPlanRelatedInvoice(invoice);
        when(dunningCollectionPlanService.findById(anyLong())).thenReturn(collectionPlan);

        List<DunningPolicy> availablePolicies = collectionPlanApiService.availableDunningPolicies(1L);

        assertEquals(1, availablePolicies.size());
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundIfCollectionPlanNotFound() {
        when(dunningCollectionPlanService.findById(anyLong())).thenReturn(null);
        collectionPlanApiService.availableDunningPolicies(1L);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundIfNoInvoiceFoundForCollectionPlan() {
        DunningCollectionPlan collectionPlan = new DunningCollectionPlan();
        collectionPlan.setId(1L);
        when(dunningCollectionPlanService.findById(anyLong())).thenReturn(collectionPlan);
        collectionPlanApiService.availableDunningPolicies(1L);
    }
}