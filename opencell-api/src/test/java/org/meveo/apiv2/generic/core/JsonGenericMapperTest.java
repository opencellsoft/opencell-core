package org.meveo.apiv2.generic.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.model.DatePeriod;
import org.meveo.model.IEntity;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.FileType;
import org.meveo.model.admin.User;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.model.tax.TaxMapping;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class JsonGenericMapperTest {
    private JsonGenericMapper jsonGenericMapper;

    @Before
    public void setUp() {
        jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
    }

    @Test
    public void should_toJson_return_the_string_representation_when_param_is_big_decimal() {
        // Given
        Object param = BigDecimal.valueOf(12345.05);
        // When
        String expected = jsonGenericMapper.toJson(null, BigDecimal.class, param);
        // Then
        assertThat(expected).isEqualTo("12345.05");
    }

    @Test
    public void should_toJson_return_as_list_representation_of_ids_when_param_is_a_collection() {
        // Given
        Object param = buildCustomerList(3);
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("id"));
        String expected = jsonGenericMapper.toJson(fields, Customer.class, param);
        // Then
        assertThat(expected).isEqualTo("[{\"id\":0},{\"id\":1},{\"id\":2}]");
    }

    @Test
    public void should_toJson_return_the_id_of_requested_entity_when_param_is_an_instance_of_base_entity() {
        // Given
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().withNestedEntities(new HashSet<>(Arrays.asList("accountType","addressbook"))).build();
        IEntity param = buildCustomerMock(55);
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("code", "id", "defaultLevel", "accountType", "addressbook", "addressbook.id"));
        String expected = jsonGenericMapper.toJson(fields, Customer.class, ImmutableGenericPaginatedResource.builder().addData(param).total(1L).limit(100L).offset(0L).build());
        // Then
        assertThat(expected).isEqualTo("{\"total\":1,\"limit\":100,\"offset\":0,\"data\":[{\"id\":55,\"defaultLevel\":true,\"accountType\":\"ACCT_CUST\",\"addressbook\":{\"id\":55}}]}");
    }

    @Test
    public void should_transform_user_by_giving_its_id_and_username() {
        // Given
        User user = new User();
        user.setId(55L);
        user.setUserName("flirtikit");
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("id", "userName"));
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(user).build();
        String userJson = jsonGenericMapper.toJson(fields, User.class, immutableGenericPaginatedResource);
        // Then
        assertThat(userJson).isEqualTo("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"id\":55,\"userName\":\"flirtikit\"}]}");

    }


    @Test
    public void should_transform_entity_by_giving_its_id_and_export_identifier() { //todo : use this UT to fix genericField
        // Given
        TaxMapping taxMapping = new TaxMapping();

        Country country = new Country();
        country.setCountryCode("Monaco");
        TradingCountry buyerCountry = new TradingCountry();
        buyerCountry.setCountry(country);
        buyerCountry.setId(123l);

        TradingCountry sellerCountry = new TradingCountry();
        sellerCountry.setCountry(country);
        sellerCountry.setId(124l);

        TaxCategory taxCategory = new TaxCategory();
        taxCategory.setCode("Cat1");
        taxCategory.setId(15L);

        TaxClass taxClass = new TaxClass();
        taxClass.setCode("class 15");
        taxClass.setId(148L);

        Tax tax = new Tax();
        tax.setCode("T.V.A.");
        tax.setId(456l);
        tax.setPercent(BigDecimal.ONE);
        taxMapping.setAccountTaxCategory(taxCategory);
        taxMapping.setChargeTaxClass(taxClass);
        taxMapping.setBuyerCountry(buyerCountry);
        taxMapping.setSellerCountry(sellerCountry);
        taxMapping.setTax(tax);
        taxMapping.setValid(new DatePeriod(getDefaultDate(), getDefaultDate()));

        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("tax", "accountTaxCategory", "valid", "buyerCountry", "sellerCountry"));
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(taxMapping).build();
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().withNestedEntities(new HashSet<>(Arrays.asList("accountTaxCategory", "buyerCountry"))).build();
        String userJson = jsonGenericMapper.toJson(fields, TaxMapping.class, immutableGenericPaginatedResource);
        // Then
