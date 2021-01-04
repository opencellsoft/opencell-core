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

package org.meveo.service.base;

import org.junit.Before;
import org.junit.Test;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.billing.Invoice;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.service.base.PersistenceService.*;

public class NativePersistenceServiceTest {

    private NativePersistenceService sut = new NativePersistenceServiceMock();
    private HashMap<String, Object> filters;

    @Before
    public void setUp() throws Exception {
        filters = new HashMap<>();
    }

    @Test
    public void should_request_only_select_id_and_order_by_id_when_there_is_no_field_present() {
        //Given
        String tableName = "TABLE_1";
        StringBuffer findIdFields = new StringBuffer();
        //When
        StringBuffer request = sut.buildSqlInsertionRequest(tableName, findIdFields);
        //Then
        assertThat(request.toString()).isEqualTo("select id from TABLE_1 order by id desc");
    }

    @Test
    public void test_from_range() {
        filters.put("fromRange fromRangeField", 1);

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "a.fromRangeField >= :a_fromRangeField " +
                "Param name:a_fromRangeField value:1");
    }

    @Test
    public void test_to_range() {
        filters.put("toRange toRangeField", 1);

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "a.toRangeField < :a_toRangeField " +
                "Param name:a_toRangeField value:1");
    }

    @Test
    public void test_to_range_inclusive() {
        filters.put("toRangeInclusive toRangeInclusiveField", 1);

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "a.toRangeInclusiveField <= :a_toRangeInclusiveField " +
                "Param name:a_toRangeInclusiveField value:1");
    }

    @Test
    public void test_to_range_optional() {
        filters.put("toOptionalRange toRangeInclusiveField", 1);

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "(" +
                    "a.toRangeInclusiveField IS NULL " +
                    "or (a.toRangeInclusiveField < :a_toRangeInclusiveField)" +
                ") " +
                "Param name:a_toRangeInclusiveField value:1");
    }

    @Test
    public void test_to_optional_range_inclusive() {
        filters.put("toOptionalRangeInclusive toOptionalRangeInclusiveField", 1);

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "(" +
                    "a.toOptionalRangeInclusiveField IS NULL " +
                    "or (a.toOptionalRangeInclusiveField <= :a_toOptionalRangeInclusiveField)" +
                ") " +
                "Param name:a_toOptionalRangeInclusiveField value:1");
    }

    @Test
    public void test_list() {
        filters.put("list listField1", 1);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                ":listField1 in elements(listField1) " +
                "Param name:listField1 value:1");
    }

    @Test
    public void test_in_list() {
        filters.put("inList inListField1", List.of("hello", "test"));
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "lower(a.inListField1) IN (:a_inListField1) " +
                "Param name:a_inListField1 value:[hello, test]");
    }

    @Test
    public void test_not_in_list() {
        filters.put("not-inList notInListField1", List.of("hello", "test"));
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "lower(a.notInListField1) NOT  IN (:a_notInListField1) " +
                "Param name:a_notInListField1 value:[hello, test]");
    }

    @Test
    public void test_min_max_range() {
        filters.put("minmaxRange minmaxRangeField1 minmaxRangeField2", 3);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "a.minmaxRangeField1<=:a_minmaxRangeField1 and a.minmaxRangeField2 > :a_minmaxRangeField2 " +
                "Param name:a_minmaxRangeField1 value:3 Param name:a_minmaxRangeField2 value:3");
    }

    @Test
    public void test_min_max_range_inclusive() {
        filters.put("minmaxRangeInclusive minmaxRangeInclusiveField1 minmaxRangeInclusiveField2", 3);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "a.minmaxRangeInclusiveField1<=:a_minmaxRangeInclusiveField1 and a.minmaxRangeInclusiveField2 >= :a_minmaxRangeInclusiveField2 " +
                "Param name:a_minmaxRangeInclusiveField2 value:3 Param name:a_minmaxRangeInclusiveField1 value:3");
    }

    @Test
    public void test_min_max_range_optional() {
        filters.put("minmaxOptionalRange minmaxOptionalRangeField1 minmaxOptionalRangeField2", 3);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(query).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "(" +
                    "a.wildcardOrField1 like '%*abc%' " +
                    "or a.wildcardOrField2 like '%*abc%' " +
                    "or a.wildcardOrField3 like '%*abc%')");
    }

    @Test
    public void test_search_wild_card_ignore_case() {
        filters.put(SEARCH_WILDCARD_OR_IGNORE_CAS + " wildcardOrField1 wildcardOrField2 wildcardOrField3", "*abc");
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "(" +
                    "lower(a.wildcardOrField1) like '%*abc%' " +
                    "or lower(a.wildcardOrField2) like '%*abc%' " +
                    "or lower(a.wildcardOrField3) like '%*abc%')");
    }

    @Test
    public void test_search_SQL() {
        String[] data = {"select a.selectField1 from tableName a  where a.selectField1 = a", "selectField2"};
        filters.put(SEARCH_SQL, data);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
                "select a.selectField1 from tableName a  where a.selectField1 = a");
    }

    @Test
    public void test_search_by_is_null() {
        filters.put("key", SEARCH_IS_NULL);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where a.key is null ");
    }

    @Test
    public void test_search_by_is_not_null() {
        filters.put("key", SEARCH_IS_NOT_NULL);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where a.key is not null ");
    }

    @Test
    public void test_not_equal() {
        filters.put("ne notEqualField", 1);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where notEqualField != :notEqualField Param name:notEqualField value:1");
    }

    @Test
    public void test_optional_not_equal() {
        filters.put("neOptional notEqualField", 1);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a ");
    }

    @Test
    public void test_search_att_type_class_list_class() {
        filters.put("eq " + SEARCH_ATTR_TYPE_CLASS, List.of("org.meveo.model.billing.Invoice"));

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where type_class  in :type_class Param name:type_class value:[org.meveo.model.billing.Invoice]");
    }

    @Test
    public void test_search_att_type_class_class() {
        filters.put("eq " + SEARCH_ATTR_TYPE_CLASS, Invoice.class);

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a ");
    }

    @Test
    public void test_search_att_type_class_string() {
        filters.put("eq " + SEARCH_ATTR_TYPE_CLASS, "org.meveo.model.billing.Invoice");

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where lower(type_class) = :type_class Param name:type_class value:org.meveo.model.billing.invoice");
    }

    @Test
    public void test_auditable_hash_map() {
        Map<Object, Object> map = new HashMap<>();
        map.put(1, Date.from(LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        filters.put("eq auditable", map);
        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a ");
    }


    @Test
    public void test() {
        filters.put("defaultEqualFilter", 1);
        filters.put("eq equalFilter", 1);
        filters.put("fromRange fromRangeFilter", 10);
        filters.put("toRange toRangeFilter", 10);
        filters.put("list listFilter", 10);
        filters.put("inList inListFilter", 10);
        filters.put("minmaxRange minmaxRangeFilter1 minmaxRangeFilter2", 10);
        filters.put("overlapOptionalRange overlapOptionalRangeFilter1 overlapOptionalRangeFilter2", List.of(10, 15));
        filters.put("likeCriterias likeCriteriasFilter", "likeWord");
        filters.put(SEARCH_WILDCARD_OR + " wildcardOrFilter", "wildCard*");
        filters.put(SEARCH_WILDCARD_OR_IGNORE_CAS + " wildcardOrIgnoreCaseFilter", "wildCardIngoreCase*");

        assertThat(getQuery()).isEqualTo("select a.selectField from tableName a  where " +
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
                "Param name:defaultEqualFilter value:1 Param name:equalFilter value:1 Param name:listFilter value:10 Param name:a_toRangeFilter value:10 Param name:a_minmaxRangeFilter2 value:10 Param name:a_minmaxRangeFilter1 value:10 Param name:a_likeCriteriasFilter value:likeword Param name:a_fromRangeFilter value:10 Param name:a_overlapOptionalRangeFilter1 value:10 Param name:a_overlapOptionalRangeFilter2 value:15");
    }

    @Test
    public void test_aggregation_functions() {
        PaginationConfiguration configuration = new PaginationConfiguration(1, 10, filters, "text", List.of("field", "field2", "max(field3)"));

        //assertThat(queryWithAggFields(configuration)).isEqualTo("select a.field, a.field2, max(field3) from tableName a  GROUP BY  a.field, a.field2");
    }

    private String getQuery() {
        return sut.getQuery("tableName", new PaginationConfiguration(10, 40, filters, "text", List.of("selectField"), "selectField", "desc")).toString();
    }

    private String queryWithAggFields(PaginationConfiguration configuration) {
        return sut.getQuery("tableName", configuration).toString();
    }
}