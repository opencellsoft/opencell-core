package org.meveo.service.billing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.payments.PaymentScheduleTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.payments.impl.PaymentScheduleTemplateService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class ServiceInstanceServiceTest {

	@Spy
	@InjectMocks
	private ServiceInstanceService serviceInstanceService;

	@Mock
	private PaymentScheduleTemplateService paymentScheduleTemplateService;

	@Mock
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Mock
	private ServiceInstance serviceInstance;

	@Before
	public void setUp() {
		ServiceTemplate serviceTemplate = new ServiceTemplate();
		doReturn(serviceTemplate).when(serviceInstance).getServiceTemplate();
		doReturn(serviceInstance).when(serviceInstanceService).update(serviceInstance);
		doReturn(new PaymentScheduleTemplate()).when(paymentScheduleTemplateService).findByServiceTemplate(serviceTemplate);
	}

	@Test
	public void testTerminateSuspendedService() {

		Date suspensionDate = DateUtils.newDate(2021, 3, 1, 0, 0, 0);
		Date terminationDate = DateUtils.newDate(2021, 6, 22, 0, 0, 0);
		Date subscriptionDate = DateUtils.newDate(2021, 1, 1, 0, 0, 0);

		Subscription subscription = new Subscription();
		subscription.setSubscriptionDate(subscriptionDate);
		InstanceStatusEnum suspendedStatus = InstanceStatusEnum.SUSPENDED;

		RecurringChargeTemplate recurringChargeTemplate = mock(RecurringChargeTemplate.class);
		SubscriptionTerminationReason terminationReason = mock(SubscriptionTerminationReason.class);

		when(serviceInstance.getStatus()).thenReturn(suspendedStatus);
		when(serviceInstance.getSubscription()).thenReturn(subscription);
		when(serviceInstance.getRecurringChargeInstances()).thenAnswer(new Answer<List<RecurringChargeInstance>>() {
			public List<RecurringChargeInstance> answer(InvocationOnMock invocation) throws Throwable {
				RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
				chargeInstance.setServiceInstance((ServiceInstance) invocation.getMock());
				chargeInstance.setRecurringChargeTemplate(recurringChargeTemplate);
				chargeInstance.setChargedToDate(suspensionDate);
				chargeInstance.setStatus(suspendedStatus);
				return Arrays.asList(chargeInstance);
			}
		});

		serviceInstance = serviceInstanceService.terminateService(serviceInstance, terminationDate, terminationReason, null);
		// Do not apply a rating during the suspend period
		verify(recurringChargeInstanceService, never()).applyRecuringChargeToEndAgreementDate(any(), any());
	}

	@Test
	public void testTerminateSuspendedServiceWithChargeNeverRated() {
		
		Date terminationDate = DateUtils.newDate(2021, 6, 22, 0, 0, 0);
		Date subscriptionDate = DateUtils.newDate(2021, 1, 1, 0, 0, 0);
		
		Subscription subscription = new Subscription();
		subscription.setSubscriptionDate(subscriptionDate);
		InstanceStatusEnum suspendedStatus = InstanceStatusEnum.SUSPENDED;
		
		RecurringChargeTemplate recurringChargeTemplate = mock(RecurringChargeTemplate.class);
		SubscriptionTerminationReason terminationReason = mock(SubscriptionTerminationReason.class);
		
		when(serviceInstance.getStatus()).thenReturn(suspendedStatus);
		when(serviceInstance.getSubscription()).thenReturn(subscription);
		when(serviceInstance.getRecurringChargeInstances()).thenAnswer(new Answer<List<RecurringChargeInstance>>() {
			public List<RecurringChargeInstance> answer(InvocationOnMock invocation) throws Throwable {
				RecurringChargeInstance chargeInstance = new RecurringChargeInstance();
				chargeInstance.setChargeDate(subscriptionDate);
				chargeInstance.setChargedToDate(null);
				chargeInstance.setApplyInAdvance(false);
				chargeInstance.setStatus(suspendedStatus);
				chargeInstance.setRecurringChargeTemplate(recurringChargeTemplate);
				chargeInstance.setServiceInstance((ServiceInstance) invocation.getMock());
				return Arrays.asList(chargeInstance);
			}
		});
		
		serviceInstance = serviceInstanceService.terminateService(serviceInstance, terminationDate, terminationReason, null);
		// Do not apply a rating during the suspend period even with charge never been applied before (chargedToDate = null)
		verify(recurringChargeInstanceService, never()).applyRecuringChargeToEndAgreementDate(any(), any());
	}
}
