package org.meveo.service.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunTypeEnum;
import org.meveo.service.billing.impl.BillingRunService;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BillingRunServiceTest {

	@Spy
	private BillingRunService billingRunService;

	@Before
	public void setUp() {
		doAnswer(invocation -> {
			BillingRun entity = (BillingRun) invocation.getArgument(0);
			((BillingRunService) invocation.getMock()).setBillingRunType(entity);
			return null;
		}).when(billingRunService).create(any());

		doAnswer(invocation -> {
			BillingRun entity = (BillingRun) invocation.getArgument(0);
			((BillingRunService) invocation.getMock()).setBillingRunType(entity);
			return entity;
		}).when(billingRunService).update(any());

		/** Prevent super class method to be run doesn't work with mockito */
		// doNothing().when((PersistenceService<BillingRun>) billingRunService).create(any());
	}

	@Test
	public void testCreateOrUpdateBillingRun() {

		BillingRun billingRunCycle = new BillingRun();
		BillingRun billingRunExceptional = new BillingRun();

		billingRunCycle.setBillingCycle(new BillingCycle());

		billingRunService.create(billingRunCycle);
		billingRunService.create(billingRunExceptional);

		assertThat(billingRunCycle.getRunType()).isEqualTo(BillingRunTypeEnum.CYCLE);
		assertThat(billingRunExceptional.getRunType()).isEqualTo(BillingRunTypeEnum.EXCEPTIONAL);

		/** reverse billingRuns already created and check the runType put */
		billingRunCycle.setBillingCycle(null);
		billingRunExceptional.setBillingCycle(new BillingCycle());
		billingRunService.update(billingRunCycle);
		billingRunService.update(billingRunExceptional);

		assertThat(billingRunCycle.getRunType()).isEqualTo(BillingRunTypeEnum.EXCEPTIONAL);
		assertThat(billingRunExceptional.getRunType()).isEqualTo(BillingRunTypeEnum.CYCLE);

	}
}
