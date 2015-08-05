package org.meveo.service.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.DiscriminatorValue;

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
		String result = fg.generate();
		System.out.println(result);
		fg.degenerate(result);
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
		nativeFilterCondition.setEl("#{ba.code eq 'BA1'}");
		nativeFilterCondition.setJpql("ba.code='BA1'");
		andFilterConditions.add(nativeFilterCondition);

		OrCompositeFilterCondition orCompositeFilterCondition = new OrCompositeFilterCondition();
		orCompositeFilterCondition.setFilterConditionType(OrCompositeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		List<FilterCondition> orFilterConditions = new ArrayList<>();

		NativeFilterCondition nativeFilterCondition2 = new NativeFilterCondition();
		nativeFilterCondition2.setFilterConditionType(NativeFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		nativeFilterCondition2.setEl("#{ba.code eq 'BA2'}");
		nativeFilterCondition2.setJpql("ba.code='BA2'");
		orFilterConditions.add(nativeFilterCondition2);

		PrimitiveFilterCondition primitiveFilterCondition = new PrimitiveFilterCondition();
		primitiveFilterCondition.setFilterConditionType(PrimitiveFilterCondition.class.getAnnotation(
				DiscriminatorValue.class).value());
		primitiveFilterCondition.setFieldName("ba.code");
		primitiveFilterCondition.setOperator("=");
		primitiveFilterCondition.setOperand("BA3");
		orFilterConditions.add(primitiveFilterCondition);
		orCompositeFilterCondition.setFilterConditions(orFilterConditions);

		andFilterConditions.add(orCompositeFilterCondition);
		andCompositeFilterCondition.setFilterConditions(andFilterConditions);

		OrderCondition orderCondition = new OrderCondition();
		orderCondition.setAscending(true);
		orderCondition.setFieldNames(new ArrayList<>(Arrays.asList("id", "code")));

		List<FilterSelector> filterSelectors = new ArrayList<>();

		FilterSelector filterSelector1 = new FilterSelector();
		filterSelector1.setTargetEntity("org.meveo.model.payments.CustomerAccount");
		filterSelector1.setAlias("ca");

		filterSelectors.add(filterSelector1);

		FilterSelector filterSelector2 = new FilterSelector();
		filterSelector2.setTargetEntity("org.meveo.model.payments.CustomerAccount");
		filterSelector2.setAlias("ca");

		filterSelectors.add(filterSelector2);

		Filter filter = new Filter();
		filter.setFilterCondition(andCompositeFilterCondition);
		filter.setOrderCondition(orderCondition);
		filter.setSecondarySelectors(filterSelectors);
		filter.setPrimarySelector(filterSelector1);

		try {
			XStream xStream = getXStream();
			return xStream.toXML(filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private XStream getXStream() {
		XStream xStream = new XStream();
		// rename the selector field
		xStream.alias("selector", FilterSelector.class);

		// rename String to field, arrayList must be specify in the fieldName
		// setter
		ClassAliasingMapper orderConditionFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		orderConditionFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(OrderCondition.class, "fieldNames", new CollectionConverter(
				orderConditionFieldMapper));

		// rename projector exportField
		ClassAliasingMapper projectorExportFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorExportFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(Projector.class, "exportFields", new CollectionConverter(
				projectorExportFieldMapper));

		// rename projector displayField
		ClassAliasingMapper projectorDisplayFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorDisplayFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(Projector.class, "displayFields", new CollectionConverter(
				projectorDisplayFieldMapper));

		// rename projector ignore field
		ClassAliasingMapper projectorIgnoreFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorIgnoreFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(Projector.class, "ignoreIfNotFoundForeignKeys", new CollectionConverter(
				projectorIgnoreFieldMapper));

		return xStream;
	}

}