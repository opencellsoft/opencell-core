package org.meveo.apiv2.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.services.generic.GenericApiAlteringService;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTypeEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.dwh.BarChart;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.model.shared.Address;
import org.meveo.service.base.PersistenceService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericApiAlteringServiceTest {
    @Spy
    @InjectMocks
    private GenericApiAlteringService sut;
    
    @Mock
    private EntityManagerWrapper entityManagerWrapper;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private PersistenceService persistenceService;
    
    @Before
    public void init() throws Exception {
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
    }
    
    @Test
    public void given_empty_entity_name_when_update_then_should_throw_meveo_exception() {
        //Given
        String entityName = "";
        String dtoToUpdate = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"historized\":false,\"notified\":false,\"auditableFields\":[],"
                + "\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\"}}";
        try {
            sut.update(entityName, 54L, dtoToUpdate);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The entityName should not be null or empty");
        }
    }
    
    @Test
    public void given_null_entity_name_when_update_then_should_throw_meveo_exception() {
        //Given
        String entityName = null;
        String dtoToUpdate = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"historized\":false,\"notified\":false,\"auditableFields\":[],"
                + "\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\"}}";
        try {
            sut.update(entityName, 54L, dtoToUpdate);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The entityName should not be null or empty");
        }
    }
    
    @Test
    public void given_non_null_but_unrecognized_entity_name_when_update_then_should_throw_meveo_exception() {
        //Given
        String entityName = "flirtikit";
        String dtoToUpdate = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"historized\":false,\"notified\":false,\"auditableFields\":[],"
                + "\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\"}}";
        try {
            sut.update(entityName, 54L, dtoToUpdate);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The requested entity does not exist");
        }
    }
    
    @Test
    public void given_null_id_when_update_then_should_throw_meveo_exception() {
        //Given
        String entityName = "Customer";
        Long id = null;
        String dtoToUpdate = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"historized\":false,\"notified\":false,\"auditableFields\":[],"
                + "\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\"}}";
        try {
            sut.update(entityName, id, dtoToUpdate);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The requested id should not be null");
        }
    }
    
    @Test
    public void given_empty_dto_when_update_then_should_throw_meveo_exception() {
        //Given
        String entityName = "Customer";
        String dtoToUpdate = "";
        try {
            sut.update(entityName, 54L, dtoToUpdate);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The given json dto representation should not be null or empty");
        }
    }
    
    @Test
    public void given_null_dto_when_update_then_should_throw_meveo_exception() {
        //Given
        String entityName = "Customer";
        String dtoToUpdate = null;
        try {
            sut.update(entityName, 54L, dtoToUpdate);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The given json dto representation should not be null or empty");
        }
    }
    
    @Test
    public void should_set_id_before_update() {
        //Given
        String dto = "{\n" + "\t\"code\":\"xxx\"\n" + "}\n";
        
        Customer customer = captureParamOnUpdate(dto, Customer.class);
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getId()).isEqualTo(54L);
    }
    
    @Test
    public void should_not_be_able_to_update_id() {
        //Given
        String dto = "{\n" + "\t\"id\":\"23\"\n" + "}\n";
        
        Customer customer = captureParamOnUpdate(dto, Customer.class);
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getId()).isNotEqualTo(23L);
    }
    
    @Test
    public void should_not_be_able_to_update_an_entity_field() {
        //Given
        String dto = "{\n" + "\t\"addressbook\": {\"code\":\"xxx\"}}";
        //When
        Customer customer = captureParamOnUpdate(dto, Customer.class);
        assertThat(customer).isNotNull();
        assertThat(customer.getAddressbook()).isNull();
    }
    
    @Test
    public void should_not_be_able_to_update_auditable() {
        //Given
        String dto = "{\"auditable\": {\"created\":1558105466615,\"creator\":\"opencell.admin\"}}";
        //When
        BillingCycle billingCycle = captureParamOnUpdate(dto, BillingCycle.class);
        
        //Then
        assertThat(billingCycle).isNotNull();
        assertThat(billingCycle.getAuditable()).isNull();
    }
    
    @Test
    public void should_not_be_able_to_update_code() {
        //Given
        String dto = "{\n" + "\t\"code\":\"xxxx\"\n" + "}\n";
        //When
        BillingCycle billingCycle = captureParamOnUpdate(dto, BillingCycle.class);
        
        //Then
        assertThat(billingCycle).isNotNull();
        assertThat(billingCycle.getCode()).isNull();
    }
    
    @Test
    public void should_not_be_able_to_update_status() {
        //Given
        String dto = "{\n" + "\t\"status\":\"ACKNOWLEDGED\"\n" + "}\n";
        //When
        Order order = captureParamOnUpdate(dto, Order.class);
        
        //Then
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.IN_CREATION);
    }
    
    @Test
    public void should_not_be_able_to_update_uuid() {
        //Given
        String dto = "{\n" + "\t\"uuid\":\"14f2ab2a-53b5-47d7-8442-5a21d54b5106\"\n" + "}\n";
        //When
        BillingCycle billingCycle = captureParamOnUpdate(dto, BillingCycle.class);
        
        //Then
        assertThat(billingCycle).isNotNull();
        assertThat(billingCycle.getUuid()).isNotEqualTo("14f2ab2a-53b5-47d7-8442-5a21d54b5106");
    }
    
    @Test
    public void should_convert_correct_big_decimal() {
        //Given
        String dto = "{\n" + "\t\"invoicingThreshold\":\"33.35\"\n" + "}\n";
        
        BillingCycle billingCycle = captureParamOnUpdate(dto, BillingCycle.class);
        assertThat(billingCycle).isNotNull();
        assertThat(billingCycle.getInvoicingThreshold()).isNotNull();
        assertThat(billingCycle.getInvoicingThreshold()).isEqualTo(BigDecimal.valueOf(33.35));
    }
    
    @Test
    public void should_convert_correct_long() {
        //Given
        String dto = "{\n" + "\t\"currentInvoiceNb\":\"13\"\n" + "}\n";
        InvoiceSequence invoiceSequence = captureParamOnUpdate(dto, InvoiceSequence.class);
        assertThat(invoiceSequence).isNotNull();
        assertThat(invoiceSequence.getCurrentInvoiceNb()).isNotNull();
        assertThat(invoiceSequence.getCurrentInvoiceNb()).isEqualTo(13L);
    }
    
    @Test
    public void should_convert_correct_integer() {
        //Given
        String dto = "{\n" + "\t\"transactionDateDelay\":\"40\"\n" + "}\n";
        
        BillingCycle billingCycle = captureParamOnUpdate(dto, BillingCycle.class);
        assertThat(billingCycle).isNotNull();
        assertThat(billingCycle.getTransactionDateDelay()).isNotNull();
        assertThat(billingCycle.getTransactionDateDelay()).isEqualTo(40);
    }
    
    @Test
    public void should_convert_correct_double() {
        //Given
        String dto = "{\n" + "\t\"min\":\"40.54\"\n" + "}\n";
        
        BarChart barChart = captureParamOnUpdate(dto, BarChart.class);
        assertThat(barChart).isNotNull();
        assertThat(barChart.getMin()).isNotNull();
        assertThat(barChart.getMin()).isEqualTo(40.54);
    }
    
    @Test
    public void should_convert_correct_String() {
        //Given
        String dto = "{\n" + "\t\"invoiceTypeEl\":\"flirtikit\"\n" + "}\n";
        //When
        BillingCycle billingCycle = captureParamOnUpdate(dto, BillingCycle.class);
        
        //Then
        assertThat(billingCycle).isNotNull();
        assertThat(billingCycle.getInvoiceTypeEl()).isNotNull();
        assertThat(billingCycle.getInvoiceTypeEl()).isEqualTo("flirtikit");
    }
    
    @Test
    public void should_convert_correct_enum() {
        //Given
        String dto = "{\n" + "\t\"counterType\":\"NOTIFICATION\"\n" + "}\n";
        
        //When
        CounterTemplate counterTemplate = captureParamOnUpdate(dto, CounterTemplate.class);
        
        //Then
        assertThat(counterTemplate).isNotNull();
        assertThat(counterTemplate.getCounterType()).isNotNull();
        assertThat(counterTemplate.getCounterType()).isEqualTo(CounterTypeEnum.NOTIFICATION);
    }
    
    @Test
    public void should_update_mutiple_fields() {
        //Given
        String dto = "{\n" + "\t\"description\": \"flirtikit\",\n"
                + "\t\"address\": {\"address1\":\"14 rue Crespin du Gast\",\"zipCode\":\"75011\",\"city\":\"PARIS\",\"state\":\"Paris Area\"},\n"
                + "\t\"contactInformation\": {\"email\":\"elminster.aumar@opencellsoft.com\"}\n}";
        //When
        Customer customer = captureParamOnUpdate(dto, Customer.class);
        
        //Then
        assertThat(customer).isNotNull();
        assertThat(customer.getDescription()).isEqualTo("flirtikit");
        assertThat(customer.getContactInformation()).isNotNull();
        assertThat(customer.getContactInformation().getEmail()).isEqualTo("elminster.aumar@opencellsoft.com");
        
        assertThat(customer.getContactInformation().getEmail()).isEqualTo("elminster.aumar@opencellsoft.com");
        assertThat(customer.getAddress()).isNotNull();
        assertThat(getAddressAssertion(customer.getAddress())).isTrue();
        
    }
    
    private Address getAddress() {
        Address expected = new Address();
        expected.setAddress1("14 rue Crespin du Gast");
        expected.setZipCode("75002");
        expected.setCity("Paris");
        return expected;
    }
    
    private boolean getAddressAssertion(Address expected) {
        return expected.getAddress1().equals("14 rue Crespin du Gast") && expected.getZipCode().equals("75011") && expected.getCity().equals("PARIS") && expected.getState()
                .equals("Paris Area");
    }
    
    private <T extends BaseEntity> T captureParamOnUpdate(String dto, Class<T> type) {
        doReturn(persistenceService).when(sut).getPersistenceService(eq(type));
        T expectedEntity = mock(type);
        when(entityManager.find(eq(type), anyLong())).thenReturn((T) ReflectionUtils.createObject(type.getName()));
        ArgumentCaptor<T> argumentCaptor = ArgumentCaptor.forClass(type);
        //When
        sut.update(type.getSimpleName(), 54L, dto);
        //Then
        verify(persistenceService).update(argumentCaptor.capture());
        T value = argumentCaptor.getValue();
        value.setId(54L);
        return value;
    }
    
}