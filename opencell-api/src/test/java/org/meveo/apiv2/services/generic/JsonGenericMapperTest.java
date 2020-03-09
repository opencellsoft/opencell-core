/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.services.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.generic.ImmutableGenericPaginatedResource;
import org.meveo.apiv2.services.generic.JsonGenericApiMapper.JsonGenericMapper;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.FileType;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.model.tax.TaxCategory;
import org.meveo.model.tax.TaxClass;
import org.meveo.model.tax.TaxMapping;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.DeserializationFeature;

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
        assertThat(expected).isEqualTo("[0,1,2]");
    }

    @Test
    public void should_toJson_return_the_id_of_requested_entity_when_param_is_an_instance_of_base_entity() {
        // Given
        Object param = buildCustomerMock(55);
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("code", "id", "defaultLevel", "accountType", "addressbook", "addressbook.id"));
        String expected = jsonGenericMapper.toJson(fields, Customer.class, param);
        // Then
        assertThat(expected).isEqualTo("{\"id\":55,\"defaultLevel\":true,\"accountType\":\"ACCT_CUST\",\"addressbook\":{\"id\":55}}");
    }

    @Test
    public void should_toJson_return_the_string_representation_when_param_is_an_non_entity_business_object() {
        // Given
        Address param = new Address();
        param.setAddress1("address1");
        param.setZipCode("75002");
        param.setCity("Paris");
        Country country = new Country();
        country.setDescription("Very beautyfull country");
        country.setCountryCode("xxx");
        param.setCountry(country);

        // When
        String expected = jsonGenericMapper.toJson(null, Address.class, param);
        // Then
        String expectedFormattedJson = "{\"address1\":\"address1\",\"zipCode\":\"75002\",\"city\":\"Paris\",\"country\":{\"countryCode\":\"xxx\",\"description\":\"Very beautyfull country\",\"code\":\"xxx\"},\"countryBundle\":\"countries.Very beautyfull country\"}";
        assertThat(expected).isEqualTo(expectedFormattedJson);
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
        String userJson = jsonGenericMapper.toJson(fields, User.class, user);
        // Then
        assertThat(userJson).isEqualTo("{\"id\":55,\"userName\":\"flirtikit\"}");

    }

    @Test
    public void should_transform_name_with_title() {
        // Given
        Name name = new Name();
        name.setFirstName("Flirtikit");
        name.setLastName("Bidlidez");
        Title title = new Title();
        title.setCode("codigo");
        name.setTitle(title);
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("firstName", "lastName", "title", "title.code"));
        String userJson = jsonGenericMapper.toJson(fields, Name.class, name);
        // Then
        assertThat(userJson).isEqualTo("{\"title\":{\"code\":\"codigo\"},\"firstName\":\"Flirtikit\",\"lastName\":\"Bidlidez\"}");
    }

    @Test
    public void should_transform_entity_by_giving_its_id_and_export_identifier() {
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
        fields.addAll(Arrays.asList("tax", "accountTaxCategory", "valid", "DatePeriod.to", "buyerCountry", "TradingCountry.country", "country.countryCode"));
        String userJson = jsonGenericMapper.toJson(fields, TaxMapping.class, taxMapping);
        // Then
        assertThat(userJson).isEqualTo(
            "{\"accountTaxCategory\":{\"id\":15,\"code\":\"Cat1\"},\"valid\":{\"to\":\"2019-01-01T00:00:00-03:00\"},\"buyerCountry\":{\"id\":123,\"country\":{\"countryCode\":\"Monaco\",\"code\":\"Monaco\"}},\"tax\":{\"id\":456,\"code\":\"T.V.A.\"}}");
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
        String serviceTemplateJson = jsonGenericMapper.toJson(null, ServiceTemplate.class, serviceTemplate);
        jsonGenericMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // Then
        assertTrue(serviceTemplateJson.contains("\"cf_serviceTemplate\":[{\"priority\":0,\"value\":{\"objectID\":123}"));
    }

    @Test
    public void should_transform_title_into_title_with_code() {
        // Given
        Title title = new Title();
        title.setCode("MR.");
        Name name = new Name();
        name.setFirstName("aaa");
        name.setLastName("bbb");
        name.setTitle(title);
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("firstName", "lastName", "title", "title.code"));
        String transform = jsonGenericMapper.toJson(fields, Name.class, name);
        assertThat(transform).isEqualTo("{\"title\":{\"code\":\"MR.\"},\"firstName\":\"aaa\",\"lastName\":\"bbb\"}");

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
        assertThat(transform).isEqualTo("{\"total\":3,\"limit\":0,\"offset\":0,\"data\":[{\"channels\":[1,2,3]},{\"channels\":[1,2,3]},{\"channels\":[1,2,3]}]}");
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
        Channel channel2 = new Channel();

        offerTemplate.setChannels(Arrays.asList(channel1, channel2));
        ImmutableGenericPaginatedResource immutableGenericPaginatedResource = ImmutableGenericPaginatedResource.builder().total(1l).limit(0l).offset(0l).addData(offerTemplate).build();
        // When
        HashSet<String> fields = new HashSet<>();
        fields.addAll(Arrays.asList("channels"));
        String transform = jsonGenericMapper1.toJson(fields, offerTemplate.getClass(), immutableGenericPaginatedResource);
        assertThat(transform).isEqualTo(
            "{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"channels\":[{\"id\":1,\"historized\":false,\"notified\":false,\"appendGeneratedCode\":false,\"disabled\":false,\"active\":true,\"codeChanged\":false,\"transient\":false},{\"historized\":false,\"notified\":false,\"appendGeneratedCode\":false,\"disabled\":false,\"active\":true,\"codeChanged\":false,\"transient\":true}]}]}");
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

        // When
        HashSet<String> fields = new HashSet<>();
        String transform = jsonGenericMapper.toJson(fields, Subscription.class, subscription);
        assertTrue(transform.contains("\"serviceInstances\":[456]"));
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