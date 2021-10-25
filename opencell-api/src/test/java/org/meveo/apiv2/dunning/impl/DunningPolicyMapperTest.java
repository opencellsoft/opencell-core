package org.meveo.apiv2.dunning.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.ImmutableDunningPolicy;

public class DunningPolicyMapperTest {

    private DunningPolicyMapper mapper;

    @Before
    public void setUp() {
        mapper = new DunningPolicyMapper();
    }

    @Test
    public void shouldConvertResourceToEntity() {
        DunningPolicy resource = ImmutableDunningPolicy.builder()
                .policyName("name")
                .policyDescription("description")
                .isActivePolicy(Boolean.TRUE)
                .isDefaultPolicy(Boolean.FALSE)
                .minBalanceTrigger(0.5)
                .isIncludeDueInvoicesInThreshold(Boolean.TRUE)
                .build();
        org.meveo.model.dunning.DunningPolicy entity = mapper.toEntity(resource);
        Assert.assertEquals("name", entity.getPolicyName());
        Assert.assertTrue(entity.getActivePolicy());
    }
}
