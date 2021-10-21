package org.meveo.apiv2.billing;

import static org.meveo.apiv2.billing.ImmutableCdrListInput.builder;

import javax.ws.rs.BadRequestException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.apiv2.billing.service.MediationApiService;
import org.meveo.commons.utils.ParamBeanFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MediationApiServiceTest {

    @Spy
    @InjectMocks
    private MediationApiService mediationApiService;

    @Mock
    protected ParamBeanFactory paramBeanFactory;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private String ipAddress = "127.0.0.1";

    @Before
    public void setup() {

    }

    @Test
    public void test_with_cdrListInput_null() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("The input params are required");
        mediationApiService.registerCdrList(null, ipAddress);
    }

    @Test
    public void test_with_cdrListInput_empty() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("The cdrs list are required");

        CdrListInput cdrListInput = builder().build();
        mediationApiService.registerCdrList(cdrListInput, ipAddress);
    }
}