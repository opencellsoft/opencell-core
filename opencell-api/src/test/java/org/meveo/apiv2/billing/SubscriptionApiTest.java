package org.meveo.apiv2.billing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionApiTest {

    @InjectMocks
    private SubscriptionApi subscriptionApi;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ServiceInstanceService serviceInstanceService;

    @Test
    public void deleteInactiveServiceInstanceNominal() {
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("SUB-1");
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        List<ServiceInstance> instances = new ArrayList<>();
        ServiceInstance instance = new ServiceInstance();
        instance.setId(1L);
        instance.setStatus(InstanceStatusEnum.INACTIVE);

        instances.add(instance);

        subscription.setServiceInstances(instances);

        Mockito.when(subscriptionService.findById(any())).thenReturn(subscription);
        Mockito.doNothing().when(serviceInstanceService).remove(1L);

        subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(1L));

    }

    @Test
    public void deleteInactiveServiceInstanceNominal2() {
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("SUB-1");
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        List<ServiceInstance> instances = new ArrayList<>();
        ServiceInstance instance = new ServiceInstance();
        instance.setId(1L);
        instance.setStatus(InstanceStatusEnum.PENDING);

        instances.add(instance);

        subscription.setServiceInstances(instances);

        Mockito.when(subscriptionService.findById(any())).thenReturn(subscription);
        Mockito.doNothing().when(serviceInstanceService).remove(1L);

        subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(1L));

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void deleteInactiveServiceInstanceSubNotFoundErr() {
        Mockito.when(subscriptionService.findById(any())).thenReturn(null);

        subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(1L));

    }

    @Test
    public void deleteInactiveServiceInstanceSubInvalidStatus() {
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("SUB-1");
        subscription.setStatus(SubscriptionStatusEnum.RESILIATED);

        List<ServiceInstance> instances = new ArrayList<>();
        ServiceInstance instance = new ServiceInstance();
        instance.setId(1L);
        instance.setStatus(InstanceStatusEnum.INACTIVE);

        instances.add(instance);

        subscription.setServiceInstances(instances);

        Mockito.when(subscriptionService.findById(any())).thenReturn(subscription);

        try {
            subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(1L));
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Subscription status is in RESILIATED status");
        }

    }

    @Test
    public void deleteInactiveServiceInstanceServiceInstanceEmptyErr() {
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("SUB-1");
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        Mockito.when(subscriptionService.findById(any())).thenReturn(subscription);

        try {
            subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(1L));
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "No service instance found for subscription [code='SUB-1']");
        }

    }

    @Test
    public void deleteInactiveServiceInstanceSubSINotFound() {
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("SUB-1");
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        List<ServiceInstance> instances = new ArrayList<>();
        ServiceInstance instance = new ServiceInstance();
        instance.setId(1L);
        instance.setStatus(InstanceStatusEnum.INACTIVE);

        instances.add(instance);

        subscription.setServiceInstances(instances);

        Mockito.when(subscriptionService.findById(any())).thenReturn(subscription);

        try {
            subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(2L));
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "ServiceInstance with [id='2'] not found for subscription [code='SUB-1']");
        }
    }

    @Test
    public void deleteInactiveServiceInstanceSubSIStatusErr() {
        Subscription subscription = new Subscription();
        subscription.setId(1L);
        subscription.setCode("SUB-1");
        subscription.setStatus(SubscriptionStatusEnum.ACTIVE);

        List<ServiceInstance> instances = new ArrayList<>();
        ServiceInstance instance = new ServiceInstance();
        instance.setId(1L);
        instance.setCode("SI-001");
        instance.setStatus(InstanceStatusEnum.ACTIVE);

        instances.add(instance);

        subscription.setServiceInstances(instances);

        Mockito.when(subscriptionService.findById(any())).thenReturn(subscription);

        try {
            subscriptionApi.deleteInactiveServiceInstance(1L, buildDeleteSI(1L));
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Invalid ServiceInstance status with code 'SI-001' : expected status INACTIVE / PENDING found ACTIVE");
        }
    }

    private ServiceInstanceToDelete buildDeleteSI(Long... ids) {
        return () -> Arrays.asList(ids);
    }

}