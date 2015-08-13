package org.meveo.service.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.DiscriminatorValue;

import org.junit.Test;
import org.meveo.model.billing.Country;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;
import org.meveo.model.filter.Projector;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

/**
 * @author Edward P. Legaspi
 **/
public class FilterXmlGenerator {

	public static void main(String[] args) {
		FilterXmlGenerator fg = new FilterXmlGenerator();
		// fg.filter1();
		String result = fg.generate();
		System.out.println(result);
		// fg.degenerate(result);
		// fg.getLinkedListFields();
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
			System.out.println(f.getName());
		}
	}

	public void degenerate(String input) {
		try {
			XStream xStream = getXStream();
			Filter filter = (Filter) xStream.fromXML(input);
			System.out.println(filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String generate() {
		AndCompositeFilterCondition andCompositeFilterCondition = new AndCompositeFilterCondition();
		andCompositeFilterCondition.setFilterConditionType(AndCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		List<FilterCondition> andFilterConditions = new ArrayList<>();

		NativeFilterCondition nativeFilterCondition = new NativeFilterCondition();
		nativeFilterCondition.setFilterConditionType(NativeFilterCondition.class
				.getAnnotation(DiscriminatorValue.class).value());
		nativeFilterCondition.setJpql("c.countryCode like '%A%'");
		andFilterConditions.add(nativeFilterCondition);

		/* OR */
		OrCompositeFilterCondition orCompositeFilterCondition = new OrCompositeFilterCondition();
		orCompositeFilterCondition.setFilterConditionType(OrCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		List<FilterCondition> orFilterConditions = new ArrayList<>();

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

		try {
			XStream xStream = getXStream();
			return xStream.toXML(filter);
		} catch (Exception e) {
			e.printStackTrace();
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
		filterSelector6.setDisplayFields(new ArrayList<>(Arrays.asList("currencyCode", "descriptionEn")));

		filterSelectors.add(filterSelector6);

		Filter filter = new Filter();
		filter.setCode("FILTER_1");
		filter.setSecondarySelectors(filterSelectors);
		filter.setPrimarySelector(filterSelector1);

		try {
			XStream xStream = getXStream();
			System.out.println(xStream.toXML(filter));
		} catch (Exception e) {
			e.printStackTrace();
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