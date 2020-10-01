package org.meveo.apiv2.services.generic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.FileType;
import org.meveo.model.admin.User;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        //Given
        Object param = BigDecimal.valueOf(12345.05);
        //When
        String expected = jsonGenericMapper.toJson(null, BigDecimal.class, param);
        //Then
        assertThat(expected).isEqualTo("12345.05");
    }


    @Test
    public void should_toJson_return_as_list_representation_of_ids_when_param_is_a_collection() {
        //Given
        Object param = buildCustomerList(3);
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("id"));
        String expected = jsonGenericMapper.toJson(fields, Customer.class, param);
        //Then
        assertThat(expected).isEqualTo("[0,1,2]");
    }

    @Test
    public void should_toJson_return_the_id_of_requested_entity_when_param_is_an_instance_of_base_entity() {
        //Given
        Object param = buildCustomerMock(55);
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("code","id", "defaultLevel","accountType", "addressbook", "addressbook.id"));
        String expected = jsonGenericMapper.toJson(fields, Customer.class, param);
        //Then
        assertThat(expected).isEqualTo("{\"id\":55,\"defaultLevel\":true,\"accountType\":\"ACCT_CUST\",\"addressbook\":{\"id\":55}}");
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
        String expected = jsonGenericMapper.toJson(null, Address.class, param);
        //Then
        String expectedFormattedJson = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\",\"code\":\"xxx\"}}";
        assertThat(expected).isEqualTo(expectedFormattedJson);
    }

    @Test
    public void should_transform_user_by_giving_its_id_and_username() {
        //Given
        User user = new User();
        user.setId(55L);
        user.setUserName("flirtikit");
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("id","userName"));
        String userJson = jsonGenericMapper.toJson(fields, User.class, user);
        //Then
        assertThat(userJson).isEqualTo("{\"id\":55,\"userName\":\"flirtikit\"}");

    }

    @Test
    public void should_transform_name_with_title() {
        //Given
        Name name = new Name();
        name.setFirstName("Flirtikit");
        name.setLastName("Bidlidez");
        Title title = new Title();
        title.setCode("codigo");
        name.setTitle(title);
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("firstName","lastName","title","title.code"));
        String userJson = jsonGenericMapper.toJson(fields, Name.class,name);
        //Then
        assertThat(userJson).isEqualTo("{\"title\":{\"code\":\"codigo\"},\"firstName\":\"Flirtikit\",\"lastName\":\"Bidlidez\"}");
    }

    @Test
    public void should_transform_entity_by_giving_its_id_and_export_identifier() {
        //Given
        InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setCode("invoiceCode");

        Country country = new Country();
        country.setCountryCode("Monaco");
        TradingCountry tradingCountry = new TradingCountry();
        tradingCountry.setCountry(country);
        tradingCountry.setId(123l);

        Tax tax = new Tax();
        tax.setCode("T.V.A.");
        tax.setId(456l);
        tax.setPercent(BigDecimal.ONE);
        invoiceSubcategoryCountry.setInvoiceSubCategory(invoiceSubCategory);
        invoiceSubcategoryCountry.setTradingCountry(tradingCountry);
        invoiceSubcategoryCountry.setTax(tax);
        invoiceSubcategoryCountry.setStartValidityDate(getDefaultDate());
        invoiceSubcategoryCountry.setEndValidityDate(getDefaultDate());

        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("tax","invoiceSubCategory","endValidityDate","invoiceSubCategory.code","tradingCountry","TradingCountry.country","country.countryCode"));
        String userJson = jsonGenericMapper.toJson(fields, InvoiceSubcategoryCountry.class, invoiceSubcategoryCountry);
        //Then
        assertThat(userJson).isEqualTo("{\"invoiceSubCategory\":{\"code\":\"invoiceCode\"},\"tradingCountry\":{\"id\":123,\"country\":{\"countryCode\":\"Monaco\",\"code\":\"Monaco\"}},\"tax\":{\"id\":456,\"code\":\"T.V.A.\"},\"endValidityDate\":1546297200000}");
    }

    @Test
    public void should_serialize_and_deserialize_date() throws IOException {
        //Given
        InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
        invoiceSubcategoryCountry.setStartValidityDate(getDefaultDate());
        invoiceSubcategoryCountry.setEndValidityDate(getDefaultDate());

        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("endValidityDate","startValidityDate"));
        String userJson = jsonGenericMapper.toJson(fields, InvoiceSubcategoryCountry.class, invoiceSubcategoryCountry);
        InvoiceSubcategoryCountry parsedInvoiceSubcategoryCountry = jsonGenericMapper.readValue(userJson, InvoiceSubcategoryCountry.class);
        //Then
        assertThat(parsedInvoiceSubcategoryCountry.getEndValidityDate()).isEqualTo(invoiceSubcategoryCountry.getEndValidityDate());
        assertThat(parsedInvoiceSubcategoryCountry.getStartValidityDate()).isEqualTo(invoiceSubcategoryCountry.getStartValidityDate());
    }

    @Test
    public void should_serialize_cf_value() {
        //Given
        ServiceTemplate serviceTemplate = new ServiceTemplate();
        serviceTemplate.setId(1234L);

        Map cf_values = new HashMap<>();
        cf_values.put("objectID", 123L);

        serviceTemplate.setCfValue("cf_serviceTemplate", cf_values);
        //When
        HashSet<String> fields = new HashSet<>();
        fields.add("cfValues");
        String serviceTemplateJson = jsonGenericMapper.toJson(null, ServiceTemplate.class, serviceTemplate);
        jsonGenericMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        //Then
        assertTrue(serviceTemplateJson.contains("\"cf_serviceTemplate\":[{\"priority\":0,\"value\":{\"objectID\":123}"));
    }

    @Test
    public void should_transform_title_into_title_with_code() {
        //Given
        Title title = new Title();
        title.setCode("MR.");
        Name name = new Name();
        name.setFirstName("aaa");
        name.setLastName("bbb");
        name.setTitle(title);
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("firstName","lastName","title","title.code"));
        String transform = jsonGenericMapper.toJson(fields, Name.class, name);
        assertThat(transform).isEqualTo("{\"title\":{\"code\":\"MR.\"},\"firstName\":\"aaa\",\"lastName\":\"bbb\"}");

    }

    @Test
    public void should_return_only_ids_of_referenced_entities_code() {
        JsonGenericMapper jsonGenericMapper1 = JsonGenericMapper.Builder.getBuilder().build();
        //Given
        OfferTemplate offerTemplate=new OfferTemplate();
        OfferTemplate offerTemplate1=new OfferTemplate();
        OfferTemplate offerTemplate2=new OfferTemplate();

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
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .total(3l).limit(0l).offset(0l)
                .addData(offerTemplate, offerTemplate1, offerTemplate2).build();
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("channels"));
        String transform = jsonGenericMapper1.toJson(fields, offerTemplate.getClass(), immutableGenericPaginatedResource);
        assertThat(transform).isEqualTo("{\"total\":3,\"limit\":0,\"offset\":0,\"data\":[{\"channels\":[1,2,3]},{\"channels\":[1,2,3]},{\"channels\":[1,2,3]}]}");
    }

    @Test
    public void should_return_the_fields_of_referenced_entities_code() {
        HashSet<String> nestedEntities = new HashSet<>();
        nestedEntities.add("channels");
        JsonGenericMapper jsonGenericMapper1 = JsonGenericMapper.Builder.getBuilder().withNestedEntities(nestedEntities).build();
        //Given
        OfferTemplate offerTemplate=new OfferTemplate();

        Channel channel1 = new Channel();
        channel1.setId(1l);
        Channel channel2 = new Channel();

        offerTemplate.setChannels(Arrays.asList(channel1, channel2));
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder()
                .total(1l).limit(0l).offset(0l)
                .addData(offerTemplate).build();
        //When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("channels"));
        String transform = jsonGenericMapper1.toJson(fields, offerTemplate.getClass(), immutableGenericPaginatedResource);
        assertThat(transform).isEqualTo("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"channels\":[{\"id\":1,\"historized\":false,\"notified\":false,\"appendGeneratedCode\":false,\"disabled\":false,\"active\":true},{\"historized\":false,\"notified\":false,\"appendGeneratedCode\":false,\"disabled\":false,\"active\":true}]}]}");
    }

    @Test
    public void should_transform_cyclic_reference_without_stackoverflow_error() throws IOException {
        //Given
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

        //When
        HashSet<String> fields = new HashSet<>();
        String transform = jsonGenericMapper.toJson(fields, Subscription.class, subscription);
        assertTrue(transform.contains("\"serviceInstances\":[456]"));
    }
    @Test
    public void should_correctly_parse_referenced_ids_From_Json() throws IOException {
        //Given
        JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
        String jsonDto = "{\"code\":\"test\", \"fileTypes\":[5], \"inputDirectory\":\"/test\"}";
        //When
        FileType databaseFetchedFileType = new FileType();
        databaseFetchedFileType.setId(5L);
        databaseFetchedFileType.setCode("test");
        databaseFetchedFileType.setDescription("test descr");

        FileFormat resultingEntity = (FileFormat) jsonGenericMapper.parseFromJson(jsonDto, FileFormat.class);
        assertTrue(resultingEntity.getFileTypes().get(0).getId().equals(5l));
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