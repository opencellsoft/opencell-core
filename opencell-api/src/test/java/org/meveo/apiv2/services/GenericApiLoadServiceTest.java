package org.meveo.apiv2.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.services.generic.GenericApiLoadService;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.base.PersistenceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
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
        doReturn(persistenceService).when(sut).getPersistenceService(any());
    }

    @Test
    public void given_an_empty_model_name_when_load_model_then_should_throw_wrong_model_name_exception() {
        assertExpectingModelException("", "The entityName should not be null or empty");
    }

    @Test
    public void given_null_id_when_load_model_then_should_throw_wrong_requested_model_id_exception() {
        try {
            //When
            sut.findByClassNameAndId("Tax", null, null, null);
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
    public void given_empty_fields_list_get_all_non_static_field_values_then_get_all_object_fields() {
        //Given
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName(new Name(new Title("title",false),"customerFirstName","customerLastName"));
        Set<String> fields = Collections.emptySet();
        //When
        PersistenceService<Customer> persistenceService = mock(PersistenceService.class);
        when(sut.getPersistenceService(any())).thenReturn(persistenceService);
        when(persistenceService.findById(anyLong(), anyList())).thenReturn(customer);
        PaginationConfiguration searchConfig = mock(PaginationConfiguration.class);
        when(searchConfig.getFetchFields()).thenReturn(Collections.emptyList());

        Optional<String> filteredFields = sut.findByClassNameAndId("Customer",1L, searchConfig, fields);
        //Then
        assertThat(filteredFields.get()).isEqualTo("{\"data\":{\"id\":1,\"name\":{\"title\":{\"code\":\"title\",\"isCompany\":false,\"descriptionNotNull\":\"title\"},\"firstName\":\"customerFirstName\",\"lastName\":\"customerLastName\",\"fullName\":\"title customerFirstName customerLastName\"},\"defaultLevel\":true,\"accountType\":\"ACCT_CUST\",\"parentEntityType\":\"org.meveo.model.admin.Seller\",\"contactInformationNullSafe\":{}}}");
    }

    @Test
    public void given_unrecognized_fields_list_get_all_non_static_field_values_then_get_empty_list() {
        //Given
        Customer customer = new Customer();
        Set<String> fields = new HashSet<>(Arrays.asList("flirtikit", "bidlidez", "gninendiden"));
        //When
        PersistenceService<Customer> persistenceService = mock(PersistenceService.class);
        when(sut.getPersistenceService(any())).thenReturn(persistenceService);
        when(persistenceService.findById(anyLong(), anyList())).thenReturn(customer);
        PaginationConfiguration searchConfig = mock(PaginationConfiguration.class);
        when(searchConfig.getFetchFields()).thenReturn(Collections.emptyList());

        Optional<String> filteredFields = sut.findByClassNameAndId("Customer",1L, searchConfig, fields);
        //Then
        assertThat(filteredFields.get()).isEqualTo("{\"data\":{}}");
    }

   @Test
    public void given_rendom_fields_list_get_all_non_static_field_values_then_get_only_recognized_fields() {
        //Given
        Customer customer = new Customer();
        customer.setAddressbook(mock(AddressBook.class));
        customer.setCustomerCategory(mock(CustomerCategory.class));
        Set<String> fields = new HashSet<>(Arrays.asList("flirtikit", "bidlidez", "addressbook", "customerCategory", "gninendiden"));
        //When
        PersistenceService<Customer> persistenceService = mock(PersistenceService.class);
        when(sut.getPersistenceService(any())).thenReturn(persistenceService);
        when(persistenceService.findById(anyLong(), anyList())).thenReturn(customer);
        PaginationConfiguration searchConfig = mock(PaginationConfiguration.class);
        when(searchConfig.getFetchFields()).thenReturn(Collections.emptyList());

        Optional<String> filteredFields = sut.findByClassNameAndId("Customer",1L, searchConfig, fields);
        //Then
        assertThat(filteredFields.get()).isNotEmpty();
        assertThat(filteredFields.get()).isNotEqualTo("{\"data\":{}}");
        assertThat(filteredFields.get()).containsSequence("addressbook", "customerCategory");
    }

    @Test
    public void given_empty_fields_list_get_all_non_static_field_values_then_get_only_non_static_fields() {
        //Given
        Customer customer = new Customer();
        Set<String> fields = Collections.emptySet();
        //When
        PersistenceService<Customer> persistenceService = mock(PersistenceService.class);
        when(sut.getPersistenceService(any())).thenReturn(persistenceService);
        when(persistenceService.findById(anyLong(), anyList())).thenReturn(customer);
        PaginationConfiguration searchConfig = mock(PaginationConfiguration.class);
        when(searchConfig.getFetchFields()).thenReturn(Collections.emptyList());

        Optional<String> filteredFields = sut.findByClassNameAndId("Customer",1L, searchConfig, fields);
        //Then
        assertThat(filteredFields.get()).doesNotContain("serialVersionUID");
        assertThat(filteredFields.get()).doesNotContain("ACCOUNT_TYPE");
    }

    @Test
    public void given_addressbook_field_when_get_all_non_static_field_values_then_should_return_a_map_containing_address_book_and_its_id() {
        //Given
        Customer customer = new Customer();
        AddressBook addressbook = new AddressBook();
        addressbook.setId(5L);
        customer.setAddressbook(addressbook);
        Set<String> fields = new HashSet<>(Collections.singletonList("addressbook"));
        //When
        PersistenceService<Customer> persistenceService = mock(PersistenceService.class);
        when(sut.getPersistenceService(any())).thenReturn(persistenceService);
        when(persistenceService.findById(anyLong(), anyList())).thenReturn(customer);
        PaginationConfiguration searchConfig = mock(PaginationConfiguration.class);
        when(searchConfig.getFetchFields()).thenReturn(Collections.emptyList());

        Optional<String> filteredFieldsAndValues = sut.findByClassNameAndId("Customer",1L, searchConfig, fields);
        //Then
        assertThat(filteredFieldsAndValues.get()).contains("addressbook");
        assertThat(filteredFieldsAndValues.get()).contains("\"addressbook\":{\"id\":5,");
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

    @Test
    public void given_null_entity_name_when_find_paginate_recprds_then_should_throw_meveo_exception() {
        //Given
        String entityName = null;
        PaginationConfiguration searchConfig = new PaginationConfiguration(Collections.emptyMap());
        assertFindPaginateRecords(null, searchConfig, null, EntityDoesNotExistsException.class, "The entityName should not be null or empty");
    }

    @Test
    public void given_empty_entity_name_when_find_paginate_records_then_should_throw_meveo_exception() {
        //Given
        String entityName = "";
        PaginationConfiguration searchConfig = new PaginationConfiguration(Collections.emptyMap());
        assertFindPaginateRecords(null, searchConfig, null, EntityDoesNotExistsException.class, "The entityName should not be null or empty");
    }

    @Test
    public void should_transform_paging_and_filtering_to_pagination_configuration() {
        //Given
        HashMap<String, Object> map = new HashMap<>();
        String textFilter = "fulltest";
        List<String> fields = Collections.singletonList("fields");
        int offset = 5;
        int limit = 15;
        String sortBy = "flirtikit";
        SortOrder order = SortOrder.DESCENDING;
        String encodedQuery = "flirtikit:5|bidlidz:gninendiden";
        PaginationConfiguration searchConfig = new PaginationConfiguration(offset, limit, map, textFilter, fields, sortBy,
                org.primefaces.model.SortOrder.valueOf(order.name()));
        //Then
        assertThat(searchConfig.getFirstRow()).isEqualTo(offset);
        assertThat(searchConfig.getNumberOfRows()).isEqualTo(limit);
        assertThat(searchConfig.getSortField()).isEqualTo(sortBy);
        assertThat(searchConfig.getOrdering().name()).isEqualTo(order.name());
    }

    @Test
    public void should_return_valid_list_of_customers() {
        //Given
        List<Customer> customers = buildCustomerList(2);
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration( 0, 3,
                new HashMap<>(),"fulltest", Collections.singletonList("addressbook"),
                "id", org.primefaces.model.SortOrder.valueOf(SortOrder.DESCENDING.name()));
        when(persistenceService.list(any(PaginationConfiguration.class))).thenReturn(customers);
        //When
        String response = sut.findPaginatedRecords(Customer.class, paginationConfiguration, new HashSet<>(paginationConfiguration.getFetchFields()), Collections.emptySet());
        //Then
        assertThat(response).contains("{\"id\":0,\"defaultLevel\":true,\"accountType\":\"ACCT_CUST\",\"addressbook\":{\"id\":0},\"parentEntityType\":\"org.meveo.model.admin.Seller\",\"contactInformationNullSafe\":{}},{\"id\":1,\"defaultLevel\":true,\"accountType\":\"ACCT_CUST\",\"addressbook\":{\"id\":1},\"parentEntityType\":\"org.meveo.model.admin.Seller\",\"contactInformationNullSafe\":{}}");
    }

    private void assertFindPaginateRecords(Class entityClass, PaginationConfiguration searchConfig, Set<String> genericFields, Class exception, String message) {
        try {
            //When
            sut.findPaginatedRecords(entityClass, searchConfig, genericFields, genericFields);
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
            sut.findByClassNameAndId(requestedModelName, 54l, null, null);
        } catch (Exception ex) {
            //Expected
            assertThat(ex).isInstanceOf(MeveoApiException.class);
            assertThat(ex.getMessage()).isEqualTo(expected);
        }
    }
}

