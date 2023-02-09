package org.meveo.apiv2.report.query.impl;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.meveo.apiv2.report.ImmutableReportQueryInput.builder;

import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.model.Auditable;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.model.report.query.QueryVisibilityEnum;

import java.util.Arrays;
import java.util.Date;

public class ReportQueryMapperTest {

    private ReportQueryMapper mapper;

    @Before
    public void setUp() {
        mapper = new ReportQueryMapper();
    }

    @Test
    public void shouldConvertResourceToEntity() {
        ReportQueryInput resource = builder()
                .queryName("code")
                .queryDescription("description")
                .visibility(QueryVisibilityEnum.PUBLIC)
                .targetEntity("org.meveo.model.billing.BillingAccount")
                .genericFields(Arrays.asList("code", "description"))
                .build();
        ReportQuery entity = mapper.toEntity(resource);
        assertThat(entity, instanceOf(ReportQuery.class));
        assertEquals(entity.getCode(), resource.getQueryName());
        assertEquals(entity.getDescription(), resource.getQueryDescription());
    }

    @Test
    public void shouldConvertEntityToResource() {
        ReportQuery entity = new ReportQuery();
        entity.setCode("code");
        entity.setDescription("Custom query");
        entity.setVisibility(QueryVisibilityEnum.PRIVATE);
        entity.setTargetEntity("org.meveo.model.billing.BillingAccount");
        entity.setFields(Arrays.asList("code", "description"));
        Auditable auditable = new Auditable();
        auditable.setCreator("opencell.admin");
        auditable.setCreated(new Date());
        entity.setAuditable(auditable);
        
        org.meveo.apiv2.report.ReportQuery resource = mapper.toResource(entity);
        assertThat(resource, instanceOf(org.meveo.apiv2.report.ReportQuery.class));
        assertEquals(resource.getCode(), entity.getCode());
    }
}
