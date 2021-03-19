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

package org.meveo.service.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;

import org.junit.Test;
import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;
import org.meveo.model.filter.Projector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

/**
 * @author Edward P. Legaspi
 **/
public class FilterXmlGenerator {
	
	private static final Logger log = LoggerFactory.getLogger(FilterXmlGenerator.class);

	public static void main(String[] args) {
		try {
			FilterXmlGenerator fg = new FilterXmlGenerator();
			// fg.filter1();
			// String result = fg.generate();
			

			// country
			String result = fg.generate2();
			log.info(result);

			// fg.degenerate(result);
			// fg.getLinkedListFields();
		} catch (Exception e) {

		}
	}

	public String generate2() throws FilterException {
		AndCompositeFilterCondition andCompositeFilterCondition = new AndCompositeFilterCondition();
		andCompositeFilterCondition.setFilterConditionType(AndCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		Set<FilterCondition> andFilterConditions = new HashSet<>();

		PrimitiveFilterCondition primitiveFilterCondition = new PrimitiveFilterCondition();
		primitiveFilterCondition.setFilterConditionType(PrimitiveFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		primitiveFilterCondition.setFieldName("startRatingDate");
		primitiveFilterCondition.setOperator("=");
		primitiveFilterCondition.setOperand("date:2015-08-01");

		andFilterConditions.add(primitiveFilterCondition);

		andCompositeFilterCondition.setFilterConditions(andFilterConditions);

		FilterSelector filterSelector1 = new FilterSelector();
		filterSelector1.setTargetEntity(PricePlanMatrix.class.getName());
		filterSelector1.setAlias("p");
		filterSelector1.setDisplayFields(new ArrayList<>(Arrays.asList("code", "eventCode")));

		Filter filter = new Filter();
		filter.setFilterCondition(andCompositeFilterCondition);
		filter.setPrimarySelector(filterSelector1);

		FilteredQueryBuilder fq = new FilteredQueryBuilder(filter);
		

		try {
			XStream xStream = getXStream();
			return xStream.toXML(filter);
		} catch (Exception e) {
			log.error("error = {}", e);
		}

		return "";
	}

	public String generate1() throws FilterException {
		AndCompositeFilterCondition andCompositeFilterCondition = new AndCompositeFilterCondition();
		andCompositeFilterCondition.setFilterConditionType(AndCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		Set<FilterCondition> andFilterConditions = new HashSet<>();

		PrimitiveFilterCondition primitiveFilterCondition = new PrimitiveFilterCondition();
		primitiveFilterCondition.setFilterConditionType(PrimitiveFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		primitiveFilterCondition.setFieldName("status");
		primitiveFilterCondition.setOperator("=");
		primitiveFilterCondition.setOperand("enum:org.meveo.model.billing.SubscriptionStatusEnum.ACTIVE");

		andFilterConditions.add(primitiveFilterCondition);

		andCompositeFilterCondition.setFilterConditions(andFilterConditions);

		OrderCondition orderCondition = new OrderCondition();
		orderCondition.setAscending(false);
		orderCondition.setFieldNames(new ArrayList<>(Arrays.asList("subscriptionDate")));

		FilterSelector filterSelector1 = new FilterSelector();
		filterSelector1.setTargetEntity(Subscription.class.getName());
		filterSelector1.setAlias("c");
		filterSelector1.setDisplayFields(new ArrayList<>(Arrays.asList("offer", "status")));

		Filter filter = new Filter();
		filter.setFilterCondition(andCompositeFilterCondition);
		filter.setPrimarySelector(filterSelector1);
		// filter.setSecondarySelectors(filterSelectors);
		filter.setOrderCondition(orderCondition);

		FilteredQueryBuilder fq = new FilteredQueryBuilder(filter);
		

		try {
			XStream xStream = getXStream();
			return xStream.toXML(filter);
		} catch (Exception e) {
			log.error("error = {}", e);
		}

		return "";
	}

	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			fields = getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	@Test
	public void getLinkedListFields() {
		List<Field> fields = new ArrayList<>();
		getAllFields(fields, Country.class);
		for (Field f : fields) {
			log.info(f.getName());
		}
	}

	public void degenerate(String input) {
		try {
			XStream xStream = getXStream();
			Filter filter = (Filter) xStream.fromXML(input);
		} catch (Exception e) {
			log.error("error = {}", e);
		}
	}

	public String generate() throws FilterException {
		AndCompositeFilterCondition andCompositeFilterCondition = new AndCompositeFilterCondition();
		andCompositeFilterCondition.setFilterConditionType(AndCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		Set<FilterCondition> andFilterConditions = new HashSet<>();

		NativeFilterCondition nativeFilterCondition = new NativeFilterCondition();
		nativeFilterCondition.setFilterConditionType(NativeFilterCondition.class
				.getAnnotation(DiscriminatorValue.class).value());
		nativeFilterCondition.setJpql("c.countryCode like '%A%'");
		andFilterConditions.add(nativeFilterCondition);

		/* OR */
		OrCompositeFilterCondition orCompositeFilterCondition = new OrCompositeFilterCondition();
		orCompositeFilterCondition.setFilterConditionType(OrCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		Set<FilterCondition> orFilterConditions = new HashSet<>();

		NativeFilterCondition nativeFilterCondition2 = new NativeFilterCondition();
		nativeFilterCondition2.setFilterConditionType(NativeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		nativeFilterCondition2.setJpql("c.countryCode like 'B%'");
		orFilterConditions.add(nativeFilterCondition2);

		PrimitiveFilterCondition primitiveFilterCondition = new PrimitiveFilterCondition();
		primitiveFilterCondition.setFilterConditionType(PrimitiveFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		primitiveFilterCondition.setFieldName("c.countryCode");
		primitiveFilterCondition.setOperator("like");
		primitiveFilterCondition.setOperand("C%");
		orFilterConditions.add(primitiveFilterCondition);

		PrimitiveFilterCondition primitiveFilterCondition2 = new PrimitiveFilterCondition();
		primitiveFilterCondition2.setFilterConditionType(PrimitiveFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		primitiveFilterCondition2.setFieldName("c.id");
		primitiveFilterCondition2.setOperator(">");
		primitiveFilterCondition2.setOperand("100");
		orFilterConditions.add(primitiveFilterCondition2);
		orCompositeFilterCondition.setFilterConditions(orFilterConditions);
		/* OR */

		andFilterConditions.add(orCompositeFilterCondition);
		andCompositeFilterCondition.setFilterConditions(andFilterConditions);

		OrderCondition orderCondition = new OrderCondition();
		orderCondition.setAscending(false);
		orderCondition.setFieldNames(new ArrayList<>(Arrays.asList("countryCode")));

		FilterSelector filterSelector1 = new FilterSelector();
		filterSelector1.setTargetEntity("org.meveo.model.billing.Country");
		filterSelector1.setAlias("c");
		filterSelector1.setDisplayFields(new ArrayList<>(Arrays.asList("currency", "language")));

		Filter filter = new Filter();
		filter.setFilterCondition(andCompositeFilterCondition);
		filter.setPrimarySelector(filterSelector1);
		// filter.setSecondarySelectors(filterSelectors);
		filter.setOrderCondition(orderCondition);

		FilteredQueryBuilder fq = new FilteredQueryBuilder(filter);
		

		try {
			XStream xStream = getXStream();
			return xStream.toXML(filter);
		} catch (Exception e) {
			log.error("error = {}", e);
		}

		return "";
	}

	public void filter1() {
		List<FilterSelector> filterSelectors = new ArrayList<>();

		FilterSelector filterSelector1 = new FilterSelector();
		filterSelector1.setTargetEntity("org.meveo.model.billing.BillingAccount");
		filterSelector1.setAlias("ba");
		filterSelector1.setExportFields(new ArrayList<>(Arrays.asList("customFields", "name", "tradingLanguage",
				"tradingCountry")));

		filterSelectors.add(filterSelector1);

		FilterSelector filterSelector2 = new FilterSelector();
		filterSelector2.setTargetEntity("org.meveo.model.crm.CustomFieldPeriod");
		filterSelector2.setAlias("cfp");

		filterSelectors.add(filterSelector2);

		FilterSelector filterSelector3 = new FilterSelector();
		filterSelector3.setTargetEntity("org.meveo.model.billing.TradingLanguage");
		filterSelector3.setAlias("tl");
		filterSelector3.setDisplayFields(new ArrayList<>(Arrays.asList("prDescription")));

		filterSelectors.add(filterSelector3);

		FilterSelector filterSelector4 = new FilterSelector();
		filterSelector4.setTargetEntity("org.meveo.model.billing.TradingCountry");
		filterSelector4.setAlias("tc");
		filterSelector4.setDisplayFields(new ArrayList<>(Arrays.asList("country", "prDescription")));

		filterSelectors.add(filterSelector4);

		FilterSelector filterSelector5 = new FilterSelector();
		filterSelector5.setTargetEntity("org.meveo.model.billing.Country");
		filterSelector5.setAlias("c");
		filterSelector5.setDisplayFields(new ArrayList<>(Arrays.asList("currency", "countryCode")));

		filterSelectors.add(filterSelector5);

		FilterSelector filterSelector6 = new FilterSelector();
		filterSelector6.setTargetEntity("org.meveo.model.admin.Currency");
		filterSelector6.setAlias("curr");
		filterSelector6.setDisplayFields(new ArrayList<>(Arrays.asList("currencyCode", "description")));

		filterSelectors.add(filterSelector6);

		Filter filter = new Filter();
		filter.setCode("FILTER_1");
		filter.setSecondarySelectors(filterSelectors);
		filter.setPrimarySelector(filterSelector1);

		try {
			XStream xStream = getXStream();
			log.info(xStream.toXML(filter));
		} catch (Exception e) {
			log.error("error = {}", e);
		}
	}

	private XStream getXStream() {
		XStream xStream = new XStream();
		// rename the selector field
		xStream.alias("andCompositeFilterCondition", AndCompositeFilterCondition.class);
		xStream.alias("filter", Filter.class);
		xStream.alias("filterCondition", FilterCondition.class);
		xStream.alias("filterSelector", FilterSelector.class);
		xStream.alias("nativeFilterCondition", NativeFilterCondition.class);
		xStream.alias("orCompositeFilterCondition", OrCompositeFilterCondition.class);
		xStream.alias("orderCondition", OrderCondition.class);
		xStream.alias("primitiveFilterCondition", PrimitiveFilterCondition.class);
		xStream.alias("projector", Projector.class);

		xStream.setMode(XStream.NO_REFERENCES);

		// rename String to field, arrayList must be specify in the fieldName
		// setter
		ClassAliasingMapper orderConditionFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		orderConditionFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(OrderCondition.class, "fieldNames", new CollectionConverter(
				orderConditionFieldMapper));

		// rename projector exportField
		ClassAliasingMapper projectorExportFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorExportFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(FilterSelector.class, "exportFields", new CollectionConverter(
				projectorExportFieldMapper));

		// rename projector displayField
		ClassAliasingMapper projectorDisplayFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorDisplayFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(FilterSelector.class, "displayFields", new CollectionConverter(
				projectorDisplayFieldMapper));

		// rename projector ignore field
		ClassAliasingMapper projectorIgnoreFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorIgnoreFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(FilterSelector.class, "ignoreIfNotFoundForeignKeys", new CollectionConverter(
				projectorIgnoreFieldMapper));

		return xStream;
	}

}