package org.meveo.service.generic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.WrongModelNameException;
import org.meveo.admin.exception.WrongRequestedModelIdException;
import org.meveo.api.dto.generic.GenericRequestDto;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericServiceTest {
    @Spy
    @InjectMocks
    private GenericService sut;

    @Mock
    private GenericRequestDto genericRequestDto;


    @Test
    public void given_an_empty_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException("", "The requested model should not be empty");
    }

    @Test
    public void given_null_id_when_load_model_then_should_throw__wrong_requested_model_id_exception() {
        try {
            //When
            sut.findBy("flirtikit", null, genericRequestDto);
            //Then
        } catch (Exception ex) {
            //Expected
            assertThat(ex).isInstanceOf(WrongRequestedModelIdException.class);
            assertThat(ex.getMessage()).isEqualTo( "Wrong requested id: null");
        }
    }

    @Test
    public void given_null_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException(null, "The requested model should not be null");
    }

    @Test
    public void given_an_unrecognizable_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException("flirtikit", "Wrong requested model: flirtikit");
    }

    @Test
    public void given_null_dto_when_init_fields_then_should_return_empty_list() {
        //When
        List<String> fields = sut.getOrInitFields(null);
        //Then
        assertThat(fields).isNotNull();
        assertThat(fields).isEmpty();
    }

    @Test
    public void given_non_null_dto_when_init_fields_then_should_return_empty_list() {
        //Given
        GenericRequestDto dto = new GenericRequestDto();
        //When
        List<String> fields = sut.getOrInitFields(dto);
        //Then
        assertThat(fields).isNotNull();
        assertThat(fields).isEmpty();
    }

    @Test
    public void given_lower_case_model_name_when_load_model_then_should_return_correct_entity_class() {
        //Given
        String modelName = "customeraccount";
        //When
        Class modelClass = sut.loadModelClassNameBy(modelName);
        //Then
        assertThat(modelClass).isEqualTo(CustomerAccount.class);
        assertThat(modelClass.getAnnotation(Entity.class)).isNotNull();

    }

    @Test
    public void given_any_case_model_name_when_load_model_then_should_return_correct_entity_class() {
        //Given
        String modelName = "cUsToMeRaCcOuNt";
        //When
        Class modelClass = sut.loadModelClassNameBy(modelName);
        //Then
        assertThat(modelClass).isEqualTo(CustomerAccount.class);
        assertThat(modelClass.getAnnotation(Entity.class)).isNotNull();

    }

    @Test
    public void should_create_an_standardized_request_on_entity() {
        //Given
        Class entityClass = Customer.class;
        //When
        String request = sut.buildRecordRequest(entityClass);
        //Then
        assertThat(request).isNotNull();
        assertThat(request).isEqualTo("select customer from Customer customer where customer.id = :id");

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
        //Then
        assertThat(expected).isEqualTo("flirtikit");
    }

    @Test
    public void given_a_dot_separated_sub_property_field_name_when_extract_field_name_then_return_the_first_part_splitted_property() {
        //Given
        String fieldName = "addressbook.id";
        //When
        String extracted = sut.extractFieldName(fieldName);
        //Then
        assertThat(extracted).isEqualTo("addressbook");
    }

    @Test
    public void given_a_regular_field_name_when_extract_field_name_then_return_the_property() {
        //Given
        String fieldName = "addressbook";
        //When
        String extracted = sut.extractFieldName(fieldName);
        //Then
        assertThat(extracted).isEqualTo("addressbook");
    }

    @Test
    public void given_empty_fields_list_get_all_non_static_field_values_then_get_all_object_fields() {
        //Given
        Customer customer = new Customer();
        List<String> fields = Collections.emptyList();
        //When
        Set<String> filteredFields = sut.buildGenericRespose(customer, fields).getValue().keySet();
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
        Set<String> filteredFields = sut.buildGenericRespose(customer, fields).getValue().keySet();
        //Then
        assertThat(filteredFields).isEmpty();
    }

    @Test
    public void given_rendom_fields_list_get_all_non_static_field_values_then_get_only_recognized_fields() {
        //Given
        Customer customer = new Customer();
        List<String> fields = Arrays.asList("flirtikit", "bidlidez", "addressbook", "customercategory", "gninendiden");
        //When
        Set<String> filteredFields = sut.buildGenericRespose(customer, fields).getValue().keySet();
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
        Set<String> filteredFields = sut.buildGenericRespose(customer, fields).getValue().keySet();
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
        Map<String, String> filteredFieldsAndValues = sut.buildGenericRespose(customer, fields).getValue();
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
            sut.loadModelClassNameBy(requestedModelName);
        } catch (Exception ex) {
            //Expected
            assertThat(ex).isInstanceOf(WrongModelNameException.class);
            assertThat(ex.getMessage()).isEqualTo(expected);
        }
    }


}