//        assertThat(userJson).isEqualTo(
//            "{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"accountTaxCategory\":{\"id\":15,\"historized\":false,\"notified\":false,\"code\":\"Cat1\",\"appendGeneratedCode\":false,\"uuid\":\"13757921-3497-47f6-9bf6-4381e3819f7d\",\"descriptionOrCode\":\"Cat1\",\"referenceCode\":\"Cat1\"},\"valid\":{\"from\":\"2019-01-01T00:00:00+01:00\",\"to\":\"2019-01-01T00:00:00+01:00\"},\"sellerCountry\":{\"id\":124},\"buyerCountry\":{\"id\":123,\"historized\":false,\"notified\":false,\"appendGeneratedCode\":false,\"uuid\":\"23f38c37-c5a2-4750-8863-9b1ada190191\",\"disabled\":false,\"country\":{\"id\":null},\"active\":true},\"tax\":{\"id\":456}}]}");
    }

    @Test
    public void should_serialize_and_deserialize_date() throws IOException {
        // Given
        DatePeriod datePeriod = new DatePeriod(getDefaultDate(), getDefaultDate());

        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("from", "to"));
        String userJson = jsonGenericMapper.toJson(fields, DatePeriod.class, datePeriod);
        DatePeriod parsedDatePeriod = jsonGenericMapper.readValue(userJson, DatePeriod.class);
        // Then
        assertThat(parsedDatePeriod.getFrom()).isEqualTo(datePeriod.getFrom());
        assertThat(parsedDatePeriod.getTo()).isEqualTo(datePeriod.getTo());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void should_serialize_cf_value() {
        // Given
        ServiceTemplate serviceTemplate = new ServiceTemplate();
        serviceTemplate.setId(1234L);

        Map cf_values = new HashMap<>();
        cf_values.put("objectID", 123L);

        serviceTemplate.setCfValue("cf_serviceTemplate", cf_values);
        // When
        HashSet<String> fields = new HashSet<>();
        fields.add("cfValues");
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(serviceTemplate).build();
        String serviceTemplateJson = jsonGenericMapper.toJson(null, ServiceTemplate.class, immutableGenericPaginatedResource);
        jsonGenericMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // Then
        assertTrue(serviceTemplateJson.contains("\"cf_serviceTemplate\":[{\"priority\":0,\"value\":{\"objectID\":123}"));
    }

    @Test
    public void should_return_only_ids_of_referenced_entities_code() {
        JsonGenericMapper jsonGenericMapper1 = JsonGenericMapper.Builder.getBuilder().build();
        // Given
        OfferTemplate offerTemplate = new OfferTemplate();
        OfferTemplate offerTemplate1 = new OfferTemplate();
        OfferTemplate offerTemplate2 = new OfferTemplate();

        Channel channel1 = new Channel();
        channel1.setId(1l);
        channel1.setCode("code-ch-1");
        Channel channel2 = new Channel();
        channel2.setId(2l);
        channel2.setCode("code-ch-2");
        Channel channel3 = new Channel();
        channel3.setId(3l);
        channel3.setCode("code-ch-3");

        offerTemplate.setChannels(Arrays.asList(channel1, channel2, channel3));
        offerTemplate1.setChannels(Arrays.asList(channel1, channel2, channel3));
        offerTemplate2.setChannels(Arrays.asList(channel1, channel2, channel3));
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(3l).limit(0l).offset(0l).addData(offerTemplate, offerTemplate1, offerTemplate2).build();
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("channels"));
        String transform = jsonGenericMapper1.toJson(fields, offerTemplate.getClass(), immutableGenericPaginatedResource);
        assertThat(transform).isEqualTo("{\"total\":3,\"limit\":0,\"offset\":0,\"data\":[{\"channels\":[{\"id\":1},{\"id\":2},{\"id\":3}]},{\"channels\":[{\"id\":1},{\"id\":2},{\"id\":3}]},{\"channels\":[{\"id\":1},{\"id\":2},{\"id\":3}]}]}");
    }

    @Test
    public void should_return_the_fields_of_referenced_entities_code() {
        HashSet<String> nestedEntities = new HashSet<>();
        nestedEntities.add("channels");
        JsonGenericMapper jsonGenericMapper1 = JsonGenericMapper.Builder.getBuilder().withNestedEntities(nestedEntities).build();
        // Given
        OfferTemplate offerTemplate = new OfferTemplate();

        Channel channel1 = new Channel();
        channel1.setId(1l);
        channel1.setCode("c1");
        Channel channel2 = new Channel();
        channel2.setId(2l);
        channel2.setCode("c2");
        offerTemplate.setChannels(Arrays.asList(channel1, channel2));
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(offerTemplate).build();
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("channels"));
        String transform = jsonGenericMapper1.toJson(fields, offerTemplate.getClass(), immutableGenericPaginatedResource);
        assertThat(transform).isEqualTo("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"channels\":[{\"id\":1,\"historized\":false,\"notified\":false,\"code\":\"c1\",\"appendGeneratedCode\":false,\"disabled\":false,\"active\":true,\"descriptionOrCode\":\"c1\",\"referenceCode\":\"c1\"},{\"id\":2,\"historized\":false,\"notified\":false,\"code\":\"c2\",\"appendGeneratedCode\":false,\"disabled\":false,\"active\":true,\"descriptionOrCode\":\"c2\",\"referenceCode\":\"c2\"}]}]}");
    }

    @Test
    public void should_transform_cyclic_reference_without_stackoverflow_error() throws IOException {
        // Given
        Subscription subscription = new Subscription();
        ServiceInstance serviceInstance = new ServiceInstance();

        subscription.setId(123L);
        subscription.setCode("MY_SUBSCRIPTION");
        subscription.setDescription("my subscription description");

        serviceInstance.setId(456L);
        serviceInstance.setCode("MY_SERVICE_INSTANCE");
        serviceInstance.setDescription("my description service instance");

        subscription.setServiceInstances(Arrays.asList(serviceInstance));
        serviceInstance.setSubscription(subscription);
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(subscription).build();
        jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().withExtractList(true).build();
        // When
        HashSet<String> fields = new HashSet<>();
        String transform = jsonGenericMapper.toJson(fields, Subscription.class, immutableGenericPaginatedResource);
        assertTrue(transform.contains("\"serviceInstances\":[{\"id\":456}]"));
    }

    @Test
    public void should_correctly_parse_referenced_ids_From_Json() throws IOException {
        // Given
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
        String jsonDto = "{\"code\":\"test\", \"fileTypes\":[5], \"inputDirectory\":\"/test\"}";
        // When
        FileType databaseFetchedFileType = new FileType();
        databaseFetchedFileType.setId(5L);
        databaseFetchedFileType.setCode("test");
        databaseFetchedFileType.setDescription("test descr");

        FileFormat resultingEntity = (FileFormat) jsonGenericMapper.parseFromJson(jsonDto, FileFormat.class);
        assertTrue(resultingEntity.getFileTypes().get(0).getId().equals(5l));
    }

    @Test
    public void serialize_customer_account() {
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setId(1L);
        customerAccount.setCode("CA");
        BillingAccount ba = new BillingAccount();
        ba.setId(2L);
        ba.setCode("BA");
        UserAccount ua = new UserAccount();
        ua.setId(3L);
        ua.setCode("UA");
        ua.setBillingAccount(ba);
        Subscription subscription = new Subscription();
        subscription.setId(4L);
        subscription.setCode("SUB");
        ua.setSubscriptions(singletonList(subscription));
        ba.setUsersAccounts(singletonList(ua));
        customerAccount.setBillingAccounts(singletonList(ba));

        JsonGenericMapper jsonMapper = JsonGenericMapper.Builder.getBuilder()
                .withNestedDepth(1L)
                .withNestedEntities(Set.of("billingAccounts", "billingAccounts.usersAccounts")).build();

        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(customerAccount).build();

        jsonMapper.toJson(Set.of(), CustomerAccount.class, immutableGenericPaginatedResource);
    }

    private Date getDefaultDate() {
        return Date.from(LocalDate.of(2019, 01, 01).atStartOfDay(ZoneId.systemDefault()).toInstant());
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
}