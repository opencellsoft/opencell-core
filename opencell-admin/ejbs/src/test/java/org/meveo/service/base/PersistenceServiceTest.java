package org.meveo.service.base;

import org.junit.Before;
import org.junit.Test;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.Invoice;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.service.base.PersistenceService.*;

public class PersistenceServiceTest {

    private PersistenceService persistenceService;
    private Map<String, Object> filters = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        persistenceService = new PersistenceServiceMock();
    }

    @Test
    public void test_empty_filters() {
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField");
    }

    @Test
    public void test_search_att_type_class_list_class() {
        filters.put("eq " + SEARCH_ATTR_TYPE_CLASS, List.of("org.meveo.model.billing.Invoice"));

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "type(a) in (:typeClass) Param name:typeClass value:[class org.meveo.model.billing.Invoice]");
    }

    @Test
    public void test_search_att_type_class_class() {
        filters.put("eq " + SEARCH_ATTR_TYPE_CLASS, Invoice.class);

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where type(a) = :typeClass Param name:typeClass value:class org.meveo.model.billing.Invoice");
    }

    @Test
    public void test_search_att_type_class_string() {
        filters.put("eq " + SEARCH_ATTR_TYPE_CLASS, "org.meveo.model.billing.Invoice");

        assertThat(getQuery()).isEqualTo("" +
                "select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where type(a) = :typeClass Param name:typeClass value:class org.meveo.model.billing.Invoice");
    }

    @Test
    public void test_from_range() {
        filters.put("fromRange fromRangeField", 1);

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "a.fromRangeField >= :a_fromRangeField " +
                "Param name:a_fromRangeField value:1");
    }

    @Test
    public void test_to_range() {
        filters.put("toRange toRangeField", 1);

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "a.toRangeField < :a_toRangeField " +
                "Param name:a_toRangeField value:1");
    }

    @Test
    public void test_to_range_inclusive() {
        filters.put("toRangeInclusive toRangeInclusiveField", 1);

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "a.toRangeInclusiveField <= :a_toRangeInclusiveField " +
                "Param name:a_toRangeInclusiveField value:1");
    }

    @Test
    public void test_to_range_optional() {
        filters.put("toOptionalRange toRangeInclusiveField", 1);

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "a.toRangeInclusiveField IS NULL " +
                "or (a.toRangeInclusiveField < :a_toRangeInclusiveField)" +
                ") " +
                "Param name:a_toRangeInclusiveField value:1");
    }

    @Test
    public void test_to_optional_range_inclusive() {
        filters.put("toOptionalRangeInclusive toOptionalRangeInclusiveField", 1);

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "a.toOptionalRangeInclusiveField IS NULL " +
                "or (a.toOptionalRangeInclusiveField <= :a_toOptionalRangeInclusiveField)" +
                ") " +
                "Param name:a_toOptionalRangeInclusiveField value:1");
    }

    @Test
    public void test_list() {
        filters.put("list listField1", 1);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                ":listField1 in elements(listField1) " +
                "Param name:listField1 value:1");
    }

    @Test
    public void inner_join_for_is_equal_requests() {
        QueryBuilder query = new QueryBuilder(Invoice.class, "I", List.of("billingAccount.id"));
        query.addValueIsEqualToField("a.b.c", "value", false, true);
        assertThat(query.toString()).contains("inner join I.a a_", "inner join a_");

        System.out.println(query);
    }

    @Test
    public void inner_join_for_is_list_requests() {
        QueryBuilder query = new QueryBuilder(Invoice.class, "I", List.of("billingAccount.id"));
        query.addListFilters("a.b.c", "Value");
        assertThat(query.toString()).contains("inner join I.a a_", "inner join a_");
    }

    @Test
    public void test_in_list() {
        filters.put("inList invoiceAgregates", List.of("hello", "test"));
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where  " +
                "exists " +
                "(" +
                    "select invoiceAgregatesItemAlias from org.meveo.model.billing.Invoice invoiceAgregatesItemAlias,IN (invoiceAgregatesItemAlias.invoiceAgregates) as invoiceAgregatesItem where" +
                        " invoiceAgregatesItemAlias=a and invoiceAgregatesItem IN (:invoiceAgregates)" +
                ") " +
                "Param name:invoiceAgregates value:[hello, test]");
    }

    @Test
    public void test_not_in_list_json() {
        filters.put("not-inList " + FROM_JSON_FUNCTION + "jsonField)", List.of("hello", "test"));
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "lower(FromJson(a.cfValues,jsonField)) NOT  IN (:FromJson_a_cfValues_jsonField_) Param name:FromJson_a_cfValues_jsonField_ value:[hello, test]");
    }

    @Test
    public void test_not_in_list_collection() {
        filters.put("not-inList invoiceAgregates", List.of("hello", "test"));
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where  " +
                "exists " +
                "(" +
                    "select invoiceAgregatesItemAlias from org.meveo.model.billing.Invoice invoiceAgregatesItemAlias," +
                        "IN (invoiceAgregatesItemAlias.invoiceAgregates) as invoiceAgregatesItem where invoiceAgregatesItemAlias=a " +
                        "and invoiceAgregatesItem NOT  IN (:invoiceAgregates)" +
                ") Param name:invoiceAgregates value:[hello, test]");
    }

    @Test
    public void test_min_max_range() {
        filters.put("minmaxRange minmaxRangeField1 minmaxRangeField2", 3);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "a.minmaxRangeField1<=:a_minmaxRangeField1 and a.minmaxRangeField2 > :a_minmaxRangeField2 " +
                "Param name:a_minmaxRangeField1 value:3 Param name:a_minmaxRangeField2 value:3");
    }

    @Test
    public void test_min_max_range_inclusive() {
        filters.put("minmaxRangeInclusive minmaxRangeInclusiveField1 minmaxRangeInclusiveField2", 3);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "a.minmaxRangeInclusiveField1<=:a_minmaxRangeInclusiveField1 and a.minmaxRangeInclusiveField2 >= :a_minmaxRangeInclusiveField2 " +
                "Param name:a_minmaxRangeInclusiveField2 value:3 Param name:a_minmaxRangeInclusiveField1 value:3");
    }

    @Test
    public void test_min_max_range_optional() {
        filters.put("minmaxOptionalRange minmaxOptionalRangeField1 minmaxOptionalRangeField2", 3);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "(a.minmaxOptionalRangeField1 IS NULL and a.minmaxOptionalRangeField2 IS NULL) " +
                "or (a.minmaxOptionalRangeField1<=:a_minmaxOptionalRangeField1 and :a_minmaxOptionalRangeField1<a.minmaxOptionalRangeField2) " +
                "or (a.minmaxOptionalRangeField1<=:a_minmaxOptionalRangeField1 and a.minmaxOptionalRangeField2 IS NULL) " +
                "or (a.minmaxOptionalRangeField1 IS NULL and :a_minmaxOptionalRangeField1<a.minmaxOptionalRangeField2)" +
                ") " +
                "Param name:a_minmaxOptionalRangeField1 value:3");
    }

    @Test
    public void test_min_max_range_optional_inclusive() {
        filters.put("minmaxOptionalRangeInclusive minmaxOptionalRangeInclusiveField1 minmaxOptionalRangeInclusiveField2", 3);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "(a.minmaxOptionalRangeInclusiveField1 IS NULL and a.minmaxOptionalRangeInclusiveField2 IS NULL) " +
                "or (a.minmaxOptionalRangeInclusiveField1<=:a_minmaxOptionalRangeInclusiveField1 and :a_minmaxOptionalRangeInclusiveField1<=a.minmaxOptionalRangeInclusiveField2) or (a.minmaxOptionalRangeInclusiveField1<=:a_minmaxOptionalRangeInclusiveField1 and a.minmaxOptionalRangeInclusiveField2 IS NULL) " +
                "or (a.minmaxOptionalRangeInclusiveField1 IS NULL and :a_minmaxOptionalRangeInclusiveField1<=a.minmaxOptionalRangeInclusiveField2)" +
                ") " +
                "Param name:a_minmaxOptionalRangeInclusiveField1 value:3");
    }

    @Test
    public void test_from_range_optional() {
        filters.put("fromOptionalRange fromOptionalRangeField", 1);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "a.fromOptionalRangeField IS NULL " +
                "or (a.fromOptionalRangeField >= :a_fromOptionalRangeField)" +
                ") " +
                "Param name:a_fromOptionalRangeField value:1");


    }

    @Test
    public void test_overlap_optional_range() {
        filters.put("overlapOptionalRange overlapOptionalRangeFiled1 overlapOptionalRangeField2", List.of(10, 20));
        String query = getQuery();
        assertThat(query).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "( a.overlapOptionalRangeFiled1 IS NULL and a.overlapOptionalRangeField2 IS NULL) " +
                "or  ( a.overlapOptionalRangeFiled1 IS NULL and :a_overlapOptionalRangeFiled1<a.overlapOptionalRangeField2) " +
                "or (a.overlapOptionalRangeField2 IS NULL and a.overlapOptionalRangeFiled1<:a_overlapOptionalRangeField2) " +
                "or (a.overlapOptionalRangeFiled1 IS NOT NULL and a.overlapOptionalRangeField2 IS NOT NULL " +
                "and (" +
                "(a.overlapOptionalRangeFiled1<=:a_overlapOptionalRangeFiled1 and :a_overlapOptionalRangeFiled1<a.overlapOptionalRangeField2) " +
                "or (:a_overlapOptionalRangeFiled1<=a.overlapOptionalRangeFiled1 and a.overlapOptionalRangeFiled1<:a_overlapOptionalRangeField2)" +
                ")" +
                ")" +
                ") Param name:a_overlapOptionalRangeFiled1 value:10 Param name:a_overlapOptionalRangeField2 value:20");
    }

    @Test
    public void test_overlap_optional_range_inclusive() {
        filters.put("overlapOptionalRangeInclusive overlapOptionalRangeFiled1 overlapOptionalRangeField2", List.of(10, 20));
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "( a.overlapOptionalRangeFiled1 IS NULL and a.overlapOptionalRangeField2 IS NULL) " +
                "or  ( a.overlapOptionalRangeFiled1 IS NULL and :a_overlapOptionalRangeFiled1<=a.overlapOptionalRangeField2) " +
                "or (a.overlapOptionalRangeField2 IS NULL and a.overlapOptionalRangeFiled1<=:a_overlapOptionalRangeField2) " +
                "or (a.overlapOptionalRangeFiled1 IS NOT NULL and a.overlapOptionalRangeField2 IS NOT NULL " +
                "and (" +
                "(a.overlapOptionalRangeFiled1<=:a_overlapOptionalRangeFiled1 and :a_overlapOptionalRangeFiled1<=a.overlapOptionalRangeField2) " +
                "or (:a_overlapOptionalRangeFiled1<=a.overlapOptionalRangeFiled1 and a.overlapOptionalRangeFiled1<=:a_overlapOptionalRangeField2)" +
                ")" +
                ")" +
                ") Param name:a_overlapOptionalRangeFiled1 value:10 Param name:a_overlapOptionalRangeField2 value:20");
    }

    @Test
    public void test_like_criterias() {
        filters.put("likeCriterias likeCriteriasField1 likeCriteriasField2 likeCriteriasField3", "%abc");
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "lower(a.likeCriteriasField1) = :a_likeCriteriasField1 " +
                "or lower(a.likeCriteriasField2) = :a_likeCriteriasField2 " +
                "or lower(a.likeCriteriasField3) = :a_likeCriteriasField3" +
                ") " +
                "Param name:a_likeCriteriasField3 value:%abc Param name:a_likeCriteriasField2 value:%abc Param name:a_likeCriteriasField1 value:%abc");
    }

    @Test
    public void test_search_wild_card() {
        filters.put(PersistenceService.SEARCH_WILDCARD_OR + " wildcardOrField1 wildcardOrField2 wildcardOrField3", "*abc");
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "a.wildcardOrField1 like '%*abc%' " +
                "or a.wildcardOrField2 like '%*abc%' " +
                "or a.wildcardOrField3 like '%*abc%')");
    }

    @Test
    public void test_search_wild_card_ignore_case() {
        filters.put(SEARCH_WILDCARD_OR_IGNORE_CAS + " wildcardOrField1 wildcardOrField2 wildcardOrField3", "*abc");
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "lower(a.wildcardOrField1) like '%*abc%' " +
                "or lower(a.wildcardOrField2) like '%*abc%' " +
                "or lower(a.wildcardOrField3) like '%*abc%')");
    }

    @Test
    public void test_search_SQL() {
        String[] data = {"select  a.selectField1 from tableName a  where a.selectField1 = a", "selectField2"};
        filters.put(SEARCH_SQL, data);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "select  a.selectField1 from tableName a  where a.selectField1 = a");
    }

    @Test
    public void test_search_by_is_null_json() {
        filters.put("key" + FROM_JSON_FUNCTION + "jsonField)", SEARCH_IS_NULL);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where keyFromJson(a.cfValues,jsonField) is null ");
    }

    @Test
    public void test_search_by_is_null_collection() {
        filters.put("key invoiceAgregates", SEARCH_IS_NULL);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where a.invoiceAgregates is empty ");
    }

    @Test
    public void test_search_by_is_not_null() {
        filters.put("key" + FROM_JSON_FUNCTION + "jsonField)", SEARCH_IS_NOT_NULL);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where keyFromJson(a.cfValues,jsonField) is not null ");
    }

    @Test
    public void test_search_by_is_not_null_collections() {
        filters.put("key invoiceAgregates", SEARCH_IS_NOT_NULL);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where a.invoiceAgregates is not empty ");
    }

    @Test
    public void test_not_equal() {
        filters.put("ne notEqualField", 1);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where notEqualField != :notEqualField Param name:notEqualField value:1");
    }

    @Test
    public void test_optional_not_equal() {
        filters.put("neOptional notEqualField", 1);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "(" +
                "notEqualField IS NULL " +
                "or (notEqualField != :notEqualField)" +
                ") Param name:notEqualField value:1");
    }

    @Test
    public void test_base_entity() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        filters.put("eq", invoice);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where a.eq = :a_eq Param name:a_eq value:Invoice[id=1, invoiceNumber=null, invoiceType=null]");
    }

    @Test
    public void test_auditable_hash_map() {
        Map<Object, Object> map = new HashMap<>();
        map.put(1, Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        filters.put("eq auditable", map);
        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where (a.auditable.1>=:startaauditable1 and a.auditable.1<:endaauditable1) Param name:startaauditable1 value:Wed Jan 01 00:00:00 WET 2020 Param name:endaauditable1 value:Thu Jan 02 00:00:00 WET 2020");
    }

    @Test
    public void test() {
        filters.put("defaultEqualFilter", 1);
        filters.put("eq equalFilter", 2);
        filters.put("fromRange fromRangeFilter", 10);
        filters.put("toRange toRangeFilter", 10);
        filters.put("list listFilter", 10);
        filters.put("minmaxRange minmaxRangeFilter1 minmaxRangeFilter2", 10);
        filters.put("overlapOptionalRange overlapOptionalRangeFilter1 overlapOptionalRangeFilter2", List.of(10, 15));
        filters.put("likeCriterias likeCriteriasFilter", "likeWord");
        filters.put(SEARCH_WILDCARD_OR + " wildcardOrFilter", "wildCard*");
        filters.put(SEARCH_WILDCARD_OR_IGNORE_CAS + " wildcardOrIgnoreCaseFilter", "wildCardIngoreCase*");

        assertThat(getQuery()).isEqualTo("select a from org.meveo.model.billing.Invoice a left join fetch a.fetchField where " +
                "defaultEqualFilter = :defaultEqualFilter " +
                "and (" +
                "( a.overlapOptionalRangeFilter1 IS NULL and a.overlapOptionalRangeFilter2 IS NULL) " +
                "or  ( a.overlapOptionalRangeFilter1 IS NULL and :a_overlapOptionalRangeFilter1<a.overlapOptionalRangeFilter2) " +
                "or (a.overlapOptionalRangeFilter2 IS NULL and a.overlapOptionalRangeFilter1<:a_overlapOptionalRangeFilter2) " +
                "or (a.overlapOptionalRangeFilter1 IS NOT NULL and a.overlapOptionalRangeFilter2 IS NOT NULL " +
                "and (" +
                "(a.overlapOptionalRangeFilter1<=:a_overlapOptionalRangeFilter1 and :a_overlapOptionalRangeFilter1<a.overlapOptionalRangeFilter2) " +
                "or (:a_overlapOptionalRangeFilter1<=a.overlapOptionalRangeFilter1 and a.overlapOptionalRangeFilter1<:a_overlapOptionalRangeFilter2)" +
                ")" +
                ")" +
                ") " +
                "and (lower(a.likeCriteriasFilter) = :a_likeCriteriasFilter) " +
                "and (lower(a.wildcardOrIgnoreCaseFilter) like '%wildcardingorecase*%') " +
                "and (a.wildcardOrFilter like '%wildCard*%') " +
                "and :listFilter in elements(listFilter) " +
                "and a.toRangeFilter < :a_toRangeFilter " +
                "and a.minmaxRangeFilter1<=:a_minmaxRangeFilter1 " +
                "and a.minmaxRangeFilter2 > :a_minmaxRangeFilter2 " +
                "and a.fromRangeFilter >= :a_fromRangeFilter " +
                "and equalFilter = :equalFilter " +
                "Param name:defaultEqualFilter value:1 Param name:equalFilter value:2 Param name:listFilter value:10 Param name:a_toRangeFilter value:10 Param name:a_minmaxRangeFilter2 value:10 Param name:a_minmaxRangeFilter1 value:10 Param name:a_likeCriteriasFilter value:likeword Param name:a_fromRangeFilter value:10 Param name:a_overlapOptionalRangeFilter1 value:10 Param name:a_overlapOptionalRangeFilter2 value:15");
    }

    private String getQuery() {
        return persistenceService.getQuery(new PaginationConfiguration(10, 40, filters, "text", List.of("fetchField"), "fetchField", "desc")).toString();
    }
}
