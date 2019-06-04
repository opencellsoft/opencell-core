package org.meveo.apiv2.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Country;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Address;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericApiLoadServiceTest {
    
    @Spy
    @InjectMocks
    private GenericApiLoadService sut;
    
    @Mock
    private GenericRequestDto genericRequestDto;
    
    @Mock
    private EntityManagerWrapper entityManagerWrapper;
    @Mock
    private EntityManager entityManager;
    
    @Before
    public void setUp() throws Exception {
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
        configureFindMock();
    }
    
    @Test
    public void given_an_empty_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException("", "The entityName should not be null or empty");
    }
    
    @Test
    public void given_null_id_when_load_model_then_should_throw_wrong_requested_model_id_exception() {
        try {
            //When
            sut.findByClassNameAndId("flirtikit", null, genericRequestDto);
            //Then
        } catch (Exception ex) {
            //Expected
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo("The requested id should not be null");
        }
    }
    
    @Test
    public void given_null_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException(null, "The entityName should not be null or empty");
    }
    
    @Test
    public void given_an_unrecognizable_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException("flirtikit", "The requested entity does not exist");
    }
    
    @Test
    public void given_null_dto_when_init_fields_then_should_return_empty_list() {
        //When
        sut.findByClassNameAndId("customer", 13l, null);
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(sut).buildGenericResponse(any(), captor.capture());
        //Then
        assertThat(captor.getValue()).isNotNull();
        assertThat(captor.getValue()).isEmpty();
    }
    
    @Test
    public void given_non_null_dto_when_init_fields_then_should_return_empty_list() {
        //Given
        GenericRequestDto dto = new GenericRequestDto();
        //When
        //When
        sut.findByClassNameAndId("customer", 13l, dto);
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(sut).buildGenericResponse(any(), captor.capture());
        //Then
        assertThat(captor.getValue()).isNotNull();
        assertThat(captor.getValue()).isEmpty();
    }
    
    @Test
    public void given_lower_case_model_name_when_load_model_then_should_return_correct_entity_class() {
        //Given
        String modelName = "customeraccount";
        //When
        ArgumentCaptor<Class> captor = getModelNameCaptor(modelName);
        //Then
        
        assertThat(captor.getValue()).isEqualTo(CustomerAccount.class);
        assertThat(captor.getValue().getAnnotation(Entity.class)).isNotNull();
        
    }
    
    @Test
    public void given_any_case_model_name_when_load_model_then_should_return_correct_entity_class() {
        //Given
        String modelName = "cUsToMeRaCcOuNt";
        //When
        ArgumentCaptor<Class> captor = getModelNameCaptor(modelName);
        //Then
        assertThat(captor.getValue()).isEqualTo(CustomerAccount.class);
        assertThat(captor.getValue().getAnnotation(Entity.class)).isNotNull();
        
    }
    
    @Test
    public void should_toJson_return_as_list_representation_of_ids_when_param_is_a_collection() {
        //Given
        Object param = buildCustomerList(3);
        //When
        String expected = sut.toJson(param);
        //Then
        assertThat(expected).isEqualTo("[0, 1, 2]");
    }
    
    @Test
    public void should_toJson_return_the_id_of_requested_entity_when_param_is_an_instance_of_base_entity() {
        //Given
        Object param = buildCustomerMock(55);
        //When
        String expected = sut.toJson(param);
        //Then
        assertThat(expected).isEqualTo("55");
    }
    
    @Test
    public void should_toJson_return_the_to_string_when_param_is_string() {
        //Given
        Object param = "flirtikit";
        //When
        String expected = sut.toJson(param);
        //Then"
        assertThat(expected).isEqualTo("flirtikit");
    }
    
    @Test
    public void should_toJson_return_the_string_representation_when_param_is_big_decimal() {
        //Given
        Object param = BigDecimal.valueOf(12345.05);
        //When
        String expected = sut.toJson(param);
        //Then
        assertThat(expected).isEqualTo("12345.05");
    }
    
    @Test
    public void should_toJson_return_the_string_representation_of_millis_when_param_is_date() {
        //Given
        Date param = new Date();
        //When
        String expected = sut.toJson(param);
        //Then
        assertThat(expected).isEqualTo(String.valueOf(param.getTime()));
    }
    
    @Test
    public void should_toJson_return_the_string_representation_when_param_is_an_non_entity_business_object() {
        //Given
        Address param = new Address();
        param.setAddress1("address1");
        param.setZipCode("75002");
        param.setCity("Paris");
        Country country = new Country();
        country.setDescription("Very beautyfull country");
        country.setCountryCode("xxx");
        param.setCountry(country);
        
        //When
        String expected = sut.toJson(param);
        //Then
        String expectedFormattedJson = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"historized\":false,\"notified\":false,\"auditableFields\":[],\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\"}}";
        assertThat(expected).isEqualTo(expectedFormattedJson);
    }
    
    @Test
    public void given_empty_fields_list_get_all_non_static_field_values_then_get_all_object_fields() {
        //Given
        Customer customer = new Customer();
        List<String> fields = Collections.emptyList();
        //When
        Set<String> filteredFields = sut.buildGenericResponse(customer, fields).getValue().keySet();
        //Then
        assertThat(filteredFields).isNotEmpty();
        assertThat(filteredFields).hasSize(32);
    }
    
    @Test
    public void given_unrecognized_fields_list_get_all_non_static_field_values_then_get_empty_list() {
        //Given
        Customer customer = new Customer();
        List<String> fields = Arrays.asList("flirtikit", "bidlidez", "gninendiden");
        //When
        Set<String> filteredFields = sut.buildGenericResponse(customer, fields).getValue().keySet();
        //Then
        assertThat(filteredFields).isEmpty();
    }
    
    @Test
    public void given_rendom_fields_list_get_all_non_static_field_values_then_get_only_recognized_fields() {
        //Given
        Customer customer = new Customer();
        customer.setAddressbook(mock(AddressBook.class));
        customer.setCustomerCategory(mock(CustomerCategory.class));
        List<String> fields = Arrays.asList("flirtikit", "bidlidez", "addressbook", "customerCategory", "gninendiden");
        //When
        Set<String> filteredFields = sut.buildGenericResponse(customer, fields).getValue().keySet();
        //Then
        assertThat(filteredFields).isNotEmpty();
        assertThat(filteredFields).hasSize(2);
        assertThat(filteredFields).containsSequence("addressbook", "customerCategory");
    }
    
    @Test
    public void given_empty_fields_list_get_all_non_static_field_values_then_get_only_non_static_fields() {
        //Given
        Customer customer = new Customer();
        List<String> fields = Collections.emptyList();
        //When
        Set<String> filteredFields = sut.buildGenericResponse(customer, fields).getValue().keySet();
        //Then
        assertThat(filteredFields).doesNotContain("serialVersionUID");
        assertThat(filteredFields).doesNotContain("ACCOUNT_TYPE");
    }
    
    @Test
    public void given_addressbook_field_when_get_all_non_static_field_values_then_should_return_a_map_containing_address_book_and_its_id() {
        //Given
        Customer customer = new Customer();
        AddressBook addressbook = new AddressBook();
        addressbook.setId(5L);
        customer.setAddressbook(addressbook);
        List<String> fields = Collections.singletonList("addressbook");
        //When
        Map<String, String> filteredFieldsAndValues = sut.buildGenericResponse(customer, fields).getValue();
        //Then
        assertThat(filteredFieldsAndValues).hasSize(1);
        assertThat(filteredFieldsAndValues).containsKeys("addressbook");
        assertThat(filteredFieldsAndValues.get("addressbook")).isEqualTo("5");
    }
    
    @Test
    public void given_null_value_field_when_extract_value_from_field_then_should_return_empty_string() throws Exception {
        //Given
        Customer result = new Customer();
        Field field = result.getClass().getDeclaredField("addressbook");
        //When
        Object extractedValue = sut.extractValueFromField(result, field);
        //Then
        assertThat(field.isAccessible()).isTrue();
        assertThat(extractedValue).isInstanceOf(String.class);
        assertThat(extractedValue).isEqualTo("");
    }
    
    @Test
    public void should_throw_entity_does_not_exists_exception_when_value_not_found() {
        when(entityManager.find(((Class<?>) any(Class.class)), anyLong())).thenReturn(null);
        try {
            sut.find(Customer.class, 24L);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(EntityDoesNotExistsException.class);
            assertThat(ex.getMessage()).isEqualTo("Customer with code=24 does not exists.");
        }
        
    }
    
    private List<Customer> buildCustomerList(int size) {
        return IntStream.range(0, size).mapToObj(this::buildCustomerMock).collect(Collectors.toList());
    }
    
    private Customer buildCustomerMock(long id) {
        Customer mock = mock(Customer.class);
        when(mock.getId()).thenReturn(id);
        return mock;
    }
    
    private void assertExpectingModelException(String requestedModelName, String expected) {
        try {
            //When
            sut.findByClassNameAndId(requestedModelName, 54l, null);
        } catch (Exception ex) {
            //Expected
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo(expected);
        }
    }
    
    private ArgumentCaptor<Class> getModelNameCaptor(String modelName) {
        ArgumentCaptor<Class> captor = ArgumentCaptor.forClass(Class.class);
        sut.findByClassNameAndId(modelName, 54L, this.genericRequestDto);
        verify(sut).find(captor.capture(), eq(54l));
        return captor;
    }
    
    private void configureFindMock() {
        when(entityManager.find(((Class<Object>) any(Class.class)), anyLong())).thenReturn(mock(BaseEntity.class));
    }
}