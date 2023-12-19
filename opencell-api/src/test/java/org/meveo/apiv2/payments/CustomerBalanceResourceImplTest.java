package org.meveo.apiv2.payments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.payments.ImmutableCustomerBalance;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.payments.resource.CustomerBalanceResourceImpl;
import org.meveo.apiv2.payments.resource.CustomerBalanceMapper;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.service.payments.impl.CustomerBalanceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class CustomerBalanceResourceImplTest {

    @InjectMocks
    private CustomerBalanceResourceImpl customerBalanceResource;

    @Mock
    private CustomerBalanceService customerBalanceService;

    private CustomerBalanceMapper mapper = new CustomerBalanceMapper();

    @Test
    public void shouldCreateCustomerBalance() {
        Resource template = ImmutableResource.builder()
                .id(1L).build();
        Resource template2 = ImmutableResource.builder()
                .id(2L).build();
        CustomerBalance customerBalance = ImmutableCustomerBalance
                .builder()
                .defaultBalance(true)
                .code("CODE")
                .label("LABEL")
                .occTemplates(Arrays.asList(template, template2))
                .build();

        Response response = customerBalanceResource.create(customerBalance);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateCustomerBalanceIfCodeAlreadyExists() {
        Resource template = ImmutableResource.builder()
                .id(1L).build();
        Resource template2 = ImmutableResource.builder()
                .id(2L).build();
        CustomerBalance customerBalance = ImmutableCustomerBalance
                .builder()
                .defaultBalance(true)
                .code("CODE")
                .label("LABEL")
                .occTemplates(Arrays.asList(template, template2))
                .build();

        when(customerBalanceService.findByCode("CODE")).thenReturn(new org.meveo.model.payments.CustomerBalance());

        customerBalanceResource.create(customerBalance);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotCreateCustomerBalanceIfOccTemplateAreMissing() {
        CustomerBalance customerBalance = ImmutableCustomerBalance
                .builder()
                .defaultBalance(true)
                .code("CODE")
                .label("LABEL")
                .occTemplates(Collections.emptyList())
                .build();

        customerBalanceResource.create(customerBalance);
    }

    @Test
    public void shouldUpdateCustomerBalance() {
        Resource template = ImmutableResource.builder()
                .id(1L).build();
        Resource template2 = ImmutableResource.builder()
                .id(2L).build();
        CustomerBalance customerBalance = ImmutableCustomerBalance
                .builder()
                .defaultBalance(true)
                .id(1L)
                .code("CODE")
                .label("LABEL")
                .occTemplates(Arrays.asList(template, template2))
                .build();

        Response response = customerBalanceResource.update(1L, customerBalance);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldDeleteCustomerBalance() {
        Resource template = ImmutableResource.builder()
                .id(1L).build();
        Resource template2 = ImmutableResource.builder()
                .id(2L).build();
        CustomerBalance customerBalance = ImmutableCustomerBalance
                .builder()
                .defaultBalance(false)
                .id(1L)
                .code("CODE")
                .label("LABEL")
                .occTemplates(Arrays.asList(template, template2))
                .build();

        org.meveo.model.payments.CustomerBalance entity = mapper.toEntity(customerBalance);
        when(customerBalanceService.findById(1L)).thenReturn(entity);

        Response response = customerBalanceResource.delete(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundIfNoCustomerBalance() {
        when(customerBalanceService.findById(1L)).thenReturn(null);

        customerBalanceResource.delete(1L);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotDeleteDefaultCustomerBalance() {
        org.meveo.model.payments.CustomerBalance entity = new org.meveo.model.payments.CustomerBalance();
        OCCTemplate template1 = new OCCTemplate();
        template1.setId(1L);
        OCCTemplate template2 = new OCCTemplate();
        template2.setId(2L);
        entity.setOccTemplates(Arrays.asList(template1, template2));
        entity.setCode("CODE_01");
        entity.setDescription("LABEL");
        entity.setDefaultBalance(true);

        when(customerBalanceService.findById(1L)).thenReturn(entity);

        customerBalanceResource.delete(1L);
    }
}