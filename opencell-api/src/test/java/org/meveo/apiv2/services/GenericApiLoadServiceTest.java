package org.meveo.apiv2.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.GenericPagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.services.generic.GenericApiLoadService;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenericApiLoadServiceTest {

    @Spy
    @InjectMocks
    private GenericApiLoadService sut;

    @Mock
    private EntityManagerWrapper entityManagerWrapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private PersistenceService persistenceService;

    @Before
    public void setUp() {
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
        configureFindMock();
        doReturn(persistenceService).when(sut).getPersistenceService(any());
        when(persistenceService.list(any(PaginationConfiguration.class))).thenReturn(new ArrayList<>());
    }

    @Test
    public void given_an_empty_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException("", "The entityName should not be null or empty");
    }

    @Test
    public void given_null_id_when_load_model_then_should_throw_wrong_requested_model_id_exception() {
        try {
            //When
            sut.findByClassNameAndId("Tax", null, null);
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

   /*  @Test
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

   /* @Test
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
        assertThat(filteredFieldsAndValues.get("addressbook")).isEqualTo("{\"code\":\"\",\"id\":5}");
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
    }*/

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

    @Test
    public void given_null_entity_name_when_find_paginate_recprds_then_should_throw_meveo_exception() {
        //Given
        String entityName = null;
        GenericPagingAndFiltering searchConfig = new GenericPagingAndFiltering();
        assertFindPaginateRecords(entityName, searchConfig, EntityDoesNotExistsException.class, "The entityName should not be null or empty");
    }

    @Test
    public void given_empty_entity_name_when_find_paginate_records_then_should_throw_meveo_exception() {
        //Given
        String entityName = "";
        GenericPagingAndFiltering searchConfig = new GenericPagingAndFiltering();
        assertFindPaginateRecords(entityName, searchConfig, EntityDoesNotExistsException.class, "The entityName should not be null or empty");
    }

    @Test
    public void should_transform_paging_and_filtering_to_pagination_configuration() {
        //Given
        HashMap<String, Object> map = new HashMap<>();
        String textFilter = "fulltest";
        String fields = "fields";
        int offset = 5;
        int limit = 15;
        String sortBy = "flirtikit";
        SortOrder order = SortOrder.DESCENDING;
        String encodedQuery = "flirtikit:5|bidlidz:gninendiden";
        GenericPagingAndFiltering searchConfig = new GenericPagingAndFiltering(textFilter, map, fields, offset, limit, sortBy, order);
        //When
        /*PaginationConfiguration paginationConfiguration = sut.paginationConfiguration(searchConfig);
        //Then
        assertThat(paginationConfiguration.getFirstRow()).isEqualTo(offset);
        assertThat(paginationConfiguration.getNumberOfRows()).isEqualTo(limit);
        assertThat(paginationConfiguration.getSortField()).isEqualTo(sortBy);
        assertThat(paginationConfiguration.getOrdering().name()).isEqualTo(order.name());*/
    }

    @Test
    public void should_return_valid_list_of_customers() {
        //Given
        List<Customer> customers = buildCustomerList(2);
        GenericPagingAndFiltering genericPagingAndFiltering = new GenericPagingAndFiltering("fulltest", new HashMap<>(), "addressbook", 0, 3, "id", SortOrder.DESCENDING);
        when(persistenceService.list(any(PaginationConfiguration.class))).thenReturn(customers);
        //When
        String response = sut.findPaginatedRecords("customer", genericPagingAndFiltering);
        //Then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(5);
    }

    private void assertFindPaginateRecords(String entityName, GenericPagingAndFiltering searchConfig, Class exception, String message) {
        try {
            //When
            sut.findPaginatedRecords(entityName, searchConfig);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(exception);
            assertThat(ex.getMessage()).isEqualTo(message);
        }
    }

    private List<Customer> buildCustomerList(int size) {
        return IntStream.range(0, size).mapToObj(this::buildCustomerMock).collect(Collectors.toList());
    }

    private Customer buildCustomerMock(long id) {
        Customer customer = new Customer();
        customer.setId(id);
        AddressBook addressbook = new AddressBook();
        addressbook.setId(id);
        customer.setAddressbook(addressbook);
        return customer;
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
        sut.findByClassNameAndId(modelName, 54L, null);
        verify(sut).find(captor.capture(), eq(54l));
        return captor;
    }

    private void configureFindMock() {
        when(entityManager.find(((Class<Object>) any(Class.class)), anyLong())).thenReturn(mock(BaseEntity.class));
    }
}
